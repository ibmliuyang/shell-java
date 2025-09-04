package com.cebbank.tms.wenjing.ift.dao;

import com.cebbank.tms.wenjing.common.base.BaseDao;
import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnbBadAst;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * 资产损失:临时表-不良资产登记簿表数据库访问层
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:07
 */
@Repository
public interface NcbsCrllnbBadAstRepository extends BaseDao<NcbsCrllnbBadAst, Long>{

 
    
    /**
    * 通过ID查询单条数据
        * @param id 主键
        * @return 实例对象
    * @throws 
    * @see
    * @since
    */
    @Query("from NcbsCrllnbBadAst t where t.id = ?1")
    NcbsCrllnbBadAst queryById(Long id);

    
    /**
    * 根据ID物理删除
        * @param id 主键
        * @return 
    * @throws 
    * @see
    * @since
    */
    @Modifying
    @Query("delete from NcbsCrllnbBadAst t where t.id = ?1")
    @Override
    void deleteById(Long id);


}