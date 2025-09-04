package com.cebbank.tms.wenjing.ift.dao;

import com.cebbank.tms.wenjing.common.base.BaseDao;
import com.cebbank.tms.wenjing.ift.domain.NcbsCgmdabRlLoanRlvc;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * 资产损失:临时表-抵债资产零售贷款关联表表数据库访问层
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-20 09:43:07
 */
@Repository
public interface NcbsCgmdabRlLoanRlvcRepository extends BaseDao<NcbsCgmdabRlLoanRlvc, Long>{

 
    
    /**
    * 通过ID查询单条数据
        * @param id 主键
        * @return 实例对象
    * @throws 
    * @see
    * @since
    */
    @Query("from NcbsCgmdabRlLoanRlvc t where t.id = ?1")
    NcbsCgmdabRlLoanRlvc queryById(Long id);

    
    /**
    * 根据ID物理删除
        * @param id 主键
        * @return 
    * @throws 
    * @see
    * @since
    */
    @Modifying
    @Query("delete from NcbsCgmdabRlLoanRlvc t where t.id = ?1")
    @Override
    void deleteById(Long id);


}