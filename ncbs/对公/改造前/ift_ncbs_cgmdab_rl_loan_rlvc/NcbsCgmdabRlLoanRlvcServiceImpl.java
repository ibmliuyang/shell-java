package com.cebbank.tms.wenjing.ift.service.impl;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.ift.domain.NcbsCgmdabRlLoanRlvc;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCgmdabRlLoanRlvcOutVO;

import com.cebbank.tms.wenjing.ift.dao.NcbsCgmdabRlLoanRlvcRepository;
import com.cebbank.tms.wenjing.ift.service.NcbsCgmdabRlLoanRlvcService;
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
 * 资产损失:临时表-抵债资产零售贷款关联表表服务实现类
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
public class NcbsCgmdabRlLoanRlvcServiceImpl extends BaseServiceImpl<NcbsCgmdabRlLoanRlvcRepository, NcbsCgmdabRlLoanRlvc, Long> implements NcbsCgmdabRlLoanRlvcService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCgmdabRlLoanRlvcServiceImpl.class);
    
    @Resource
    private NcbsCgmdabRlLoanRlvcRepository ncbsCgmdabRlLoanRlvcRepository;


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
    public Page<NcbsCgmdabRlLoanRlvcOutVO> listPage(BaseQuery<NcbsCgmdabRlLoanRlvc> vo, UserVO userVO) throws Exception {
        Page<NcbsCgmdabRlLoanRlvc> page =  super.listPage(vo);
       //查询结果转换成vo返回
        List<NcbsCgmdabRlLoanRlvc> list = page.getContent();
        List<NcbsCgmdabRlLoanRlvcOutVO> listVo = new ArrayList<>();
        NcbsCgmdabRlLoanRlvcOutVO ncbsCgmdabRlLoanRlvcOutVO = null;

   
        for (NcbsCgmdabRlLoanRlvc ncbsCgmdabRlLoanRlvc : list) {
            ncbsCgmdabRlLoanRlvcOutVO = new NcbsCgmdabRlLoanRlvcOutVO();
            BeanCopyUtil.copyObject(ncbsCgmdabRlLoanRlvc, ncbsCgmdabRlLoanRlvcOutVO);
            listVo.add(ncbsCgmdabRlLoanRlvcOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }
    
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCgmdabRlLoanRlvcOutVO
    * @throws Exception
    * @see
    * @since
    */
    @Override
    public NcbsCgmdabRlLoanRlvcOutVO queryInfoById(Long id) throws Exception{
        NcbsCgmdabRlLoanRlvc ncbsCgmdabRlLoanRlvc = this.ncbsCgmdabRlLoanRlvcRepository.queryById(id);
        NcbsCgmdabRlLoanRlvcOutVO vo = new NcbsCgmdabRlLoanRlvcOutVO();
        BeanCopyUtil.copyObject(ncbsCgmdabRlLoanRlvc, vo);
                
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
        ncbsCgmdabRlLoanRlvcRepository.deleteById(id);
    }
    
    
    /**
    * 保存修改
        * @param ncbsCgmdabRlLoanRlvc 实体
        * @param userVO 用户
        * @return  NcbsCgmdabRlLoanRlvc
    * @throws 
    * @see
    * @since
    */
    @Override
    public NcbsCgmdabRlLoanRlvc saveOrUpdate(NcbsCgmdabRlLoanRlvc ncbsCgmdabRlLoanRlvc,UserVO userVO) throws Exception{
        if(Objects.isNull(ncbsCgmdabRlLoanRlvc.getId())){
    
            ncbsCgmdabRlLoanRlvc.setCreateTime(new Date());
            ncbsCgmdabRlLoanRlvc.setCreateUserId(userVO.getUserId());
            ncbsCgmdabRlLoanRlvc.setCreateUserName(userVO.getFullName());
                   
        }else{
            // 更新
            NcbsCgmdabRlLoanRlvc temp = selectById(ncbsCgmdabRlLoanRlvc.getId());
            ncbsCgmdabRlLoanRlvc.setCreateTime(temp.getCreateTime());
            ncbsCgmdabRlLoanRlvc.setCreateUserId(temp.getCreateUserId());
            ncbsCgmdabRlLoanRlvc.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCgmdabRlLoanRlvc.setUpdateTime(new Date());
        ncbsCgmdabRlLoanRlvc.setUpdateUserId(userVO.getUserId());
        ncbsCgmdabRlLoanRlvc.setUpdateUserName(userVO.getFullName());
                
        return super.saveOrUpdate(ncbsCgmdabRlLoanRlvc);
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