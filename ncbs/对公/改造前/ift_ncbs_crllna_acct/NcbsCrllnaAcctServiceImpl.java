package com.cebbank.tms.wenjing.ift.service.impl;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnaAcct;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCrllnaAcctOutVO;

import com.cebbank.tms.wenjing.ift.dao.NcbsCrllnaAcctRepository;
import com.cebbank.tms.wenjing.ift.service.NcbsCrllnaAcctService;
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
 * 资产损失:临时表-贷款账户主表表服务实现类
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:05
 */
@Service
public class NcbsCrllnaAcctServiceImpl extends BaseServiceImpl<NcbsCrllnaAcctRepository, NcbsCrllnaAcct, Long> implements NcbsCrllnaAcctService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCrllnaAcctServiceImpl.class);
    
    @Resource
    private NcbsCrllnaAcctRepository ncbsCrllnaAcctRepository;


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
    public Page<NcbsCrllnaAcctOutVO> listPage(BaseQuery<NcbsCrllnaAcct> vo, UserVO userVO) throws Exception {
        Page<NcbsCrllnaAcct> page =  super.listPage(vo);
       //查询结果转换成vo返回
        List<NcbsCrllnaAcct> list = page.getContent();
        List<NcbsCrllnaAcctOutVO> listVo = new ArrayList<>();
        NcbsCrllnaAcctOutVO ncbsCrllnaAcctOutVO = null;

   
        for (NcbsCrllnaAcct ncbsCrllnaAcct : list) {
            ncbsCrllnaAcctOutVO = new NcbsCrllnaAcctOutVO();
            BeanCopyUtil.copyObject(ncbsCrllnaAcct, ncbsCrllnaAcctOutVO);
            listVo.add(ncbsCrllnaAcctOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCrllnaAcctOutVO
    * @throws Exception
    * @see
    * @since
    */
    @Override
    public NcbsCrllnaAcctOutVO queryInfoById(Long id) throws Exception{
        NcbsCrllnaAcct ncbsCrllnaAcct = this.ncbsCrllnaAcctRepository.queryById(id);
        NcbsCrllnaAcctOutVO vo = new NcbsCrllnaAcctOutVO();
        BeanCopyUtil.copyObject(ncbsCrllnaAcct, vo);
                
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
        ncbsCrllnaAcctRepository.deleteById(id);
    }
    
    
    /**
    * 保存修改
        * @param ncbsCrllnaAcct 实体
        * @param userVO 用户
        * @return  NcbsCrllnaAcct
    * @throws 
    * @see
    * @since
    */
    @Override
    public NcbsCrllnaAcct saveOrUpdate(NcbsCrllnaAcct ncbsCrllnaAcct,UserVO userVO) throws Exception{
        if(Objects.isNull(ncbsCrllnaAcct.getId())){
    
            ncbsCrllnaAcct.setCreateTime(new Date());
            ncbsCrllnaAcct.setCreateUserId(userVO.getUserId());
            ncbsCrllnaAcct.setCreateUserName(userVO.getFullName());
                   
        }else{
            // 更新
            NcbsCrllnaAcct temp = selectById(ncbsCrllnaAcct.getId());
            ncbsCrllnaAcct.setCreateTime(temp.getCreateTime());
            ncbsCrllnaAcct.setCreateUserId(temp.getCreateUserId());
            ncbsCrllnaAcct.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCrllnaAcct.setUpdateTime(new Date());
        ncbsCrllnaAcct.setUpdateUserId(userVO.getUserId());
        ncbsCrllnaAcct.setUpdateUserName(userVO.getFullName());
                
        return super.saveOrUpdate(ncbsCrllnaAcct);
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