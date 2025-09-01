package com.cebbank.tms.wenjing.ift.service.impl;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbChkWriteOff;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbChkWriteOffOutVO;

import com.cebbank.tms.wenjing.ift.dao.NcbsCcllnbChkWriteOffRepository;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnbChkWriteOffService;
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
 * 资产损失:临时表-对公贷款核销登记簿表服务实现类
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
public class NcbsCcllnbChkWriteOffServiceImpl extends BaseServiceImpl<NcbsCcllnbChkWriteOffRepository, NcbsCcllnbChkWriteOff, Long> implements NcbsCcllnbChkWriteOffService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnbChkWriteOffServiceImpl.class);
    
    @Resource
    private NcbsCcllnbChkWriteOffRepository ncbsCcllnbChkWriteOffRepository;


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
    public Page<NcbsCcllnbChkWriteOffOutVO> listPage(BaseQuery<NcbsCcllnbChkWriteOff> vo, UserVO userVO) throws Exception {
        Page<NcbsCcllnbChkWriteOff> page =  super.listPage(vo);
       //查询结果转换成vo返回
        List<NcbsCcllnbChkWriteOff> list = page.getContent();
        List<NcbsCcllnbChkWriteOffOutVO> listVo = new ArrayList<>();
        NcbsCcllnbChkWriteOffOutVO ncbsCcllnbChkWriteOffOutVO = null;

   
        for (NcbsCcllnbChkWriteOff ncbsCcllnbChkWriteOff : list) {
            ncbsCcllnbChkWriteOffOutVO = new NcbsCcllnbChkWriteOffOutVO();
            BeanCopyUtil.copyObject(ncbsCcllnbChkWriteOff, ncbsCcllnbChkWriteOffOutVO);
            listVo.add(ncbsCcllnbChkWriteOffOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCcllnbChkWriteOffOutVO
    * @throws Exception
    * @see
    * @since
    */
    @Override
    public NcbsCcllnbChkWriteOffOutVO queryInfoById(Long id) throws Exception{
        NcbsCcllnbChkWriteOff ncbsCcllnbChkWriteOff = this.ncbsCcllnbChkWriteOffRepository.queryById(id);
        NcbsCcllnbChkWriteOffOutVO vo = new NcbsCcllnbChkWriteOffOutVO();
        BeanCopyUtil.copyObject(ncbsCcllnbChkWriteOff, vo);
                
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
        ncbsCcllnbChkWriteOffRepository.deleteById(id);
    }
    
    
    /**
    * 保存修改
        * @param ncbsCcllnbChkWriteOff 实体
        * @param userVO 用户
        * @return  NcbsCcllnbChkWriteOff
    * @throws 
    * @see
    * @since
    */
    @Override
    public NcbsCcllnbChkWriteOff saveOrUpdate(NcbsCcllnbChkWriteOff ncbsCcllnbChkWriteOff,UserVO userVO) throws Exception{
        if(Objects.isNull(ncbsCcllnbChkWriteOff.getId())){
    
            ncbsCcllnbChkWriteOff.setCreateTime(new Date());
            ncbsCcllnbChkWriteOff.setCreateUserId(userVO.getUserId());
            ncbsCcllnbChkWriteOff.setCreateUserName(userVO.getFullName());
                   
        }else{
            // 更新
            NcbsCcllnbChkWriteOff temp = selectById(ncbsCcllnbChkWriteOff.getId());
            ncbsCcllnbChkWriteOff.setCreateTime(temp.getCreateTime());
            ncbsCcllnbChkWriteOff.setCreateUserId(temp.getCreateUserId());
            ncbsCcllnbChkWriteOff.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCcllnbChkWriteOff.setUpdateTime(new Date());
        ncbsCcllnbChkWriteOff.setUpdateUserId(userVO.getUserId());
        ncbsCcllnbChkWriteOff.setUpdateUserName(userVO.getFullName());
                
        return super.saveOrUpdate(ncbsCcllnbChkWriteOff);
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