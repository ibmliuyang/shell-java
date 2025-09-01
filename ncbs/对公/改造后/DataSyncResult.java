package com.cebbank.tms.wenjing.ift.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    
    /**
     * 创建成功的数据同步结果
     * @param importedRows 导入的记录数
     * @param tableName 目标表名
     * @return DataSyncResult
     */
    public static DataSyncResult success(Long importedRows, String tableName) {
        DataSyncResult result = new DataSyncResult();
        result.setImportedRows(importedRows);
        result.setTableName(tableName);
        result.setProcessTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return result;
    }
}