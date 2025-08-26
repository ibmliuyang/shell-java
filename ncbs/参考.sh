#!/bin/bash



export LANG=zh_CN.UTF-8


#脚本变量
DN_OUTPUT=`date +%Y%m%d`
yesterday=`date +%Y%m%d -d "1 days ago"`
today=`date +%Y%m%d`
ECASPATH=/ims/ims/files/ift/in_files/ECAS
EMERGENCYPATH=/ims/ims/files/ift/in_files/ECAS

LOGPATH=/ims/ims/logs/batch/ecas_invFlowSync						#日志文件路径
LOGFILE=${LOGPATH}/ims_ecas_invFlowSync_${DN_OUTPUT}.log			#日志名称

PID=$$
checkitem="$0"

#判断日志文件路径
if [ -d ${LOGPATH} ] ;then
	echo `date "+%Y-%m-%d %H:%M:%S"` "日志文件路径存在" >>${LOGFILE}
else
	mkdir -p ${LOGPATH}
	echo `date "+%Y-%m-%d %H:%M:%S"` "创建日志文件路径" >>${LOGFILE}
fi


#检查执行的脚本是否已在执行中，避免重复执行
#echo `date "+%Y-%m-%d %H:%M:%S"` "检查并防止重复执行..." >>${LOGFILE}
#echo $checkitem

#let procCnt=`ps -A --format='%p%P%C%x%a' --width 2048 -w --sort pid|grep "$checkitem"|grep -v grep|grep -v " \-c sh"|grep -v "${PID}"|grep -c sh|awk '{printf("%d\n",$1)}'`
#if [ ${procCnt} -gt 0 ];then
#	proctt=`ps -A --format='%p%P%C%x%a' --width 2048 -w --sort pid|grep "$checkitem"|grep -v grep|grep -v " \-c sh"|grep -v "${PID}"`
#	echo "$0已经在运行[procs=${proctt},当前id=${PID}],此次执行自动取消." >>${LOGFILE}
#	echo 100 >>${LOGFILE}
#	exit 100;
#fi
#echo `date "+%Y-%m-%d %H:%M:%S"` "脚本唯一执行，当前ID:${PID}" >>${LOGFILE}

#mfx 20240521优化防重检查判断逻辑
#定义文件锁
CHECK_LOCK_FILE="${LOGPATH}/${checkitem}.lock"

