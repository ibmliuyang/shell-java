# 文件入库操作从Shell改到Java的改造说明书

## 1. 改造概述

本次改造将原有的Shell脚本直接调用MySQL数据库导入数据的方式改造为Shell+Java混合架构：
- **Shell负责**：文件处理、数据过滤、文件管理、并发控制和日志记录
- **Java负责**：数据导入、业务逻辑处理、事务管理
- **通信方式**：Shell通过HTTP API调用Java服务

## 2. 架构变化对比

### 2.1 改造前架构
```
Shell脚本 → 直接调用mysqlimport → MySQL数据库
```

### 2.2 改造后架构

#### 2.2.1 标准模式（适用于简单文件处理）
```
Shell脚本 → HTTP API调用 → Java服务 → JPA/Hibernate → MySQL数据库
```

#### 2.2.2 复杂模式（适用于需要多次数据库查询的情况）
```
Shell脚本 → 多次API调用获取查询结果 → 文件过滤 → 最终API调用 → Java服务 → MySQL数据库

具体流程：
1. Shell调用 /getMaintainLnDueBillNos 获取维护借据号列表
2. Shell调用 /getExclusionLnDueBillNos 获取排除借据号列表
3. Shell在本地根据查询结果进行数据过滤
4. Shell调用 /sync 接口导入过滤后的数据
```

## 3. 详细改造内容

### 3.1 Shell脚本改造

#### 3.1.1 删除数据库连接配置
**改造前：**
```bash
#数据库连接
export DB_USERID="-h ${db_host} -P ${db_port} -u ${db_username} -p${db_password}"

# 清表数据SQL
TRUNCATE_SQL="truncate table tp_ncbs_${org_table_name};"
TABLE_COLUMNS=--columns=字段列表
```

**改造后：**
```bash
# 删除所有数据库相关配置，由Java API负责
```

#### 3.1.2 数据导入方式改造
**改造前：**
```bash
# 清空表数据
/edb/app/mysql-5.7/bin/mysql ${DB_USERID} -B ${db_name} -e "${TRUNCATE_SQL}"
# 导入数据
/edb/app/mysql-5.7/bin/mysqlimport --socket=/mysqldata/3309/socket/mysql.sock ${DB_USERID} ${db_name} --fields-terminated-by='|+|' ${TABLE_COLUMNS} --local ${work_dir}/${TARGET_FILE} --verbose
```

**改造后（标准模式）：**
```bash
# Java API接口地址
api_url="http://tms-app-cdc:19201/webapi/模块名/sync"
# 超时设置
time_url="--connect-timeout 30 --max-time 1800"

# 构建请求参数
request_data=$(cat <<EOF
{
    "filePath": "${work_dir}/${TARGET_FILE}",
    "processDate": "${yesterday_with_dash}",
    "tableName": "tp_ncbs_${org_table_name}"
}
EOF
)

# 调用Java API
result=`curl -s $time_url -X POST -H "Content-Type: application/json" -d "$request_data" $api_url`
```

**改造后（复杂数据库查询模式）：**
```bash
# 第一步：调用Java API获取维护借据号
maintain_api_url="http://tms-app-cdc:19201/webapi/模块名/getMaintainLnDueBillNos"
maintain_result=`curl -s $time_url -X POST -H "Content-Type: application/json" -d "$maintain_request_data" $maintain_api_url`
# 保存结果到文件
echo "$maintain_result" | grep -o '"[^"]*"' | tr -d '"' > "$maintain_file"

# 第二步：调用Java API获取排除借据号
exclusion_api_url="http://tms-app-cdc:19201/webapi/模块名/getExclusionLnDueBillNos"
exclusion_result=`curl -s $time_url -X POST -H "Content-Type: application/json" -d "$exclusion_request_data" $exclusion_api_url`
# 保存结果到文件
echo "$exclusion_result" | grep -o '"[^"]*"' | tr -d '"' > "$exclusion_file"

# 第三步：根据查询结果过滤数据
# 过滤逻辑1：只保留在维护表中的借据号
awk -F '\\|\\+\\|' 'NR==FNR {values[$1]; next} $1 in values' "$maintain_file" "$unzip_data_file" > "${work_dir}/temp1.txt"
# 过滤逻辑2：排除在排除表中的借据号
awk -F '\\|\\+\\|' 'NR==FNR {values[$1]; next} !($1 in values)' "$exclusion_file" "${work_dir}/temp1.txt" > "${work_dir}/temp2.txt"

# 第四步：调用数据导入API
api_url="http://tms-app-cdc:19201/webapi/模块名/sync"
result=`curl -s $time_url -X POST -H "Content-Type: application/json" -d "$request_data" $api_url`
```

