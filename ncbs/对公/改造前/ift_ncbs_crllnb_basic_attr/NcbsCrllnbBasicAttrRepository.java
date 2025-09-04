package com.cebbank.tms.wenjing.ift.dao;

import com.cebbank.tms.wenjing.common.base.BaseDao;
import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnbBasicAttr;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * 资产损失:临时表-零售贷款账户基础属性表表数据库访问层
 * <pre>
 *
 * </pre>
 *
 * @author zhugang
 * @version V2.0.0
 * @date 2025-06-24 14:49:54
 * @see
 * @since
 */
@Repository
public interface NcbsCrllnbBasicAttrRepository extends BaseDao<NcbsCrllnbBasicAttr, Long> {


    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     * @throws
     * @see
     * @since
     */
    @Query("from NcbsCrllnbBasicAttr t where t.id = ?1")
    NcbsCrllnbBasicAttr queryById(Long id);


    /**
     * 根据ID物理删除
     *
     * @param id 主键
     * @return
     * @throws
     * @see
     * @since
     */
    @Modifying
    @Query("delete from NcbsCrllnbBasicAttr t where t.id = ?1")
    @Override
    void deleteById(Long id);


}