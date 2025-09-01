package com.cebbank.tms.wenjing.ift.service.impl;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbNonPerfmgAsset;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbNonPerfmgAssetOutVO;

import com.cebbank.tms.wenjing.ift.dao.NcbsCcllnbNonPerfmgAssetRepository;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnbNonPerfmgAssetService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import java.util.Date;
import com.cebbank.tms.wenjing.common.util.BeanCopyUtil;
import java.util.List;
import java.util.Objects;
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
 * 资产损失:临时表-对公贷款不良资产登记簿表服务实现类
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
public class NcbsCcllnbNonPerfmgAssetServiceImpl extends BaseServiceImpl<NcbsCcllnbNonPerfmgAssetRepository, NcbsCcllnbNonPerfmgAsset, Long> implements NcbsCcllnbNonPerfmgAssetService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnbNonPerfmgAssetServiceImpl.class);
    
    @Resource
    private NcbsCcllnbNonPerfmgAssetRepository ncbsCcllnbNonPerfmgAssetRepository;


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
    public Page<NcbsCcllnbNonPerfmgAssetOutVO> listPage(BaseQuery<NcbsCcllnbNonPerfmgAsset> vo, UserVO userVO) throws Exception {
        Page<NcbsCcllnbNonPerfmgAsset> page =  super.listPage(vo);
       //查询结果转换成vo返回
        List<NcbsCcllnbNonPerfmgAsset> list = page.getContent();
        List<NcbsCcllnbNonPerfmgAssetOutVO> listVo = new ArrayList<>();
        NcbsCcllnbNonPerfmgAssetOutVO ncbsCcllnbNonPerfmgAssetOutVO = null;

   
        for (NcbsCcllnbNonPerfmgAsset ncbsCcllnbNonPerfmgAsset : list) {
            ncbsCcllnbNonPerfmgAssetOutVO = new NcbsCcllnbNonPerfmgAssetOutVO();
            BeanCopyUtil.copyObject(ncbsCcllnbNonPerfmgAsset, ncbsCcllnbNonPerfmgAssetOutVO);
            listVo.add(ncbsCcllnbNonPerfmgAssetOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCcllnbNonPerfmgAssetOutVO
    * @throws Exception
    * @see
    * @since
    */
    @Override
    public NcbsCcllnbNonPerfmgAssetOutVO queryInfoById(Long id) throws Exception{
        NcbsCcllnbNonPerfmgAsset ncbsCcllnbNonPerfmgAsset = this.ncbsCcllnbNonPerfmgAssetRepository.queryById(id);
        NcbsCcllnbNonPerfmgAssetOutVO vo = new NcbsCcllnbNonPerfmgAssetOutVO();
        BeanCopyUtil.copyObject(ncbsCcllnbNonPerfmgAsset, vo);
                
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
        ncbsCcllnbNonPerfmgAssetRepository.deleteById(id);
    }
    
    
    /**
    * 保存修改
        * @param ncbsCcllnbNonPerfmgAsset 实体
        * @param userVO 用户
        * @return  NcbsCcllnbNonPerfmgAsset
    * @throws 
    * @see
    * @since
    */
    @Override
    public NcbsCcllnbNonPerfmgAsset saveOrUpdate(NcbsCcllnbNonPerfmgAsset ncbsCcllnbNonPerfmgAsset,UserVO userVO) throws Exception{
        if(Objects.isNull(ncbsCcllnbNonPerfmgAsset.getId())){
    
            ncbsCcllnbNonPerfmgAsset.setCreateTime(new Date());
            ncbsCcllnbNonPerfmgAsset.setCreateUserId(userVO.getUserId());
            ncbsCcllnbNonPerfmgAsset.setCreateUserName(userVO.getFullName());
                   
        }else{
            // 更新
            NcbsCcllnbNonPerfmgAsset temp = selectById(ncbsCcllnbNonPerfmgAsset.getId());
            ncbsCcllnbNonPerfmgAsset.setCreateTime(temp.getCreateTime());
            ncbsCcllnbNonPerfmgAsset.setCreateUserId(temp.getCreateUserId());
            ncbsCcllnbNonPerfmgAsset.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCcllnbNonPerfmgAsset.setUpdateTime(new Date());
        ncbsCcllnbNonPerfmgAsset.setUpdateUserId(userVO.getUserId());
        ncbsCcllnbNonPerfmgAsset.setUpdateUserName(userVO.getFullName());
                
        return super.saveOrUpdate(ncbsCcllnbNonPerfmgAsset);
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
     


}