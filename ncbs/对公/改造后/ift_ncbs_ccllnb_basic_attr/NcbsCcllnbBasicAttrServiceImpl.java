package com.cebbank.tms.wenjing.ift.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import com.cebbank.tms.wenjing.common.util.BeanCopyUtil;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.ift.dao.NcbsCcllnbBasicAttrRepository;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbBasicAttr;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnbBasicAttrService;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbBasicAttrOutVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class NcbsCcllnbBasicAttrServiceImpl extends BaseServiceImpl<NcbsCcllnbBasicAttrRepository, NcbsCcllnbBasicAttr, Long> implements NcbsCcllnbBasicAttrService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnbBasicAttrServiceImpl.class);
    
    @Resource
    private NcbsCcllnbBasicAttrRepository repository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private static final String DATA_SEPARATOR = "\\|\\+\\|";
    private static final int BATCH_SIZE = 1000;
    private static final int LN_BIZ_CLAS_COL = 4; // 贷款业务分类所在列（索引4）

    @Override
    public Page<NcbsCcllnbBasicAttrOutVO> listPage(BaseQuery<NcbsCcllnbBasicAttr> vo, UserVO userVO) throws Exception {
        Page<NcbsCcllnbBasicAttr> page = super.listPage(vo);
        List<NcbsCcllnbBasicAttr> list = page.getContent();
        List<NcbsCcllnbBasicAttrOutVO> listVo = new ArrayList<>();
        
        for (NcbsCcllnbBasicAttr entity : list) {
            NcbsCcllnbBasicAttrOutVO outVO = new NcbsCcllnbBasicAttrOutVO();
            BeanCopyUtil.copyObject(entity, outVO);
            listVo.add(outVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    @Override
    public NcbsCcllnbBasicAttrOutVO queryInfoById(Long id) throws Exception {
        NcbsCcllnbBasicAttr entity = this.repository.queryById(id);
        NcbsCcllnbBasicAttrOutVO vo = new NcbsCcllnbBasicAttrOutVO();
        BeanCopyUtil.copyObject(entity, vo);
        return vo;
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public NcbsCcllnbBasicAttr saveOrUpdate(NcbsCcllnbBasicAttr entity, UserVO userVO) throws Exception {
        if (Objects.isNull(entity.getId())) {
            entity.setCreateTime(new Date());
            entity.setCreateUserId(userVO.getUserId());
            entity.setCreateUserName(userVO.getFullName());
        }
        entity.setUpdateTime(new Date());
        entity.setUpdateUserId(userVO.getUserId());
        entity.setUpdateUserName(userVO.getFullName());
        return super.saveOrUpdate(entity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class, value = "utaxTransactionManager")
    public void deleteAllByIds(List<Long> ids) throws Exception {
        for (Long id : ids) {
            this.deleteById(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, value = "utaxTransactionManager")
    public DataSyncResult syncData(DataSyncRequest request) throws Exception {
        log.info(StrUtil.format("开始同步对公贷款属性表数据，文件路径: {}", request.getFilePath()));

        File dataFile = new File(request.getFilePath());
        if (!dataFile.exists()) {
            throw new RuntimeException("数据文件不存在: " + request.getFilePath());
        }

        long startTime = System.currentTimeMillis();
        
        // 清空表数据
        jdbcTemplate.execute("TRUNCATE TABLE " + request.getTableName());

        // 读取并处理文件
        List<String> lines = FileUtil.readLines(dataFile, StandardCharsets.UTF_8);
        
        if (lines.isEmpty()) {
           return DataSyncResult.success(0L, request.getTableName());
        }

        List<NcbsCcllnbBasicAttr> entityList = new ArrayList<>();
        int validCount = 0;
        int filteredCount = 0;

        for (String line : lines) {
            if (StrUtil.isBlank(line)) continue;
            
            String[] fields = line.split(DATA_SEPARATOR);
            
            // 过滤逻辑：只保留ln_biz_clas等于2或3的数据
            if (fields.length > LN_BIZ_CLAS_COL) {
                String lnBizClas = fields[LN_BIZ_CLAS_COL];
                if (!"2".equals(lnBizClas) && !"3".equals(lnBizClas)) {
                    filteredCount++;
                    continue;
                }
            }
            
            NcbsCcllnbBasicAttr entity = new NcbsCcllnbBasicAttr();
            entity.setLnDueBillNo(fields.length > 0 ? fields[0] : null);
            if (fields.length > LN_BIZ_CLAS_COL) {
                entity.setLnBizClas(fields[LN_BIZ_CLAS_COL]);
            }
            entity.setCreateTime(new Date());
            entity.setRecordStat("1");
            
            entityList.add(entity);
            validCount++;

            if (entityList.size() >= BATCH_SIZE) {
                saveAll(entityList);
                entityList.clear();
            }
        }

        if (!entityList.isEmpty()) {
            saveAll(entityList);
        }

        log.info(StrUtil.format("对公贷款属性表数据同步完成，总行数: {}, 有效数据: {}, 过滤数据: {}", 
                lines.size(), validCount, filteredCount));

       return DataSyncResult.success((long)validCount, request.getTableName());
    }
}