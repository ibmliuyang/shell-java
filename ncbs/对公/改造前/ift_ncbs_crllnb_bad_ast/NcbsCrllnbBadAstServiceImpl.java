package com.cebbank.tms.wenjing.ift.service.impl;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnbBadAst;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCrllnbBadAstOutVO;

import com.cebbank.tms.wenjing.ift.dao.NcbsCrllnbBadAstRepository;
import com.cebbank.tms.wenjing.ift.service.NcbsCrllnbBadAstService;
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
 * 资产损失:临时表-不良资产登记簿表服务实现类
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:07
 */
@Service
public class NcbsCrllnbBadAstServiceImpl extends BaseServiceImpl<NcbsCrllnbBadAstRepository, NcbsCrllnbBadAst, Long> implements NcbsCrllnbBadAstService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCrllnbBadAstServiceImpl.class);
    
    @Resource
    private NcbsCrllnbBadAstRepository ncbsCrllnbBadAstRepository;


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
    public Page<NcbsCrllnbBadAstOutVO> listPage(BaseQuery<NcbsCrllnbBadAst> vo, UserVO userVO) throws Exception {
        Page<NcbsCrllnbBadAst> page =  super.listPage(vo);
       //查询结果转换成vo返回
        List<NcbsCrllnbBadAst> list = page.getContent();
        List<NcbsCrllnbBadAstOutVO> listVo = new ArrayList<>();
        NcbsCrllnbBadAstOutVO ncbsCrllnbBadAstOutVO = null;

   
        for (NcbsCrllnbBadAst ncbsCrllnbBadAst : list) {
            ncbsCrllnbBadAstOutVO = new NcbsCrllnbBadAstOutVO();
            BeanCopyUtil.copyObject(ncbsCrllnbBadAst, ncbsCrllnbBadAstOutVO);
            listVo.add(ncbsCrllnbBadAstOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCrllnbBadAstOutVO
    * @throws Exception
    * @see
    * @since
    */
    @Override
    public NcbsCrllnbBadAstOutVO queryInfoById(Long id) throws Exception{
        NcbsCrllnbBadAst ncbsCrllnbBadAst = this.ncbsCrllnbBadAstRepository.queryById(id);
        NcbsCrllnbBadAstOutVO vo = new NcbsCrllnbBadAstOutVO();
        BeanCopyUtil.copyObject(ncbsCrllnbBadAst, vo);
                
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
        ncbsCrllnbBadAstRepository.deleteById(id);
    }
    
    
    /**
    * 保存修改
        * @param ncbsCrllnbBadAst 实体
        * @param userVO 用户
        * @return  NcbsCrllnbBadAst
    * @throws 
    * @see
    * @since
    */
    @Override
    public NcbsCrllnbBadAst saveOrUpdate(NcbsCrllnbBadAst ncbsCrllnbBadAst,UserVO userVO) throws Exception{
        if(Objects.isNull(ncbsCrllnbBadAst.getId())){
    
            ncbsCrllnbBadAst.setCreateTime(new Date());
            ncbsCrllnbBadAst.setCreateUserId(userVO.getUserId());
            ncbsCrllnbBadAst.setCreateUserName(userVO.getFullName());
                   
        }else{
            // 更新
            NcbsCrllnbBadAst temp = selectById(ncbsCrllnbBadAst.getId());
            ncbsCrllnbBadAst.setCreateTime(temp.getCreateTime());
            ncbsCrllnbBadAst.setCreateUserId(temp.getCreateUserId());
            ncbsCrllnbBadAst.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCrllnbBadAst.setUpdateTime(new Date());
        ncbsCrllnbBadAst.setUpdateUserId(userVO.getUserId());
        ncbsCrllnbBadAst.setUpdateUserName(userVO.getFullName());
                
        return super.saveOrUpdate(ncbsCrllnbBadAst);
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