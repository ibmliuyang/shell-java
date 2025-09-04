package com.cebbank.tms.wenjing.ift.service;

import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.BaseService;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnbBasicAttr;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCrllnbBasicAttrOutVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 资产损失:临时表-零售贷款账户基础属性表表服务接口
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
public interface NcbsCrllnbBasicAttrService extends BaseService<NcbsCrllnbBasicAttr, Long> {


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
    Page<NcbsCrllnbBasicAttrOutVO> listPage(BaseQuery<NcbsCrllnbBasicAttr> vo, UserVO userVO) throws Exception;


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
     * @param ncbsCrllnbBasicAttr 实体
     * @param userVO              用户
     * @return NcbsCrllnbBasicAttr
     * @throws Exception
     * @see
     * @since
     */
    NcbsCrllnbBasicAttr saveOrUpdate(NcbsCrllnbBasicAttr ncbsCrllnbBasicAttr, UserVO userVO);


    /**
     * 详情查询
     *
     * @param id 主键
     * @return NcbsCrllnbBasicAttrOutVO
     * @throws Exception
     * @see
     * @since
     */
    NcbsCrllnbBasicAttrOutVO queryInfoById(Long id);


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