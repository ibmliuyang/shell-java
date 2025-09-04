package com.cebbank.tms.wenjing.ift.service;

import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnaAcct;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCrllnaAcctOutVO;

import com.cebbank.tms.wenjing.common.base.BaseService;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import java.util.List;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.common.base.BaseQuery;

/**
 * 资产损失:临时表-贷款账户主表表服务接口
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:05
 */
public interface NcbsCrllnaAcctService extends BaseService<NcbsCrllnaAcct, Long>{

    
    /**
    * 列表查询
        * @param vo 查询条件
        * @param userVO 用户
        * @return Page
    * @throws Exception
    * @see
    * @since
    */
    Page<NcbsCrllnaAcctOutVO> listPage(BaseQuery<NcbsCrllnaAcct> vo, UserVO userVO) throws Exception;
    
    
    /**
    * 根据ID删除
        * @param id 主键
        * @return 
    * @throws 
    * @see
    * @since
    */
    @Override
    void deleteById(Long id);

    
    /**
    * 保存修改
        * @param ncbsCrllnaAcct 实体
        * @param userVO 用户
        * @return  NcbsCrllnaAcct
    * @throws Exception
    * @see
    * @since
    */
    NcbsCrllnaAcct saveOrUpdate(NcbsCrllnaAcct ncbsCrllnaAcct, UserVO userVO) throws Exception;
 
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCrllnaAcctOutVO
    * @throws Exception
    * @see
    * @since
    */
    NcbsCrllnaAcctOutVO queryInfoById(Long id) throws Exception;

    
    /**
    * 批量删除
        * @param ids 主键集合
        * @return 
    * @throws Exception
    * @see
    * @since
    */
    void deleteAllByIds(List<Long> ids) throws Exception;
    
    
    
   

}