package com.cebbank.tms.wenjing.ift.vo;

import lombok.Data;

/**
 * 数据同步请求VO
 * 
 * @author 系统重构
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-08-25 10:00:00
 */
@Data
public class DataSyncRequest {
    
    /**
     * 处理后的数据文件完整路径
     */
    private String filePath;
    
    /**
     * 数据处理日期(YYYY-MM-DD)
     */
    private String processDate;
    
    /**
     * 目标表名
     */
    private String tableName;
}