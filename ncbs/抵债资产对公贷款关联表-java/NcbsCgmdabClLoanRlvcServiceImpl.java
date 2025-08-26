package com.cebbank.tms.wenjing.ift.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import com.cebbank.tms.wenjing.common.util.BeanCopyUtil;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.ift.dao.NcbsCgmdabClLoanRlvcRepository;
import com.cebbank.tms.wenjing.ift.domain.NcbsCgmdabClLoanRlvc;
import com.cebbank.tms.wenjing.ift.service.NcbsCgmdabClLoanRlvcService;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCgmdabClLoanRlvcOutVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 资产损失:临时表-抵债资产对公贷款关联表表服务实现类
 * <pre>
 *
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-20 09:43:07
 */
@Service
public class NcbsCgmdabClLoanRlvcServiceImpl extends BaseServiceImpl<NcbsCgmdabClLoanRlvcRepository, NcbsCgmdabClLoanRlvc, Long> implements NcbsCgmdabClLoanRlvcService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCgmdabClLoanRlvcServiceImpl.class);

    @Resource
    private NcbsCgmdabClLoanRlvcRepository ncbsCgmdabClLoanRlvcRepository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 数据字段分隔符
     */
    private static final String DATA_SEPARATOR = "\\|\\+\\|";

    /**
     * 批量插入大小
     */
    private static final int BATCH_SIZE = 1000;


    /**
    * 列表查询
        * @param vo 查询条件
        * @param userVO 用户
        * @return Page
    * @throws Exception
    * @see
    * @since
    */
    @Override
    public Page<NcbsCgmdabClLoanRlvcOutVO> listPage(BaseQuery<NcbsCgmdabClLoanRlvc> vo, UserVO userVO) throws Exception {
        Page<NcbsCgmdabClLoanRlvc> page =  super.listPage(vo);
       //查询结果转换成vo返回
        List<NcbsCgmdabClLoanRlvc> list = page.getContent();
        List<NcbsCgmdabClLoanRlvcOutVO> listVo = new ArrayList<>();
        NcbsCgmdabClLoanRlvcOutVO ncbsCgmdabClLoanRlvcOutVO = null;


        for (NcbsCgmdabClLoanRlvc ncbsCgmdabClLoanRlvc : list) {
            ncbsCgmdabClLoanRlvcOutVO = new NcbsCgmdabClLoanRlvcOutVO();
            BeanCopyUtil.copyObject(ncbsCgmdabClLoanRlvc, ncbsCgmdabClLoanRlvcOutVO);
            listVo.add(ncbsCgmdabClLoanRlvcOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }


    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCgmdabClLoanRlvcOutVO
    * @throws Exception
    * @see
    * @since
    */
    @Override
    public NcbsCgmdabClLoanRlvcOutVO queryInfoById(Long id) throws Exception{
        NcbsCgmdabClLoanRlvc ncbsCgmdabClLoanRlvc = this.ncbsCgmdabClLoanRlvcRepository.queryById(id);
        NcbsCgmdabClLoanRlvcOutVO vo = new NcbsCgmdabClLoanRlvcOutVO();
        BeanCopyUtil.copyObject(ncbsCgmdabClLoanRlvc, vo);

        return vo;
    }


    /**
    * 根据ID删除
        * @param id 主键
        * @return
    * @throws
    * @see
    * @since
    */
    @Override
    public void deleteById(Long id) {
        ncbsCgmdabClLoanRlvcRepository.deleteById(id);
    }


    /**
    * 保存修改
        * @param ncbsCgmdabClLoanRlvc 实体
        * @param userVO 用户
        * @return  NcbsCgmdabClLoanRlvc
    * @throws
    * @see
    * @since
    */
    @Override
    public NcbsCgmdabClLoanRlvc saveOrUpdate(NcbsCgmdabClLoanRlvc ncbsCgmdabClLoanRlvc,UserVO userVO) throws Exception{
        if(Objects.isNull(ncbsCgmdabClLoanRlvc.getId())){

            ncbsCgmdabClLoanRlvc.setCreateTime(new Date());
            ncbsCgmdabClLoanRlvc.setCreateUserId(userVO.getUserId());
            ncbsCgmdabClLoanRlvc.setCreateUserName(userVO.getFullName());

        }else{
            // 更新
            NcbsCgmdabClLoanRlvc temp = selectById(ncbsCgmdabClLoanRlvc.getId());
            ncbsCgmdabClLoanRlvc.setCreateTime(temp.getCreateTime());
            ncbsCgmdabClLoanRlvc.setCreateUserId(temp.getCreateUserId());
            ncbsCgmdabClLoanRlvc.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCgmdabClLoanRlvc.setUpdateTime(new Date());
        ncbsCgmdabClLoanRlvc.setUpdateUserId(userVO.getUserId());
        ncbsCgmdabClLoanRlvc.setUpdateUserName(userVO.getFullName());

        return super.saveOrUpdate(ncbsCgmdabClLoanRlvc);
    }


    /**
    * 批量删除
        * @param ids 主键集合
        * @return
    * @throws Exception
    * @see
    * @since
    */
     @Override
     @Transactional(rollbackFor = Exception.class, value = "utaxTransactionManager")
     public void deleteAllByIds(List<Long> ids) throws Exception{
        for(Long id:ids){
            this.deleteById(id);
        }
    }

    /**
     * 数据同步
     * @param request 数据同步请求
     * @return DataSyncResult
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = "utaxTransactionManager")
    public DataSyncResult syncData(DataSyncRequest request) throws Exception {
        log.info(StrUtil.format("开始同步数据，文件路径: {}, 处理日期: {}, 表名: {}",   request.getFilePath(), request.getProcessDate(), request.getTableName()));

        // 验证文件是否存在
        File dataFile = new File(request.getFilePath());
        if (!dataFile.exists()) {
            throw new RuntimeException("数据文件不存在: " + request.getFilePath());
        }

        if (!dataFile.canRead()) {
            throw new RuntimeException("数据文件无法读取: " + request.getFilePath());
        }

        try {
            // 1. 清空目标表
            truncateTable(request.getTableName());

            // 2. 读取文件并插入数据
            long importedRows = importDataFromFile(dataFile, request.getTableName());

            // 3. 返回结果
            DataSyncResult result = new DataSyncResult();
            result.setImportedRows(importedRows);
            result.setProcessTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.setTableName(request.getTableName());
            log.info(StrUtil.format("数据同步完成，导入记录数: {}",  importedRows));
            return result;

        } catch (Exception e) {
            log.error("数据同步失败", e);
            throw new RuntimeException("数据同步失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清空目标表
     *
     * @param tableName 表名
     */
    private void truncateTable(String tableName) {
        log.info(StrUtil.format("清空表数据: {}",  tableName));
        String sql = "TRUNCATE TABLE " + tableName;
        jdbcTemplate.execute(sql);
        log.info(StrUtil.format("表数据清空完成: {}",  tableName));
    }

    /**
     * 从文件导入数据
     *
     * @param dataFile  数据文件
     * @param tableName 目标表名
     * @return 导入记录数
     */
    private long importDataFromFile(File dataFile, String tableName) {
        log.info(StrUtil.format("开始从文件导入数据: {}",  dataFile.getAbsolutePath()));
        // 读取文件内容
        List<String> lines = FileUtil.readLines(dataFile, StandardCharsets.UTF_8);

        long totalRows = 0;
        String insertSql = buildInsertSql(tableName);

        List<Object[]> batchArgs = new ArrayList<>();

        for (String line : lines) {
            if (StrUtil.isBlank(line)) {
                continue;
            }

            // 解析数据行
            String[] fields = line.split(DATA_SEPARATOR, -1);
            if (fields.length != 49) {
                log.info(StrUtil.format("数据行字段数不正确，期望49个字段，实际{}个字段: {}", fields.length , JSON.toJSONString(line)));
                continue;
            }

            // 处理字段数据（去除前后空格，空值转null）
            Object[] values = new Object[49];
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i].trim();
                values[i] = StrUtil.isBlank(field) ? null : field;
            }

            batchArgs.add(values);

            // 批量执行
            if (batchArgs.size() >= BATCH_SIZE) {
                jdbcTemplate.batchUpdate(insertSql, batchArgs);
                totalRows += batchArgs.size();
                batchArgs.clear();
                log.info(StrUtil.format("批量插入完成，当前总记录数:  {}", JSON.toJSONString(totalRows)));
            }
        }

        // 处理剩余数据
        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(insertSql, batchArgs);
            totalRows += batchArgs.size();
        }
        log.info(StrUtil.format("文件数据导入完成，总记录数: {}", JSON.toJSONString(totalRows)));
        return totalRows;
    }

    /**
     * 构建插入SQL语句
     *
     * @param tableName 表名
     * @return 插入SQL
     */
    private String buildInsertSql(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" (");

        // 添加字段名
        String[] columnNames = {
                "commute_debt_ast_no", "ln_due_bill_no", "dtl_seri_no", "cred_ast_impai_provis",
                "inte_acct_acct_no", "rcv_cost", "pay_comp_price", "repay_pri_amt",
                "repay_int_amt", "commute_debt_ast_assoc_stat", "depr_retu_pri", "depr_retu_int",
                "retu_regu_pri", "retu_overd_pri", "retu_dead_ln_pri", "retu_bad_debt_pri",
                "retu_rece_accru_int", "retu_rece_deb_int", "retu_rece_accru_pena_int", "retu_rece_pena_int",
                "retu_on_bal_accru_compou_int", "retu_on_bal_compou_int", "retu_wri_off_pri", "cred_ast_impai_provis_acct_no",
                "to_be_deal_with_commute_debt_ast_occ_amt", "contr_no", "cust_no", "revol_fund_amt",
                "revol_amt1", "revol_amt2", "rese_fie", "rese_fie_1",
                "rese_fie_2", "dispo_cost", "to_be_retu_dispo_cost", "retu_col_accru_int",
                "retu_col_deb_int", "retu_col_accru_pena_int", "retu_col_pena_int", "retu_off_bal_accru_compou_int",
                "retu_off_bal_compou_int", "retu_wri_off_int", "retu_alr_wri_off_pri_int", "retu_wri_off_int_compou_int",
                "mainte_tell", "mainte_inst", "mainte_dt", "mainte_tm", "record_stat"
        };

        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(columnNames[i]);
        }

        sql.append(") VALUES (");

        // 添加占位符
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }

        sql.append(")");
        log.info(StrUtil.format("构建的插入SQL:{}", sql.toString()));
        return sql.toString();
    }


}