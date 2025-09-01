package com.cebbank.tms.wenjing.ift.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import com.cebbank.tms.wenjing.common.util.BeanCopyUtil;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.ift.dao.NcbsCcllnbChkWriteOffRepository;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbChkWriteOff;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnbChkWriteOffService;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbChkWriteOffOutVO;
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
public class NcbsCcllnbChkWriteOffServiceImpl extends BaseServiceImpl<NcbsCcllnbChkWriteOffRepository, NcbsCcllnbChkWriteOff, Long> implements NcbsCcllnbChkWriteOffService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnbChkWriteOffServiceImpl.class);
    
    @Resource
    private NcbsCcllnbChkWriteOffRepository repository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private static final String DATA_SEPARATOR = "\\|\\+\\|";
    private static final int BATCH_SIZE = 1000;

    @Override
    public Page<NcbsCcllnbChkWriteOffOutVO> listPage(BaseQuery<NcbsCcllnbChkWriteOff> vo, UserVO userVO) throws Exception {
        Page<NcbsCcllnbChkWriteOff> page = super.listPage(vo);
        List<NcbsCcllnbChkWriteOff> list = page.getContent();
        List<NcbsCcllnbChkWriteOffOutVO> listVo = new ArrayList<>();
        
        for (NcbsCcllnbChkWriteOff entity : list) {
            NcbsCcllnbChkWriteOffOutVO outVO = new NcbsCcllnbChkWriteOffOutVO();
            BeanCopyUtil.copyObject(entity, outVO);
            listVo.add(outVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    @Override
    public NcbsCcllnbChkWriteOffOutVO queryInfoById(Long id) throws Exception {
        NcbsCcllnbChkWriteOff entity = this.repository.queryById(id);
        NcbsCcllnbChkWriteOffOutVO vo = new NcbsCcllnbChkWriteOffOutVO();
        BeanCopyUtil.copyObject(entity, vo);
        return vo;
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    public NcbsCcllnbChkWriteOff saveOrUpdate(NcbsCcllnbChkWriteOff entity, UserVO userVO) throws Exception {
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
        log.info(StrUtil.format("开始同步对公贷款核销表数据，文件路径: {}", request.getFilePath()));

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

        List<NcbsCcllnbChkWriteOff> entityList = new ArrayList<>();
        int validCount = 0;

        for (String line : lines) {
            if (StrUtil.isBlank(line)) continue;
            
            String[] fields = line.split(DATA_SEPARATOR);
            NcbsCcllnbChkWriteOff entity = new NcbsCcllnbChkWriteOff();
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