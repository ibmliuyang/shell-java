package com.cebbank.tms.wenjing.ift.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnaAcct;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnaAcctOutVO;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;

import com.cebbank.tms.wenjing.ift.dao.NcbsCcllnaAcctRepository;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnaAcctService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import com.cebbank.tms.wenjing.common.util.BeanCopyUtil;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import com.cebbank.tms.wenjing.common.jpa.Specifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageImpl;
import com.cebbank.tms.wenjing.common.base.BaseQuery;
import java.util.ArrayList;
import com.cebbank.tms.wenjing.common.util.Const;
import com.cebbank.tms.wenjing.sys.domain.SysAttachment;
import com.cebbank.tms.wenjing.sys.service.SysAttachmentService;
import com.cebbank.tms.wenjing.sys.vo.out.SysAttachmentOutVO;
import org.springframework.beans.BeanUtils;
import com.cebbank.tms.wenjing.common.constant.UTaxConstant;
import com.cebbank.tms.wenjing.common.util.ToExcel;
import java.util.LinkedHashMap;
import com.cebbank.tms.wenjing.common.util.PKUtils;

/**
 * 资产损失:临时表-对公贷款户主表表服务实现类
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:04
 */
@Service
public class NcbsCcllnaAcctServiceImpl extends BaseServiceImpl<NcbsCcllnaAcctRepository, NcbsCcllnaAcct, Long> implements NcbsCcllnaAcctService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnaAcctServiceImpl.class);
    
    @Resource
    private NcbsCcllnaAcctRepository ncbsCcllnaAcctRepository;

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
     * TABLE_COLUMNS 字段数
     */
    private static final int TABLE_COLUMNS = 77;


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
    public Page<NcbsCcllnaAcctOutVO> listPage(BaseQuery<NcbsCcllnaAcct> vo, UserVO userVO) throws Exception {
        Page<NcbsCcllnaAcct> page =  super.listPage(vo);
       //查询结果转换成vo返回
        List<NcbsCcllnaAcct> list = page.getContent();
        List<NcbsCcllnaAcctOutVO> listVo = new ArrayList<>();
        NcbsCcllnaAcctOutVO ncbsCcllnaAcctOutVO = null;

   
        for (NcbsCcllnaAcct ncbsCcllnaAcct : list) {
            ncbsCcllnaAcctOutVO = new NcbsCcllnaAcctOutVO();
            BeanCopyUtil.copyObject(ncbsCcllnaAcct, ncbsCcllnaAcctOutVO);
            listVo.add(ncbsCcllnaAcctOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCcllnaAcctOutVO
    * @throws Exception
    * @see
    * @since
    */
    @Override
    public NcbsCcllnaAcctOutVO queryInfoById(Long id) throws Exception{
        NcbsCcllnaAcct ncbsCcllnaAcct = this.ncbsCcllnaAcctRepository.queryById(id);
        NcbsCcllnaAcctOutVO vo = new NcbsCcllnaAcctOutVO();
        BeanCopyUtil.copyObject(ncbsCcllnaAcct, vo);
                
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
        ncbsCcllnaAcctRepository.deleteById(id);
    }
    
    
    /**
    * 保存修改
        * @param ncbsCcllnaAcct 实体
        * @param userVO 用户
        * @return  NcbsCcllnaAcct
    * @throws 
    * @see
    * @since
    */
    @Override
    public NcbsCcllnaAcct saveOrUpdate(NcbsCcllnaAcct ncbsCcllnaAcct,UserVO userVO) throws Exception{
        if(Objects.isNull(ncbsCcllnaAcct.getId())){
    
            ncbsCcllnaAcct.setCreateTime(new Date());
            ncbsCcllnaAcct.setCreateUserId(userVO.getUserId());
            ncbsCcllnaAcct.setCreateUserName(userVO.getFullName());
                   
        }else{
            // 更新
            NcbsCcllnaAcct temp = selectById(ncbsCcllnaAcct.getId());
            ncbsCcllnaAcct.setCreateTime(temp.getCreateTime());
            ncbsCcllnaAcct.setCreateUserId(temp.getCreateUserId());
            ncbsCcllnaAcct.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCcllnaAcct.setUpdateTime(new Date());
        ncbsCcllnaAcct.setUpdateUserId(userVO.getUserId());
        ncbsCcllnaAcct.setUpdateUserName(userVO.getFullName());
                
        return super.saveOrUpdate(ncbsCcllnaAcct);
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
        log.info(StrUtil.format("开始同步对公贷款账户主表数据，文件路径: {}, 处理日期: {}, 表名: {}", 
                request.getFilePath(), request.getProcessDate(), request.getTableName()));

        long startTime = System.currentTimeMillis();

        // 验证文件是否存在
        File dataFile = new File(request.getFilePath());
        if (!dataFile.exists()) {
            throw new RuntimeException("数据文件不存在: " + request.getFilePath());
        }

        if (!dataFile.canRead()) {
            throw new RuntimeException("数据文件无法读取: " + request.getFilePath());
        }

        try {
            // 1. 清空表数据
            String truncateSql = "TRUNCATE TABLE " + request.getTableName();
            log.info(StrUtil.format("清空表数据: {}", truncateSql));
            jdbcTemplate.execute(truncateSql);

            // 2. 读取文件内容（文件已经在Shell层过滤过）
            List<String> lines = FileUtil.readLines(dataFile, StandardCharsets.UTF_8);
            log.info(StrUtil.format("读取到文件行数: {}", lines.size()));

            if (lines.isEmpty()) {
                log.warn("数据文件为空");
                return DataSyncResult.success(0L, request.getTableName());
            }

            // 3. 解析数据并批量入库
            List<NcbsCcllnaAcct> entityList = new ArrayList<>();
            int validCount = 0;

            for (String line : lines) {
                if (StrUtil.isBlank(line)) {
                    continue;
                }

                String[] fields = line.split(DATA_SEPARATOR);
                if (fields.length < TABLE_COLUMNS) {
                    log.warn(StrUtil.format("数据格式不正确，期望{}个字段，实际{}个字段: {}", TABLE_COLUMNS, fields.length, line));
                    continue;
                }

                try {
                    NcbsCcllnaAcct entity = parseLineToEntity(fields);
                    if (entity != null) {
                        entityList.add(entity);
                        validCount++;

                        // 批量处理
                        if (entityList.size() >= BATCH_SIZE) {
                            saveEntityBatch(entityList);
                            entityList.clear();
                        }
                    }
                } catch (Exception e) {
                    log.error(StrUtil.format("解析数据行失败: {}", line), e);
                }
            }

            // 处理剩余数据
            if (!entityList.isEmpty()) {
                saveEntityBatch(entityList);
            }

            long endTime = System.currentTimeMillis();
            log.info(StrUtil.format("数据同步完成，共处理{}条数据，有效数据{}条，耗时{}ms", 
                    lines.size(), validCount, endTime - startTime));

            return DataSyncResult.success((long)validCount, request.getTableName());
        } catch (Exception e) {
            log.error("数据同步失败", e);
            throw e;
        }
    }
    
    /**
     * 获取需要维护的借据号列表
     * @return List<String>
     * @throws Exception
     */
    @Override
    public List<String> getMaintainLnDueBillNos() throws Exception {
        String sql = "select ln_due_bill_no from ncbs_ccllna_acct " +
                    "union all select ln_due_bill_no from tp_ncbs_ccllnb_chk_write_off " +
                    "union all select ln_due_bill_no from tp_ncbs_ccllnb_non_perfmg_asset " +
                    "union all select ln_due_bill_no from tp_ncbs_cgmdab_cl_loan_rlvc";
        return jdbcTemplate.queryForList(sql, String.class);
    }
    
    /**
     * 获取要过滤掉的借据号列表（贷款属性表中ln_biz_clas为2或3的）
     * @return List<String>
     * @throws Exception
     */
    @Override
    public List<String> getExclusionLnDueBillNos() throws Exception {
        String sql = "select ln_due_bill_no from tp_ncbs_ccllnb_basic_attr where ln_biz_clas in ('2', '3')";
        return jdbcTemplate.queryForList(sql, String.class);
    }
    
    /**
     * 解析数据行为实体对象
     * @param fields 数据字段数组
     * @return NcbsCcllnaAcct
     */
    private NcbsCcllnaAcct parseLineToEntity(String[] fields) {
        try {
            NcbsCcllnaAcct entity = new NcbsCcllnaAcct();
            // 这里需要根据实际的TABLE_COLUMNS映射关系来设置字段值
            // 示例：
            if (fields.length > 0 && StrUtil.isNotBlank(fields[0])) {
                entity.setLnDueBillNo(fields[0].trim());
            }
            // ... 其他字段的映射需要根据实际情况补充
            
            return entity;
        } catch (Exception e) {
            log.error(StrUtil.format("解析数据行失败: {}", JSON.toJSONString(fields)), e);
            return null;
        }
    }
    
    /**
     * 批量保存实体
     * @param entityList 实体列表
     */
    private void saveEntityBatch(List<NcbsCcllnaAcct> entityList) {
        if (entityList.isEmpty()) {
            return;
        }
        
        try {
            ncbsCcllnaAcctRepository.saveAll(entityList);
            log.debug(StrUtil.format("批量保存{}条数据成功", entityList.size()));
        } catch (Exception e) {
            log.error(StrUtil.format("批量保存{}条数据失败", entityList.size()), e);
            throw e;
        }
    }
     


}