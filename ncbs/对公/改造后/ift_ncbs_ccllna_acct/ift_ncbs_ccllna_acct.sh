#!/bin/bash
#***************************************************************
#系统名称： 税务管理系统
#脚本名称： ift_ncbs_ccllna_acct.sh
#脚本功能： 对公贷款账户主表（架构重构版）
#
#版本        日期       编写者    项目组/修改内容
#-------   -------     -------  ----------
#v1.0      2025-02-24  祝  刚    税务管理系统/新建
#v2.0      2025-08-25  刘扬      税务管理系统/Shell+Java架构重构
#****************************************************************

# 设置字符编码
export LANG=zh_CN.UTF-8

# 当前脚本目录
script_dir="$(dirname "$(readlink -f "$0")")"
# 表名
org_table_name="ccllna_acct"

# 源文件目录
source_dir="/cebtms/files/ift/ecas"
# 操作目录
work_dir="/cebtms/files/ift/work/${org_table_name}"
# 备份目录
bak_dir='/cebtms/files/bak/ift/ecas'

# 目标文件名
TARGET_FILE=tp_ncbs_${org_table_name}.txt

# 检查目标目录是否存在，如果不存在则创建
if [ ! -d "$work_dir" ]; then
    mkdir -p "$work_dir"
fi
if [ ! -d "$bak_dir" ]; then
    mkdir -p "$bak_dir"
fi

# 数据文件
# 获取今天的日期
today_with_dash=$(date -d "today" +%Y-%m-%d)
# 获取昨天的日期
yesterday=$(date -d "yesterday" +%Y%m%d)
yesterday_with_dash=$(date -d "yesterday" +%Y-%m-%d)

# 构建文件名模式
pattern="a_ncbs_${org_table_name}_${yesterday}_*.dat.gz.ok"

# 日志目录
DN_OUTPUT=`date +%Y%m%d`
LOGPATH="/cebtms/files/logs/batch/${DN_OUTPUT}"
LOGFILE="${LOGPATH}/tms_sync_ift_data_${org_table_name}_${DN_OUTPUT}.log"

#判断日志文件路径
if [ -d ${LOGPATH} ] ;then
	echo `date "+%Y-%m-%d %H:%M:%S"` "日志文件路径存在" >> ${LOGFILE}
else
	echo `date "+%Y-%m-%d %H:%M:%S"` "创建日志文件路径" >> ${LOGFILE}
	mkdir -p ${LOGPATH}
fi
echo `date "+%Y-%m-%d %H:%M:%S"` "创建日志文件" >> ${LOGFILE}
touch ${LOGFILE}

# 加锁文件定义
CHECK_LOCK_FILE="${LOGPATH}/tms_sync_ift_data_${org_table_name}.lock"

#定义加文件锁函数
tmsLock(){
    echo `date "+%Y-%m-%d %H:%M:%S"` "检查并防止重复执行..." >> ${LOGFILE}
	if [ -f ${CHECK_LOCK_FILE} ] ;then
	    echo `date "+%Y-%m-%d %H:%M:%S"` "已加锁，重复执行，将关闭进程" >> ${LOGFILE}
        echo "100" >> ${LOGFILE}
        echo "100"
        exit 100
	else
		echo `date "+%Y-%m-%d %H:%M:%S"` "创建锁文件:${CHECK_LOCK_FILE}" >> ${LOGFILE}
		touch ${CHECK_LOCK_FILE}
	fi
}

#定义解锁函数
tmsUnlock(){
	echo `date "+%Y-%m-%d %H:%M:%S"` "解除锁文件:${CHECK_LOCK_FILE}" >> ${LOGFILE}
	rm -f ${CHECK_LOCK_FILE}
}

# 检查是否上锁
tmsLock

# 使用 find 查找符合条件的文件
files=$(find ${source_dir} -maxdepth 1 -type f -name "${pattern}")

# 检查是否找到了文件
if [ -z "$files" ]; then
    echo `date "+%Y-%m-%d %H:%M:%S"` "没有找到符合条件的文件: ${pattern}" >> ${LOGFILE}
    echo `date "+%Y-%m-%d %H:%M:%S"` "脚本执行结束 失败" >> ${LOGFILE}
    fail='-1'
    echo $fail >> ${LOGFILE}
    echo $fail
    tmsUnlock
    exit 1
