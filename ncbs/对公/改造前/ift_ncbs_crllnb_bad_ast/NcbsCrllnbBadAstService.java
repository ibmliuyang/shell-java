package com.cebbank.tms.wenjing.ift.service;

import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnbBadAst;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCrllnbBadAstOutVO;

import com.cebbank.tms.wenjing.common.base.BaseService;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import java.util.List;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.common.base.BaseQuery;

/**
 * 资产损失:临时表-不良资产登记簿表服务接口
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:07
 */
public interface NcbsCrllnbBadAstService extends BaseService<NcbsCrllnbBadAst, Long>{

    
    /**
    * 列表查询
        * @param vo 查询条件
        * @param userVO 用户
        * @return Page
    * @throws Exception
    * @see
    * @since
    */
    Page<NcbsCrllnbBadAstOutVO> listPage(BaseQuery<NcbsCrllnbBadAst> vo, UserVO userVO) throws Exception;
    
    
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
        * @param ncbsCrllnbBadAst 实体
        * @param userVO 用户
        * @return  NcbsCrllnbBadAst
    * @throws Exception
    * @see
    * @since
    */
    NcbsCrllnbBadAst saveOrUpdate(NcbsCrllnbBadAst ncbsCrllnbBadAst, UserVO userVO) throws Exception;
 
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCrllnbBadAstOutVO
    * @throws Exception
    * @see
    * @since
    */
    NcbsCrllnbBadAstOutVO queryInfoById(Long id) throws Exception;

    
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