#### 3.1.3 返回码规范
**改造前：**
```bash
echo 1  # 失败返回1
echo 0  # 成功返回0
```

**改造后：**
```bash
fail='-1'    # 失败返回-1
success='0'  # 成功返回0
```

#### 3.1.4 字符编码设置
**改造后新增：**
```bash
# 设置字符编码
export LANG=zh_CN.UTF-8
```

### 3.2 Java服务改造

#### 3.2.1 Controller层增强
**标准sync接口：**
```java
@Path("/sync")
@POST
@Consumes("application/json")
@Produces("application/json")
public ResultVo<DataSyncResult> sync(DataSyncRequest request) {
    log.info(StrUtil.format("开始处理数据同步请求: {}", JSON.toJSONString(request)));
    
    try {
        DataSyncResult result = service.syncData(request);
        log.info(StrUtil.format("数据同步完成: {}", JSON.toJSONString(result)));
        return ResultVo.success(result);
    } catch (Exception e) {
        log.error(StrUtil.format("数据同步失败: {}", e.getMessage()), e);
        return ResultVo.failure("数据同步失败: " + e.getMessage());
    }
}
```

**复杂啳景新增接口（针对需要多次数据库查询的情况）：**
```java
@Path("/getMaintainLnDueBillNos")
@POST
@Consumes("application/json")
@Produces("application/json")
public ResultVo<List<String>> getMaintainLnDueBillNos(DataSyncRequest request) throws Exception {
    log.info(StrUtil.format("开始获取维护借据号列表: {}", JSON.toJSONString(request)));
    
    try {
        List<String> lnDueBillNos = service.getMaintainLnDueBillNos();
        log.info(StrUtil.format("获取维护借据号完成，数量: {}", lnDueBillNos.size()));
        return ResultVo.success(lnDueBillNos);
    } catch (Exception e) {
        log.error(StrUtil.format("获取维护借据号失败: {}", e.getMessage()), e);
        return ResultVo.failure("获取维护借据号失败: " + e.getMessage());
    }
}

@Path("/getExclusionLnDueBillNos")
@POST
@Consumes("application/json")
@Produces("application/json")
public ResultVo<List<String>> getExclusionLnDueBillNos(DataSyncRequest request) throws Exception {
    log.info(StrUtil.format("开始获取排除借据号列表: {}", JSON.toJSONString(request)));
    
    try {
        List<String> lnDueBillNos = service.getExclusionLnDueBillNos();
        log.info(StrUtil.format("获取排除借据号完成，数量: {}", lnDueBillNos.size()));
        return ResultVo.success(lnDueBillNos);
    } catch (Exception e) {
        log.error(StrUtil.format("获取排除借据号失败: {}", e.getMessage()), e);
        return ResultVo.failure("获取排除借据号失败: " + e.getMessage());
    }
}
```

#### 3.2.2 Service层实现数据导入
**核心功能：**
1. 文件验证和读取
2. 数据解析和转换
3. 批量数据导入
4. 事务管理
5. 异常处理

```java
@Override
@Transactional(rollbackFor = Exception.class, value = "utaxTransactionManager")
public DataSyncResult syncData(DataSyncRequest request) throws Exception {
    // 1. 文件验证
    File dataFile = new File(request.getFilePath());
    if (!dataFile.exists()) {
        throw new RuntimeException("数据文件不存在: " + request.getFilePath());
    }

    // 2. 清空表数据
    String truncateSql = "TRUNCATE TABLE " + request.getTableName();
    jdbcTemplate.execute(truncateSql);

    // 3. 读取文件内容
    List<String> lines = FileUtil.readLines(dataFile, StandardCharsets.UTF_8);

    // 4. 批量处理数据
    List<Entity> batchList = new ArrayList<>();
    for (String line : lines) {
        if (StrUtil.isBlank(line)) continue;
        
        String[] fields = line.split(DATA_SEPARATOR);
        Entity entity = parseLineToEntity(fields);
        batchList.add(entity);
        
        if (batchList.size() >= BATCH_SIZE) {
            saveAll(batchList);
            batchList.clear();
        }
    }
    
    // 5. 处理剩余数据
    if (!batchList.isEmpty()) {
        saveAll(batchList);
    }

    return DataSyncResult.success(totalCount, processedCount);
}
```

