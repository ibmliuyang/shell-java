#!/bin/bash
#***************************************************************
#系统名称： 税务管理系统
#脚本名称： ift_ncbs_crllna_acct_final.sh
#脚本功能： 零售贷款账户主表
#
#版本        日期       编写者    项目组/修改内容
#-------   -------     -------  ----------
#v1.0      2025-02-24  祝  刚    税务管理系统/新建
#****************************************************************


# 当前脚本目录
script_dir="$(dirname "$(readlink -f "$0")")"
# 表名
org_table_name="crllna_acct"

# 源文件目录
source_dir="/cebtms/files/ift/ecas"
# 操作目录
work_dir="/cebtms/files/ift/work/${org_table_name}"
# 备份目录
bak_dir='/cebtms/files/bak/ift/ecas'

# 清表数据SQL
TRUNCATE_SQL="truncate table tp_ncbs_${org_table_name};"
TABLE_COLUMNS=--columns=ln_due_bill_no,contr_no,cust_no,cust_name,biz_inst_no,accoun_inst_no,ln_prod_no,prod_name,open_acct_dt,regi_timest,val_dt,exp_dt,mon_unit_ln_term,ln_days,ln_form,accru_non_accru_stat,ln_acct_stat,mul_ln_ded_sequ,mul_ln_overd_ded_sequ,curre,contr_amt,due_bill_amt,regu_pri,overd_pri,dead_ln_pri,bad_debt_pri,rece_accru_int,col_accru_int,rece_deb_int,col_deb_int,rece_accru_pena_int,col_accru_pena_int,rece_pena_int,col_pena_int,on_bal_accru_compou_int,on_bal_compou_int,off_bal_accru_compou_int,off_bal_compou_int,accru_subsi_on_int,rece_subsi_on_int,rece_accru_outp_tax,col_accru_outp_tax,prep_int,wri_off_pri,wri_off_int,rece_cost,alr_wri_off_pri_int,wri_off_int_compou_int,last_fin_tran_day,chan_no,chan_name,ln_usa,due_bill_chara,ast_stat,notes_info,dtl_seri_no,open_acct_inst_no,open_acct_tell,canc_acct_tell,canc_acct_dt,bat_grp_no,rede_tm_poi_int_pay_off_ind,ln_wri_off_stat,by_reve_ind,subsi_on_int_compou_int,col_subsi_on_int_compou_int,col_accru_subsi_on_int,col_subsi_on_int,subsi_on_int_accru_compou_int,col_subsi_on_int_accru_compou_int,revol_fund_amt,revol_amt1,revol_amt2,rese_fie,rese_fie_1,rese_fie_2,mainte_tell,mainte_inst,mainte_dt,mainte_tm,record_stat,overa_sit_aff_stat,overa_sit_aff_seq_no,optim_lock_vers_no,sys_timest
TARGET_FILE=tp_ncbs_${org_table_name}.txt

# 读取需要维护的借据号
ln_due_bill_no_all_file="ln_due_bill_no_all_file.txt"
ln_due_bill_no_all_sql="select ln_due_bill_no from ncbs_crllna_acct union all select ln_due_bill_no from tp_ncbs_crllnb_wri_off union all select ln_due_bill_no from tp_ncbs_crllnb_bad_ast union all select ln_due_bill_no from tp_ncbs_cgmdab_rl_loan_rlvc;"

# 读取贷款属性表中要过滤掉的借据号
ln_due_bill_no_all_exclusion_file="ln_due_bill_no_all_exclusion_file.txt"
ln_due_bill_no_all_exclusion_sql="select ln_due_bill_no from tp_ncbs_crllnb_basic_attr where ln_biz_clas in ('2', '3');"

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

    # 读取数据库中借据号维护表 ${ln_due_bill_no_all_file}
    target_ln_due_bill_no_all_file="${work_dir}/${ln_due_bill_no_all_file}"
	/edb/app/mysql-5.7/bin/mysql ${DB_USERID} -B ${db_name} -Nse "${ln_due_bill_no_all_sql}" > "${target_ln_due_bill_no_all_file}"

    # 读取数据库中贷款属性表中的数据 ${ln_due_bill_no_all_exclusion_file}
    target_ln_due_bill_no_all_exclusion_file="${work_dir}/${ln_due_bill_no_all_exclusion_file}"
    /edb/app/mysql-5.7/bin/mysql ${DB_USERID} -B ${db_name} -Nse "${ln_due_bill_no_all_exclusion_sql}" > "${target_ln_due_bill_no_all_exclusion_file}"

    # 过滤数据 - 借据号维护表
    echo `date "+%Y-%m-%d %H:%M:%S"` "过滤数据: $(wc -l ${target_ln_due_bill_no_all_file})" >> ${LOGFILE}
    awk -F '\\|\\+\\|' 'NR==FNR {values[$1]; next} $1 in values' ${target_ln_due_bill_no_all_file} "$unzip_data_file" > "${work_dir}/temp.txt"
    mv "${work_dir}/temp.txt" "$unzip_data_file"
    rm ${target_ln_due_bill_no_all_file}

    # 过滤数据 - 贷款属性借据号
    echo `date "+%Y-%m-%d %H:%M:%S"` "过滤数据: $(wc -l ${target_ln_due_bill_no_all_exclusion_file})" >> ${LOGFILE}
    awk -F '\\|\\+\\|' 'NR==FNR {values[$1]; next} !($1 in values)' ${target_ln_due_bill_no_all_exclusion_file} "$unzip_data_file" > "${work_dir}/temp.txt"
    mv "${work_dir}/temp.txt" "$unzip_data_file"
    rm ${target_ln_due_bill_no_all_exclusion_file}

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
