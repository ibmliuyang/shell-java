package com.cebbank.tms.wenjing.ift.service;

import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.BaseService;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbBasicAttr;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbBasicAttrOutVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 资产损失:中间表-贷款账户基础属性表表服务接口
 * <pre>
 *
 * </pre>
 *
 * @author zhugang
 * @version V2.0.0
 * @date 2025-04-14 10:19:31
 * @see
 * @since
 */
public interface NcbsCcllnbBasicAttrService extends BaseService<NcbsCcllnbBasicAttr, Long> {


    /**
     * 列表查询
     *
     * @param vo     查询条件
     * @param userVO 用户
     * @return Page
     * @throws Exception
     * @see
     * @since
     */
    Page<NcbsCcllnbBasicAttrOutVO> listPage(BaseQuery<NcbsCcllnbBasicAttr> vo, UserVO userVO) throws Exception;


    /**
     * 根据ID删除
     *
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
     *
     * @param ncbsCcllnbBasicAttr 实体
     * @param userVO              用户
     * @return NcbsCcllnbBasicAttr
     * @throws Exception
     * @see
     * @since
     */
    NcbsCcllnbBasicAttr saveOrUpdate(NcbsCcllnbBasicAttr ncbsCcllnbBasicAttr, UserVO userVO);


    /**
     * 详情查询
     *
     * @param id 主键
     * @return NcbsCcllnbBasicAttrOutVO
     * @throws Exception
     * @see
     * @since
     */
    NcbsCcllnbBasicAttrOutVO queryInfoById(Long id);


    /**
     * 批量删除
     *
     * @param ids 主键集合
     * @return
     * @throws Exception
     * @see
     * @since
     */
    void deleteAllByIds(List<Long> ids);


}