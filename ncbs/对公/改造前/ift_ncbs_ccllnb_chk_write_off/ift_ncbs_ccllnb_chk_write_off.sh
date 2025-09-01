#!/bin/bash
#***************************************************************
#系统名称： 税务管理系统
#脚本名称： ift_ecas_ccllnb_chk_write_off.sh
#脚本功能： 对公贷款核销登记簿
#
#版本        日期       编写者    项目组/修改内容
#-------   -------     -------  ----------
#v1.0      2025-02-24  祝  刚    税务管理系统/新建
#****************************************************************


# 当前脚本目录
script_dir="$(dirname "$(readlink -f "$0")")"
# 表名
org_table_name="ccllnb_chk_write_off"
# 维护日期所在列
maintain_date_col=35

# 源文件目录
source_dir="/cebtms/files/ift/ecas"
# 操作目录
work_dir="/cebtms/files/ift/work/${org_table_name}"
# 备份目录
bak_dir='/cebtms/files/bak/ift/ecas'

# 清表数据SQL
TRUNCATE_SQL="truncate table tp_ncbs_${org_table_name};"
TABLE_COLUMNS=--columns=ln_due_bill_no,contr_no,cust_no,cust_name,open_acct_dt,exp_dt,biz_inst_no,accoun_inst_no,distrib_amt,wri_off_dt,wri_off_pri,wri_off_int,wri_off_recov_dt,retu_wri_off_pri,retu_wri_off_int,wri_off_tell,ln_wri_off_dept,alr_wri_off_pri_int,retu_alr_wri_off_pri_int,wri_off_int_compou_int,retu_wri_off_int_compou_int,last_tm_int_accru_dt,ln_wri_off_stat,tran_tell,acct_canc_case_rese_ind,wri_off_int_accru_manr,revol_fund_amt,revol_amt1,revol_amt2,rese_fie,rese_fie_1,rese_fie_2,mainte_tell,mainte_inst,mainte_dt,mainte_tm,record_stat
TARGET_FILE=tp_ncbs_${org_table_name}.txt

# 检查目标目录是否存在，如果不存在则创建
if [ ! -d "$work_dir" ]; then
    mkdir -p "$work_dir"
fi
if [ ! -d "$bak_dir" ]; then
    mkdir -p "$bak_dir"
fi

#数据库连接
export DB_USERID="-h ${db_host} -P ${db_port} -u ${db_username} -p${db_password}"

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
	echo `date "+%Y-%m-%d %H:%M:%S"` "日志文件路径存在"
else
	echo `date "+%Y-%m-%d %H:%M:%S"` "创建日志文件路径"
	mkdir -p ${LOGPATH}
fi
echo `date "+%Y-%m-%d %H:%M:%S"` "创建日志文件"
touch ${LOGFILE}
# 加锁文件定义
CHECK_LOCK_FILE="${LOGPATH}/tms_sync_ift_data_${org_table_name}.lock"

#加文件锁
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

#解锁
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
    echo '1' >> ${LOGFILE}
    echo 1
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

    if [ -n "$maintain_date_col" ]; then
        # 过滤数据
        echo `date "+%Y-%m-%d %H:%M:%S"` "过滤数据 yesterday_with_dash=$yesterday_with_dash" >> ${LOGFILE}
        awk -F '\\|\\+\\|' -v maintain_date_col="$maintain_date_col" -v today_with_dash="$today_with_dash" -v yesterday_with_dash="$yesterday_with_dash" '$maintain_date_col == yesterday_with_dash' "$unzip_data_file" > "${work_dir}/temp.txt"
        mv "${work_dir}/temp.txt" "$unzip_data_file"
    fi

    # 导入数据到数据库中
    # 修改名称
    mv "${unzip_data_file}" "${work_dir}/${TARGET_FILE}"
	# 清空表数据
	echo `date "+%Y-%m-%d %H:%M:%S"` "清空表数据" >> ${LOGFILE}
	/edb/app/mysql-5.7/bin/mysql ${DB_USERID} -B ${db_name} -e "${TRUNCATE_SQL}"
    # insert data
	echo `date "+%Y-%m-%d %H:%M:%S"` "导入新数据" >> ${LOGFILE}
    if( /edb/app/mysql-5.7/bin/mysqlimport --socket=/mysqldata/3309/socket/mysql.sock ${DB_USERID} ${db_name} --fields-terminated-by='|+|' ${TABLE_COLUMNS} --local ${work_dir}/${TARGET_FILE} --verbose )
    then
	    echo `date "+%Y-%m-%d %H:%M:%S"` "导入新数据完成" >> ${LOGFILE}
    else
        echo `date "+%Y-%m-%d %H:%M:%S"` "导入新数据失败" >> ${LOGFILE}
    fi

    # 删除数据文件
    rm ${file}
    rm "${work_dir}/${TARGET_FILE}"

    # 移动原文件到备份目录
    mv "${data_file}" "${bak_dir}"

    found_data_files=true
done

# 检查是否有数据文件被找到
if ! $found_data_files; then
    echo `date "+%Y-%m-%d %H:%M:%S"` "没有找到符合条件的数据文件" >> ${LOGFILE}
    echo `date "+%Y-%m-%d %H:%M:%S"` "脚本执行结束 失败" >> ${LOGFILE}
    echo '1' >> ${LOGFILE}
    echo 1
    tmsUnlock
    exit 1
fi
# 正常结束
echo 0 >> ${LOGFILE}
echo 0
tmsUnlock
exit 0
