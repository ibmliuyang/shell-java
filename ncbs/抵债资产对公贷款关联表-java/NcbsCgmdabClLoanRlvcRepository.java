package com.cebbank.tms.wenjing.ift.dao;

import com.cebbank.tms.wenjing.common.base.BaseDao;
import com.cebbank.tms.wenjing.ift.domain.NcbsCgmdabClLoanRlvc;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * 资产损失:临时表-抵债资产对公贷款关联表表数据库访问层
 * <pre>
 * 
 * </pre>
 * @author 系统重构
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-08-25 10:00:00
 */
@Repository
public interface NcbsCgmdabClLoanRlvcRepository extends BaseDao<NcbsCgmdabClLoanRlvc, Long>{

 
    
    /**
    * 通过ID查询单条数据
        * @param id 主键
        * @return 实例对象
    * @throws 
    * @see
    * @since
    */
    @Query("from NcbsCgmdabClLoanRlvc t where t.id = ?1")
    NcbsCgmdabClLoanRlvc queryById(Long id);

    
    /**
    * 根据ID物理删除
        * @param id 主键
        * @return 
    * @throws 
    * @see
    * @since
    */
    @Modifying
    @Query("delete from NcbsCgmdabClLoanRlvc t where t.id = ?1")
    @Override
    void deleteById(Long id);


}