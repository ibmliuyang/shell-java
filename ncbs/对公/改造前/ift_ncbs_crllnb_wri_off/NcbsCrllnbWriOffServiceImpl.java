package com.cebbank.tms.wenjing.ift.service.impl;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnbWriOff;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCrllnbWriOffOutVO;

import com.cebbank.tms.wenjing.ift.dao.NcbsCrllnbWriOffRepository;
import com.cebbank.tms.wenjing.ift.service.NcbsCrllnbWriOffService;
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
 * 资产损失:临时表-核销登记簿表服务实现类
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:08
 */
@Service
public class NcbsCrllnbWriOffServiceImpl extends BaseServiceImpl<NcbsCrllnbWriOffRepository, NcbsCrllnbWriOff, Long> implements NcbsCrllnbWriOffService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCrllnbWriOffServiceImpl.class);
    
    @Resource
    private NcbsCrllnbWriOffRepository ncbsCrllnbWriOffRepository;


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
    public Page<NcbsCrllnbWriOffOutVO> listPage(BaseQuery<NcbsCrllnbWriOff> vo, UserVO userVO) throws Exception {
        Page<NcbsCrllnbWriOff> page =  super.listPage(vo);
       //查询结果转换成vo返回
        List<NcbsCrllnbWriOff> list = page.getContent();
        List<NcbsCrllnbWriOffOutVO> listVo = new ArrayList<>();
        NcbsCrllnbWriOffOutVO ncbsCrllnbWriOffOutVO = null;

   
        for (NcbsCrllnbWriOff ncbsCrllnbWriOff : list) {
            ncbsCrllnbWriOffOutVO = new NcbsCrllnbWriOffOutVO();
            BeanCopyUtil.copyObject(ncbsCrllnbWriOff, ncbsCrllnbWriOffOutVO);
            listVo.add(ncbsCrllnbWriOffOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCrllnbWriOffOutVO
    * @throws Exception
    * @see
    * @since
    */
    @Override
    public NcbsCrllnbWriOffOutVO queryInfoById(Long id) throws Exception{
        NcbsCrllnbWriOff ncbsCrllnbWriOff = this.ncbsCrllnbWriOffRepository.queryById(id);
        NcbsCrllnbWriOffOutVO vo = new NcbsCrllnbWriOffOutVO();
        BeanCopyUtil.copyObject(ncbsCrllnbWriOff, vo);
                
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
        ncbsCrllnbWriOffRepository.deleteById(id);
    }
    
    
    /**
    * 保存修改
        * @param ncbsCrllnbWriOff 实体
        * @param userVO 用户
        * @return  NcbsCrllnbWriOff
    * @throws 
    * @see
    * @since
    */
    @Override
    public NcbsCrllnbWriOff saveOrUpdate(NcbsCrllnbWriOff ncbsCrllnbWriOff,UserVO userVO) throws Exception{
        if(Objects.isNull(ncbsCrllnbWriOff.getId())){
    
            ncbsCrllnbWriOff.setCreateTime(new Date());
            ncbsCrllnbWriOff.setCreateUserId(userVO.getUserId());
            ncbsCrllnbWriOff.setCreateUserName(userVO.getFullName());
                   
        }else{
            // 更新
            NcbsCrllnbWriOff temp = selectById(ncbsCrllnbWriOff.getId());
            ncbsCrllnbWriOff.setCreateTime(temp.getCreateTime());
            ncbsCrllnbWriOff.setCreateUserId(temp.getCreateUserId());
            ncbsCrllnbWriOff.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCrllnbWriOff.setUpdateTime(new Date());
        ncbsCrllnbWriOff.setUpdateUserId(userVO.getUserId());
        ncbsCrllnbWriOff.setUpdateUserName(userVO.getFullName());
                
        return super.saveOrUpdate(ncbsCrllnbWriOff);
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