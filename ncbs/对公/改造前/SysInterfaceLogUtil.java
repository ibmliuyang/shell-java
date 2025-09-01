package com.cebbank.tms.wenjing.dppt.utils;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.util.SpringContextsUtil;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.sys.log.domain.SysInterfaceLog;
import com.cebbank.tms.wenjing.sys.log.service.SysInterfaceLogService;

/**
 * 日志工具类
 * <pre>
 *
 * </pre>
 *
 * @author 祝刚
 * @version V5.0.0
 * @date 2024/9/18 15:17
 * @see
 * @since V5.0.0 (2024/9/18 15:17)
 */
public class SysInterfaceLogUtil {

    private static CSPSLogger log = CSPSLogFactory.get(SysInterfaceLogUtil.class);

    public static void insertErrorLog(String systemName, String interfaceCode, String interfaceName, String error, UserVO userVO) {
        // 获取日志服务接口
        SysInterfaceLog interfaceLog = new SysInterfaceLog();
        interfaceLog.setSystem(systemName);
        interfaceLog.setInterfaceCode(interfaceCode);
        interfaceLog.setInterfaceName(interfaceName);
        interfaceLog.setResultMessage(error);
        try {
            SysInterfaceLogService sysInterfaceLogService = SpringContextsUtil.getBean("sysInterfaceLogService", SysInterfaceLogService.class);
            sysInterfaceLogService.saveOrUpdate(interfaceLog, userVO);
        } catch (Exception e) {
            log.error("储存接口日志失败", e);
        }
    }

    public static void insertIFTLog(String systemName, String interfaceCode, String interfaceName, String  requestParam , String resultMessage, UserVO userVO,Long consumingTime) {
        // 获取日志服务接口
        SysInterfaceLog interfaceLog = new SysInterfaceLog();
        interfaceLog.setSystem("IFT-FILE2TPTABLE");
        interfaceLog.setInterfaceCode(interfaceCode);
        interfaceLog.setInterfaceName(interfaceName);
        interfaceLog.setRequestParam(requestParam);
        interfaceLog.setResultMessage(resultMessage);
        interfaceLog.setConsumingTime(Integer.valueOf(consumingTime.toString()));
        try {
            SysInterfaceLogService sysInterfaceLogService = SpringContextsUtil.getBean("sysInterfaceLogService", SysInterfaceLogService.class);
            sysInterfaceLogService.saveOrUpdate(interfaceLog, userVO);
        } catch (Exception e) {
            log.error("储存接口日志失败", e);
        }
    }

}
