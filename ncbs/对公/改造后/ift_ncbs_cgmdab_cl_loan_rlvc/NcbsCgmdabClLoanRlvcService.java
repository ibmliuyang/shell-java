package com.cebbank.tms.wenjing.ift.service;

import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.BaseService;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.ift.domain.NcbsCgmdabClLoanRlvc;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCgmdabClLoanRlvcOutVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 资产损失:临时表-抵债资产对公贷款关联表表服务接口
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-20 09:43:07
 */
public interface NcbsCgmdabClLoanRlvcService extends BaseService<NcbsCgmdabClLoanRlvc, Long>{

    
    /**
    * 列表查询
        * @param vo 查询条件
        * @param userVO 用户
        * @return Page
    * @throws Exception
    * @see
    * @since
    */
    Page<NcbsCgmdabClLoanRlvcOutVO> listPage(BaseQuery<NcbsCgmdabClLoanRlvc> vo, UserVO userVO) throws Exception;
    
    
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
        * @param ncbsCgmdabClLoanRlvc 实体
        * @param userVO 用户
        * @return  NcbsCgmdabClLoanRlvc
    * @throws Exception
    * @see
    * @since
    */
    NcbsCgmdabClLoanRlvc saveOrUpdate(NcbsCgmdabClLoanRlvc ncbsCgmdabClLoanRlvc, UserVO userVO) throws Exception;
 
    
    /**
    * 详情查询
        * @param id 主键
        * @return NcbsCgmdabClLoanRlvcOutVO
    * @throws Exception
    * @see
    * @since
    */
    NcbsCgmdabClLoanRlvcOutVO queryInfoById(Long id) throws Exception;

    
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
     * @see
     * @since
     */
    DataSyncResult syncData(DataSyncRequest request) throws Exception;




}