package com.cebbank.tms.wenjing.ift.service;

import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnaAcct;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnaAcctOutVO;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;

import com.cebbank.tms.wenjing.common.base.BaseService;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import java.util.List;
import org.springframework.data.domain.Page;
import com.cebbank.tms.wenjing.common.base.BaseQuery;

/**
 * 资产损失:临时表-对公贷款户主表表服务接口
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:04
 */
public interface NcbsCcllnaAcctService extends BaseService<NcbsCcllnaAcct, Long>{

    
    /**
    * 列表查询
        * @param vo 查询条件
        * @param userVO 用户
        * @return Page
    * @throws Exception
    * @see
    * @since
    */
    Page<NcbsCcllnaAcctOutVO> listPage(BaseQuery<NcbsCcllnaAcct> vo, UserVO userVO) throws Exception;
    
    
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
        * @param ncbsCcllnaAcct 实体
        * @param userVO 用户
        * @return  NcbsCcllnaAcct
    * @throws Exception
    * @see
    * @since
    */
    NcbsCcllnaAcct saveOrUpdate(NcbsCcllnaAcct ncbsCcllnaAcct, UserVO userVO) throws Exception;
 
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCcllnaAcctOutVO
    * @throws Exception
    * @see
    * @since
    */
    NcbsCcllnaAcctOutVO queryInfoById(Long id) throws Exception;

    
    /**
    * 批量删除
        * @param ids 主键集合
        * @return 
    * @throws Exception
    * @see
    * @since
    */
    void deleteAllByIds(List<Long> ids) throws Exception;
    
    /**
     * 数据同步
     * @param request 数据同步请求
     * @return DataSyncResult
     * @throws Exception
     */
    DataSyncResult syncData(DataSyncRequest request) throws Exception;
    
    /**
     * 获取需要维护的借据号列表
     * @return List<String>
     * @throws Exception
     */
    List<String> getMaintainLnDueBillNos() throws Exception;
    
    /**
     * 获取要过滤掉的借据号列表（贷款属性表中ln_biz_clas为2或3的）
     * @return List<String>
     * @throws Exception
     */
    List<String> getExclusionLnDueBillNos() throws Exception;
    
    
    
   

}