#### 3.2.3 数据传输对象
**DataSyncRequest:**
```java
public class DataSyncRequest {
    private String filePath;      // 文件路径
    private String processDate;   // 处理日期
    private String tableName;     // 表名
}
```

**DataSyncResult:**
```java
public class DataSyncResult {
    private boolean success;      // 是否成功
    private String message;       // 消息
    private int totalCount;       // 总记录数
    private int processedCount;   // 处理记录数
    private long processTime;     // 处理耗时
}
```

### 3.3 技术栈使用规范

#### 3.3.1 日志记录
```java
// 使用StrUtil.format和JSON.toJSONString
log.info(StrUtil.format("开始处理请求: {}", JSON.toJSONString(request)));
log.error(StrUtil.format("处理失败: {}", e.getMessage()), e);
```

#### 3.3.2 导入规范
```java
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
```

## 4. 改造优势

### 4.1 架构优势
1. **职责分离**：Shell专注文件处理，Java专注业务逻辑
2. **可维护性**：Java代码更易维护和调试
3. **扩展性**：便于添加新的业务逻辑和验证规则
4. **事务管理**：Java提供更完善的事务控制

### 4.2 技术优势
1. **异常处理**：Java提供更完善的异常处理机制
2. **日志管理**：统一的日志记录规范
3. **性能优化**：批量处理和连接池优化
4. **监控支持**：便于集成监控和告警

### 4.3 运维优势
1. **错误定位**：更详细的错误信息和堆栈跟踪
2. **性能监控**：可以监控API调用性能
3. **扩展部署**：Java服务可以水平扩展
4. **统一管理**：纳入微服务管理体系

## 5. 改造标准流程

### 5.1 Shell脚本改造步骤
1. 保留文件处理逻辑（解压、过滤、重命名）
2. 删除数据库连接配置
3. 删除mysqlimport调用
4. 添加HTTP API调用
5. 统一返回码规范
6. 添加字符编码设置

### 5.2 Java服务改造步骤
1. Controller添加sync接口
2. Service添加syncData方法
3. 实现文件读取和数据解析
4. 实现批量数据导入
5. 添加事务管理
6. 完善异常处理和日志记录

### 5.3 测试验证
1. 单元测试：验证数据解析和转换逻辑
2. 集成测试：验证Shell与Java API集成
3. 性能测试：验证大数据量处理性能
4. 异常测试：验证各种异常情况处理

## 6. 注意事项

### 6.1 数据一致性
- 使用事务确保数据导入的原子性
- 先清空表再导入，避免数据重复

### 6.2 性能优化
- 批量处理大数据文件（BATCH_SIZE=1000）
- 合理设置API超时时间
- 优化SQL执行性能

### 6.3 错误处理
- 文件不存在或无法读取时的处理
- 数据格式错误时的处理
- 网络异常时的重试机制

### 6.4 兼容性
- 保持原有的文件路径和命名规范
- 确保日志格式的一致性
- 维护相同的返回码规范

## 7. 部署配置

### 7.1 API地址配置
```bash
# 根据环境配置对应的API地址
api_url="http://tms-app-cdc:19201/webapi/模块名/sync"
```

### 7.2 超时配置
```bash
# 连接超时30秒，总超时30分钟
time_url="--connect-timeout 30 --max-time 1800"
```

## 8. 监控和运维

### 8.1 日志监控
- Shell脚本日志：`/cebtms/files/logs/batch/`
- Java服务日志：应用服务器日志目录

### 8.2 性能监控
- API调用响应时间
- 数据处理量统计
- 错误率监控

### 8.3 告警机制
- 文件处理失败告警
- API调用超时告警
- 数据导入异常告警

---

## 总结

本次改造实现了Shell脚本与Java服务的有效分离，提高了系统的可维护性、扩展性和监控能力。Shell专注于文件处理，Java专注于业务逻辑，通过HTTP API进行通信，形成了清晰的架构边界和职责分工。