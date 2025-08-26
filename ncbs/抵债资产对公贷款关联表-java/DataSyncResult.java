package com.cebbank.tms.wenjing.ift.vo;

import lombok.Data;

/**
 * 数据同步结果VO
 * 
 * @author 系统重构
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-08-25 10:00:00
 */
@Data
public class DataSyncResult {
    
    /**
     * 导入的记录数
     */
    private Long importedRows;
    
    /**
     * 处理完成时间
     */
    private String processTime;
    
    /**
     * 目标表名
     */
    private String tableName;
}