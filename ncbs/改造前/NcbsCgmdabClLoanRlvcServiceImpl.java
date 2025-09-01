package com.cebbank.tms.wenjing.ift.service.impl;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.ift.domain.NcbsCgmdabClLoanRlvc;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCgmdabClLoanRlvcOutVO;

import com.cebbank.tms.wenjing.ift.dao.NcbsCgmdabClLoanRlvcRepository;
import com.cebbank.tms.wenjing.ift.service.NcbsCgmdabClLoanRlvcService;
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
     


}