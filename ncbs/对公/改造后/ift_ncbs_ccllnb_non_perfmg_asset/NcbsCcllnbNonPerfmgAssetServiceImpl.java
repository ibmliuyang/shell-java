package com.cebbank.tms.wenjing.ift.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import com.cebbank.tms.wenjing.common.util.BeanCopyUtil;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.ift.dao.NcbsCcllnbNonPerfmgAssetRepository;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbNonPerfmgAsset;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnbNonPerfmgAssetService;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbNonPerfmgAssetOutVO;
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
public class NcbsCcllnbNonPerfmgAssetServiceImpl extends BaseServiceImpl<NcbsCcllnbNonPerfmgAssetRepository, NcbsCcllnbNonPerfmgAsset, Long> implements NcbsCcllnbNonPerfmgAssetService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnbNonPerfmgAssetServiceImpl.class);
    
    @Resource
    private NcbsCcllnbNonPerfmgAssetRepository repository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private static final String DATA_SEPARATOR = "\\|\\+\\|";
    private static final int BATCH_SIZE = 1000;

    @Override
    public Page<NcbsCcllnbNonPerfmgAssetOutVO> listPage(BaseQuery<NcbsCcllnbNonPerfmgAsset> vo, UserVO userVO) throws Exception {
        Page<NcbsCcllnbNonPerfmgAsset> page = super.listPage(vo);
        List<NcbsCcllnbNonPerfmgAsset> list = page.getContent();
        List<NcbsCcllnbNonPerfmgAssetOutVO> listVo = new ArrayList<>();
        
        for (NcbsCcllnbNonPerfmgAsset entity : list) {
            NcbsCcllnbNonPerfmgAssetOutVO outVO = new NcbsCcllnbNonPerfmgAssetOutVO();
            BeanCopyUtil.copyObject(entity, outVO);
            listVo.add(outVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    @Override
    public NcbsCcllnbNonPerfmgAssetOutVO queryInfoById(Long id) throws Exception {
        NcbsCcllnbNonPerfmgAsset entity = this.repository.queryById(id);
        NcbsCcllnbNonPerfmgAssetOutVO vo = new NcbsCcllnbNonPerfmgAssetOutVO();
        BeanCopyUtil.copyObject(entity, vo);
        return vo;
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public NcbsCcllnbNonPerfmgAsset saveOrUpdate(NcbsCcllnbNonPerfmgAsset entity, UserVO userVO) throws Exception {
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
        log.info(StrUtil.format("开始同步对公贷款不良资产表数据，文件路径: {}", request.getFilePath()));

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

        List<NcbsCcllnbNonPerfmgAsset> entityList = new ArrayList<>();
        int validCount = 0;

        for (String line : lines) {
            if (StrUtil.isBlank(line)) continue;
            
            String[] fields = line.split(DATA_SEPARATOR);
            NcbsCcllnbNonPerfmgAsset entity = new NcbsCcllnbNonPerfmgAsset();
            entity.setLnDueBillNo(fields.length > 0 ? fields[0] : null);
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

       return DataSyncResult.success((long)validCount, request.getTableName());
    }
}