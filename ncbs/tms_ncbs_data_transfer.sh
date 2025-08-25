#!/bin/bash
#***************************************************************
#系统名称： 税务管理系统
#脚本名称： tms_ncbs_data_transfer.sh
#脚本功能： 数据清洗-对公
# 参数： dgdk/grdk  对公/零售
#
#版本        日期       编写者    项目组/修改内容
#-------   -------     -------  ----------
#v1.0      2025-07-14  祝  刚    税务管理系统/新建
#****************************************************************


export LANG=zh_CN.UTF-8

#日志
SCRIPT_NAME="tms_ncbs_data_transfer"
DN_OUTPUT=`date +%Y%m%d`
#日志文件路径
LOGPATH=/cebtms/files/logs/batch/$DN_OUTPUT
#判断日志文件路径
if [ -d ${LOGPATH} ] ;then
	echo `date "+%Y-%m-%d %H:%M:%S"` "日志文件路径存在" >>${LOGFILE}
else
	mkdir -p ${LOGPATH}
	echo `date "+%Y-%m-%d %H:%M:%S"` "创建日志文件路径" >>${LOGFILE}
fi
#日志名称
LOGFILE=${LOGPATH}/${SCRIPT_NAME}_$1_${DN_OUTPUT}.log
touch ${LOGFILE}

CHECK_LOCK_FILE="${LOGPATH}/${SCRIPT_NAME}_$1.lock"

#加文件锁
tmsLock(){
    echo `date "+%Y-%m-%d %H:%M:%S"` "检查并防止重复执行..." >>${LOGFILE}
	if [ -f ${CHECK_LOCK_FILE} ] ;then
	    echo `date "+%Y-%m-%d %H:%M:%S"` "已加锁，重复执行，将关闭进程" >>${LOGFILE}
        echo "100" >> ${LOGFILE}
        echo "100"
        exit 100
	else
		echo `date "+%Y-%m-%d %H:%M:%S"` "创建锁文件:${CHECK_LOCK_FILE}" >>${LOGFILE}
		touch ${CHECK_LOCK_FILE}
	fi
}

#解锁
tmsUnlock(){
	echo `date "+%Y-%m-%d %H:%M:%S"` "解除锁文件:${CHECK_LOCK_FILE}" >>${LOGFILE}
	rm -f ${CHECK_LOCK_FILE}
}


transferName=$1
#脚本变量
SERVER_HOST=${app_host}
biz_url="${SERVER_HOST}/webapi/ast-hx-sjqx/${transferName}"
time_url="--connect-timeout 10 -m 20"

yesterday=`date +%Y%m%d -d "1 days ago"`
today=`date +%Y%m%d`


#判断日志文件路径
if [ -d ${LOGPATH} ] ;then
	echo `date "+%Y-%m-%d %H:%M:%S"` "日志文件路径存在" >>${LOGFILE}
else
	mkdir -p ${LOGPATH}
	echo `date "+%Y-%m-%d %H:%M:%S"` "创建日志文件路径" >>${LOGFILE}
fi


#检查执行的脚本是否已在执行中，避免重复执行
tmsLock


echo `date "+%Y-%m-%d %H:%M:%S"` "-----------Goto batchingService start--------" >>${LOGFILE}
#调用接口地址地址
#调用接口
echo `date "+%Y-%m-%d %H:%M:%S"` "curl $time_url $biz_url" >>${LOGFILE}
result=`curl $time_url $biz_url`


#调用完成时间
echo `date "+%Y-%m-%d %H:%M:%S"` "执行完成，返回结果：result=$result" >>${LOGFILE}
echo `date "+%Y-%m-%d %H:%M:%S"` "-----------Goto batchingService end--------" >>${LOGFILE}

echo 0
tmsUnlock
exit 0