fi

# 遍历文件
echo `date "+%Y-%m-%d %H:%M:%S"` "遍历文件" >> ${LOGFILE}
found_data_files=false
for file in $files; do
    echo `date "+%Y-%m-%d %H:%M:%S"` "ok文件=${file}" >> ${LOGFILE}
    # 处理数据文件
    data_file="${file%.ok}"
    echo `date "+%Y-%m-%d %H:%M:%S"` "处理文件=${data_file}" >> ${LOGFILE}
    if [ -z "$data_file" ]; then
        echo `date "+%Y-%m-%d %H:%M:%S"` "没有找到数据文件${data_file}" >> ${LOGFILE}
        continue
    fi

    # 处理数据
    unzip_data_file="${work_dir}/$(basename ${data_file%.gz})"
    echo `date "+%Y-%m-%d %H:%M:%S"` "解压文件${unzip_data_file}" >> ${LOGFILE}
    # 解压文件到目标目录
    gunzip -c "${data_file}" > "${unzip_data_file}"

    # 第一步：调用Java API获取需要维护的借据号
    echo `date "+%Y-%m-%d %H:%M:%S"` "-----------调用Java API获取维护借据号--------" >> ${LOGFILE}
    
    maintain_api_url="http://tms-app-cdc:19201/webapi/ncbs-ccllna-acct/getMaintainLnDueBillNos"
    time_url="--connect-timeout 30 --max-time 1800"
    
    maintain_request_data=$(cat <<EOF
{
    "processDate": "${yesterday_with_dash}"
}
EOF
)
    
    echo `date "+%Y-%m-%d %H:%M:%S"` "调用维护借据号接口: $maintain_api_url" >> ${LOGFILE}
    maintain_result=`curl -s $time_url -X POST -H "Content-Type: application/json" -d "$maintain_request_data" $maintain_api_url`
    
    # 检查API调用是否成功
    if [ $? -ne 0 ]; then
        echo `date "+%Y-%m-%d %H:%M:%S"` "获取维护借据号失败" >> ${LOGFILE}
        fail='-1'
        echo $fail >> ${LOGFILE}
        echo $fail
        tmsUnlock
        exit 1
    fi
    
    # 将维护借据号结果保存到文件
    maintain_file="${work_dir}/maintain_ln_due_bill_nos.txt"
    echo "$maintain_result" | grep -o '"[^"]*"' | tr -d '"' > "$maintain_file"
    echo `date "+%Y-%m-%d %H:%M:%S"` "维护借据号已保存到: $maintain_file" >> ${LOGFILE}
    
    # 第二步：调用Java API获取需要排除的借据号
    echo `date "+%Y-%m-%d %H:%M:%S"` "-----------调用Java API获取排除借据号--------" >> ${LOGFILE}
    
    exclusion_api_url="http://tms-app-cdc:19201/webapi/ncbs-ccllna-acct/getExclusionLnDueBillNos"
    
    exclusion_request_data=$(cat <<EOF
{
    "processDate": "${yesterday_with_dash}"
}
EOF
)
    
    echo `date "+%Y-%m-%d %H:%M:%S"` "调用排除借据号接口: $exclusion_api_url" >> ${LOGFILE}
    exclusion_result=`curl -s $time_url -X POST -H "Content-Type: application/json" -d "$exclusion_request_data" $exclusion_api_url`
    
    # 检查API调用是否成功
    if [ $? -ne 0 ]; then
        echo `date "+%Y-%m-%d %H:%M:%S"` "获取排除借据号失败" >> ${LOGFILE}
        fail='-1'
        echo $fail >> ${LOGFILE}
        echo $fail
        tmsUnlock
        exit 1
    fi
    
    # 将排除借据号结果保存到文件
    exclusion_file="${work_dir}/exclusion_ln_due_bill_nos.txt"
    echo "$exclusion_result" | grep -o '"[^"]*"' | tr -d '"' > "$exclusion_file"
    echo `date "+%Y-%m-%d %H:%M:%S"` "排除借据号已保存到: $exclusion_file" >> ${LOGFILE}
    
    # 第三步：根据维护借据号和排除借据号过滤数据
    echo `date "+%Y-%m-%d %H:%M:%S"` "-----------开始过滤数据--------" >> ${LOGFILE}
    
    # 过滤逻辑1：只保留在维护表中的借据号
    echo `date "+%Y-%m-%d %H:%M:%S"` "第一步过滤：保留维护表中的借据号" >> ${LOGFILE}
    awk -F '\\|\\+\\|' 'NR==FNR {values[$1]; next} $1 in values' "$maintain_file" "$unzip_data_file" > "${work_dir}/temp1.txt"
    
    # 过滤逻辑2：排除在排除表中的借据号
    echo `date "+%Y-%m-%d %H:%M:%S"` "第二步过滤：排除排除表中的借据号" >> ${LOGFILE}
    awk -F '\\|\\+\\|' 'NR==FNR {values[$1]; next} !($1 in values)' "$exclusion_file" "${work_dir}/temp1.txt" > "${work_dir}/temp2.txt"
    
    # 最终处理文件
    mv "${work_dir}/temp2.txt" "${work_dir}/${TARGET_FILE}"
    
    # 清理临时文件
    rm -f "$maintain_file" "$exclusion_file" "${work_dir}/temp1.txt"
    
    # 检查处理后的文件是否存在
    if [ ! -f "${work_dir}/${TARGET_FILE}" ]; then
        echo `date "+%Y-%m-%d %H:%M:%S"` "处理后的文件不存在: ${work_dir}/${TARGET_FILE}" >> ${LOGFILE}
        tmsUnlock
        exit 1
    fi
    
    # 记录文件行数
    line_count=$(wc -l < "${work_dir}/${TARGET_FILE}")
    echo `date "+%Y-%m-%d %H:%M:%S"` "过滤后文件行数: $line_count" >> ${LOGFILE}
    
    # 第四步：调用Java API进行数据导入
    echo `date "+%Y-%m-%d %H:%M:%S"` "-----------开始调用Java API导入数据--------" >> ${LOGFILE}
    
    # Java API接口地址
    api_url="http://tms-app-cdc:19201/webapi/ncbs-ccllna-acct/sync"
    
    # 构建请求参数
    request_data=$(cat <<EOF
{
    "filePath": "${work_dir}/${TARGET_FILE}",
    "processDate": "${yesterday_with_dash}",
    "tableName": "tp_ncbs_${org_table_name}"
}
EOF
)
    echo `date "+%Y-%m-%d %H:%M:%S"` "请求参数: $request_data" >> ${LOGFILE}
    echo `date "+%Y-%m-%d %H:%M:%S"` "调用接口: $api_url" >> ${LOGFILE}
    
    # 调用Java API
    result=`curl -s $time_url -X POST -H "Content-Type: application/json" -d "$request_data" $api_url`
    
    # 调用完成时间
    echo `date "+%Y-%m-%d %H:%M:%S"` "执行完成，返回结果：result=$result" >> ${LOGFILE}
    echo `date "+%Y-%m-%d %H:%M:%S"` "-----------数据导入成功!!!!!!-----开始处理临时文件--------" >> ${LOGFILE}
    
    # 删除处理文件
    rm -f ${file}
    rm -f "${work_dir}/${TARGET_FILE}"
    # 移动原文件到备份目录
    mv "${data_file}" "${bak_dir}"
    echo `date "+%Y-%m-%d %H:%M:%S"` "文件处理完成，已移动到备份目录" >> ${LOGFILE}
    echo `date "+%Y-%m-%d %H:%M:%S"` "-----------Java API调用完成--------" >> ${LOGFILE}
    found_data_files=true
done

# 检查是否有数据文件被找到
if ! $found_data_files; then
    echo `date "+%Y-%m-%d %H:%M:%S"` "没有找到符合条件的数据文件" >> ${LOGFILE}
    echo `date "+%Y-%m-%d %H:%M:%S"` "脚本执行结束 失败" >> ${LOGFILE}
    fail='-1'
    echo $fail >> ${LOGFILE}
    echo $fail
    tmsUnlock
    exit 1
fi

#解锁文件
tmsUnlock

#返回结果定义
success='0'
fail='-1'

# 正常结束
echo `date "+%Y-%m-%d %H:%M:%S"` "脚本执行结束 成功" >> ${LOGFILE}
echo $success >> ${LOGFILE}
echo $success
exit 0