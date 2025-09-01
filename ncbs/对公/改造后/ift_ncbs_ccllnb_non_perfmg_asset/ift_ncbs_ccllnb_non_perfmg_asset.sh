#!/bin/bash
#***************************************************************
#系统名称： 税务管理系统
#脚本名称： ift_ncbs_ccllnb_non_perfmg_asset.sh
#脚本功能： 对公贷款不良资产表（架构重构版）
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
org_table_name="ccllnb_non_perfmg_asset"

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
yesterday=$(date -d "yesterday" +%Y%m%d)
yesterday_with_dash=$(date -d "yesterday" +%Y-%m-%d)
pattern="a_ncbs_${org_table_name}_${yesterday}_*.dat.gz.ok"

# 日志目录
DN_OUTPUT=`date +%Y%m%d`
LOGPATH="/cebtms/files/logs/batch/${DN_OUTPUT}"
LOGFILE="${LOGPATH}/tms_sync_ift_data_${org_table_name}_${DN_OUTPUT}.log"

if [ ! -d ${LOGPATH} ]; then
	mkdir -p ${LOGPATH}
fi
touch ${LOGFILE}

# 加锁文件定义
CHECK_LOCK_FILE="${LOGPATH}/tms_sync_ift_data_${org_table_name}.lock"

tmsLock(){
	if [ -f ${CHECK_LOCK_FILE} ] ;then
        echo "100"
        exit 100
	else
		touch ${CHECK_LOCK_FILE}
	fi
}

tmsUnlock(){
	rm -f ${CHECK_LOCK_FILE}
}

tmsLock

# 使用 find 查找符合条件的文件
files=$(find ${source_dir} -maxdepth 1 -type f -name "${pattern}")

if [ -z "$files" ]; then
    fail='-1'
    echo $fail
    tmsUnlock
    exit 1
fi

found_data_files=false
for file in $files; do
    data_file="${file%.ok}"
    if [ -z "$data_file" ]; then
        continue
    fi

    unzip_data_file="${work_dir}/$(basename ${data_file%.gz})"
    gunzip -c "${data_file}" > "${unzip_data_file}"
    mv "${unzip_data_file}" "${work_dir}/${TARGET_FILE}"

    # 调用Java API
    api_url="http://tms-app-cdc:19201/webapi/ncbs-ccllnb-non-perfmg-asset/sync"
    time_url="--connect-timeout 30 --max-time 1800"

    request_data=$(cat <<EOF
{
    "filePath": "${work_dir}/${TARGET_FILE}",
    "processDate": "${yesterday_with_dash}",
    "tableName": "tp_ncbs_${org_table_name}"
}
EOF
)
    
    result=`curl -s $time_url -X POST -H "Content-Type: application/json" -d "$request_data" $api_url`
    
    rm -f ${file}
    rm -f "${work_dir}/${TARGET_FILE}"
    mv "${data_file}" "${bak_dir}"
    found_data_files=true
done

if ! $found_data_files; then
    fail='-1'
    echo $fail
    tmsUnlock
    exit 1
fi

tmsUnlock
success='0'
echo $success
exit 0