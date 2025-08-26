# 抵债资产对公贷款关联表Java代码说明

## 项目概述

本项目基于javaDemo的代码风格，在原有CRUD功能基础上增加了`sync`数据同步方法，实现Shell+Java混合架构的数据导入功能。

## 文件结构

```
抵债资产对公贷款关联表-java/
├── NcbsCgmdabClLoanRlvc.java              # 实体类 (Entity)
├── NcbsCgmdabClLoanRlvcController.java    # 控制器 (Controller) 
├── NcbsCgmdabClLoanRlvcService.java       # 服务接口 (Service Interface)
├── NcbsCgmdabClLoanRlvcServiceImpl.java   # 服务实现 (Service Implementation)
├── NcbsCgmdabClLoanRlvcRepository.java    # 数据访问层 (Repository)
├── NcbsCgmdabClLoanRlvcOutVO.java         # 输出VO
├── DataSyncRequest.java                   # 数据同步请求VO
├── DataSyncResult.java                    # 数据同步结果VO
└── README.md                              # 本说明文档
```

## 核心功能

### 1. 原有功能 (继承自javaDemo)
- ✅ **详情查询**: `GET /ncbs-cgmdab-cl-loan-rlvc/get/{id}`
- ✅ **保存更新**: `POST /ncbs-cgmdab-cl-loan-rlvc/save-update`
- ✅ **分页查询**: `POST /ncbs-cgmdab-cl-loan-rlvc/page-list`
- ✅ **单条删除**: `POST /ncbs-cgmdab-cl-loan-rlvc/delete-by-id/{id}`
- ✅ **批量删除**: `POST /ncbs-cgmdab-cl-loan-rlvc/delete-all-by-id`

### 2. 新增功能 (数据同步)
- ✅ **数据同步**: `POST /ncbs-cgmdab-cl-loan-rlvc/sync`

## API接口详情

### 数据同步接口

**接口地址**: `POST http://tms-app-cdc:19201/ncbs-cgmdab-cl-loan-rlvc/sync`

**请求参数**:
```json
{
    "filePath": "/cebtms/files/ift/work/cgmdab_cl_loan_rlvc/tp_ncbs_cgmdab_cl_loan_rlvc.txt",
    "processDate": "2025-08-24", 
    "tableName": "tp_ncbs_cgmdab_cl_loan_rlvc"
}
```

**成功响应**:
```json
{
    "code": "1000",
    "message": "操作成功",
    "data": {
        "importedRows": 12345,
        "processTime": "2025-08-25 10:30:15",
        "tableName": "tp_ncbs_cgmdab_cl_loan_rlvc"
    },
    "success": true
}
```

**失败响应**:
```json
{
    "code": "9999",
    "message": "数据导入失败:文件不存在或格式错误",
    "data": null,
    "success": false
}
```

## 代码特点

### 1. 严格遵循javaDemo风格
- ✅ 使用JAX-RS注解 (`@Path`, `@POST`, `@GET`)
- ✅ 使用`CSPSLogger`进行日志记录
- ✅ 使用`ResultVo`作为统一返回格式
- ✅ 使用`@Resource`注入依赖
- ✅ 继承`ControllerSupport`基类
- ✅ 使用`@AspLog`记录操作日志

### 2. 数据同步功能特点
- ✅ **事务管理**: 使用`@Transactional`确保数据一致性
- ✅ **批量处理**: 支持大文件批量导入 (BATCH_SIZE=1000)
- ✅ **数据验证**: 严格验证49个字段格式
- ✅ **错误处理**: 完善的异常处理和日志记录
- ✅ **文件管理**: 支持文件读取和格式解析

### 3. 与Shell脚本集成
- ✅ **HTTP通信**: Shell通过curl调用Java API
- ✅ **标准响应**: 返回Shell可识别的JSON格式
- ✅ **错误传递**: 异常信息正确传递给Shell
- ✅ **日志一致**: Java和Shell日志格式统一

## 数据处理流程

### 1. Shell脚本职责
1. 文件查找和解压
2. 数据过滤 (按维护日期)
3. 文件格式转换
4. HTTP API调用
5. 文件清理和备份

### 2. Java服务职责
1. 参数验证
2. 文件读取
3. 数据解析和验证
4. 数据库清空 (TRUNCATE)
5. 批量数据导入
6. 结果返回

## 技术栈

- **框架**: Spring Boot + JAX-RS
- **ORM**: JPA + Hibernate  
- **数据库**: MySQL 5.7+
- **工具**: Lombok + Hutool
- **日志**: CSPS Logger
- **事务**: Spring Transaction

## 部署配置

### 1. 数据库配置
确保以下表存在：
```sql
CREATE TABLE tp_ncbs_cgmdab_cl_loan_rlvc (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    commute_debt_ast_no VARCHAR(50),
    ln_due_bill_no VARCHAR(50),
    -- ... 其他49个业务字段
    create_time DATETIME,
    update_time DATETIME,
    -- ... 系统字段
);
```

### 2. 应用配置
```yaml
# 数据源配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tms_db
    username: ${db_username}
    password: ${db_password}

# 事务配置  
utaxTransactionManager:
  # 事务管理器配置
```

### 3. 权限配置
确保以下权限：
- Java进程对数据文件有读取权限
- 数据库用户有CREATE、DROP、INSERT、SELECT权限
- Shell脚本能访问Java服务端口19201

## 监控运维

### 1. 日志文件
- **Shell日志**: `/cebtms/files/logs/batch/YYYYMMDD/tms_sync_ift_data_cgmdab_cl_loan_rlvc_YYYYMMDD.log`
- **Java日志**: 通过CSPS Logger统一管理

### 2. 关键指标
- 文件处理成功率
- 数据导入记录数
- API响应时间
- 错误发生频率

### 3. 告警机制
- 文件处理失败告警
- 数据导入异常告警
- API调用超时告警
- 数据库连接异常告警

## 注意事项

1. **数据安全**: 导入前会清空目标表，确保有数据备份
2. **文件格式**: 严格按照49个字段的格式要求
3. **字段分隔符**: 必须使用`|+|`分隔符
4. **事务回滚**: 导入失败时自动回滚
5. **并发控制**: Shell脚本通过文件锁防止重复执行

## 版本信息

- **版本**: V2.0.0
- **作者**: 系统重构
- **日期**: 2025-08-25
- **基于**: javaDemo代码风格