#定义加文件锁函数
imsLock(){
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

#定义解锁函数
imsUnlock(){
	echo `date "+%Y-%m-%d %H:%M:%S"` "解除锁文件:${CHECK_LOCK_FILE}" >>${LOGFILE}
	rm ${CHECK_LOCK_FILE}
}

#检查执行的脚本是否已在执行中，避免重复执行
imsLock

#检查文件是否存在
ECASHLFIELS=`find ${ECASPATH} -name 'w*'${yesterday}'*.tar.Z' | wc -l`
if [ ${ECASHLFIELS} -eq 0 ]; then
		echo `date "+%Y-%m-%d %H:%M:%S"` "${ECASPATH}/w${yesterday}_0001.tar.Z file not exist!" >>${LOGFILE}
		
		echo `date "+%Y-%m-%d %H:%M:%S"` "Check ${EMERGENCYPATH}/w${yesterday}_0001.tar.Z !!!" >>${LOGFILE}
		ECASHLFIELS_EMC=`find ${EMERGENCYPATH} -name 'w*'${yesterday}'*.tar.Z' | wc -l`
		if [ ${ECASHLFIELS_EMC} -eq 0 ]; then
			echo `date "+%Y-%m-%d %H:%M:%S"` "${EMERGENCYPATH}/w${yesterday}_0001.tar.Z file not exist!" >>${LOGFILE}
			#解锁文件
			imsUnlock
			echo 1 >>${LOGFILE}
			echo 1
			exit 1
		fi
		
		#To copy g.xxxxxxxx.tar.Z from EMERGENCYPATH to ECASPATH
		echo `date "+%Y-%m-%d %H:%M:%S"` "Copy ${EMERGENCYPATH}/w${yesterday}_0001.tar.Z to ${ECASPATH}!" >>${LOGFILE}
		\cp -f -R  ${EMERGENCYPATH}/w*${yesterday}*.tar.Z  ${ECASPATH}
		if [ $? != 0 ]; then
			echo "********* Copy ${EMERGENCYPATH}/w${yesterday}_0001.tar.Z to ${ECASPATH} Failed!!! ****{$?}******" >> ${LOGFILE}
			#解锁文件
			imsUnlock
			echo 151 >>${LOGFILE}
			echo 151
			exit 151
		fi
fi

ECASHLFIELS=`find ${ECASPATH} -name 'w*'${yesterday}'*.tar.Z' | wc -l`
if [ ${ECASHLFIELS} -eq 0 ]; then
		echo `date "+%Y-%m-%d %H:%M:%S"` "${ECASPATH}/w${yesterday}_0001.tar.Z file not exist!" >>${LOGFILE}
		#解锁文件
		imsUnlock
		echo 101 >>${LOGFILE}
		echo 101
		exit 101
fi

echo `date "+%Y-%m-%d %H:%M:%S"` "${ECASPATH}/w${yesterday}_0001.tar.Z file exist!!!!!!" >>${LOGFILE}

cd ${ECASPATH}
\cp -f -R ${ECASPATH}/w*${yesterday}*.tar.Z ${ECASPATH}/w${yesterday}_0001_tmp.tar.Z
uncompress -f ${ECASPATH}/w*${yesterday}*_tmp.tar.Z

if [ $? != 0 ]; then
	echo "********* uncompress ${ECASPATH}/w${yesterday}_0001_tmp.tar.Z Failed!!! ***{$?}*******" >> ${LOGFILE}
	#解锁文件
	imsUnlock
	echo 151 >>${LOGFILE}
	echo 151
	exit 151
fi

tar -xf w*${yesterday}*.tar


#分割bspdj
countNum=`cat ${ECASPATH}/${yesterday}*_1/D0252_${yesterday}_bspdj.txt|wc -l`
echo `date "+%Y-%m-%d %H:%M:%S"` "D0252_${yesterday}_bspdj.txt total $countNum" >>${LOGFILE}

cd ${ECASPATH}/${yesterday}*_1
split -l 300000 D0252_${yesterday}_bspdj.txt -d -a 2 D0252_${yesterday}_bspdj_ && ls |grep D0252_${yesterday}_bspdj_|xargs -n1 -i{} mv {} {}.txt
if [ $? != 0 ]; then
	echo "********* split file fail,to check ***{$?}*******" >> ${LOGFILE}
	rm -r ${ECASPATH}/${yesterday}*_1 ${ECASPATH}/w*${yesterday}*.tar
	#解锁文件
  imsUnlock
	echo 151 >>${LOGFILE}
	echo 151
	exit 151
else
	rm ${ECASPATH}/${yesterday}*_1/D0252_${yesterday}_bspdj.txt
	ls -tlr >> ${LOGFILE}
fi

echo `date "+%Y-%m-%d %H:%M:%S"` "-----------Goto batchingService start--------" >>${LOGFILE}
#调用接口地址地址
gd_url="http://ims-xx-service:8099/trigger/invFlowSync"
time_url="-siL CURLOPT_TIMEOUT 10800"


#调用接口
echo `date "+%Y-%m-%d %H:%M:%S"` "curl $time_url $gd_url | grep '"code":"0"'|wc -l" >>${LOGFILE}
result=`curl $time_url $gd_url | grep '"code":"0"'|wc -l`

cd ${ECASPATH}
rm -r ${ECASPATH}/${yesterday}*_1 ${ECASPATH}/w*${yesterday}*.tar
#rm ${ECASPATH}/w*${yesterday}*.tar

#调用完成时间
echo `date "+%Y-%m-%d %H:%M:%S"` "执行完成，返回结果：result=$result" >>${LOGFILE}
echo `date "+%Y-%m-%d %H:%M:%S"` "-----------Goto batchingService end--------" >>${LOGFILE}

#解锁文件
imsUnlock

#返回结果定义
success='0'
fail='-1'

#返回结果中包含code:0则为成功，否则失败
if [ $result -eq 1 ] 
then 
	echo `date "+%Y-%m-%d %H:%M:%S"` "-----------Well done!!!!!!-----Prepare to deal tmp file--------" >>${LOGFILE}
	echo $success >>${LOGFILE}
	echo $success
else
	echo `date "+%Y-%m-%d %H:%M:%S"` "-----------Check-Check_Check!!!!!!-----" >>${LOGFILE}
	echo $fail >>${LOGFILE}
	echo $fail
fi
