package com.cebbank.tms.wenjing.ift.service.impl;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import com.cebbank.tms.wenjing.common.util.BeanCopyUtil;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.ift.dao.NcbsCcllnbBasicAttrRepository;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbBasicAttr;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnbBasicAttrService;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbBasicAttrOutVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 资产损失:中间表-贷款账户基础属性表表服务实现类
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
@Service
public class NcbsCcllnbBasicAttrServiceImpl extends BaseServiceImpl<NcbsCcllnbBasicAttrRepository, NcbsCcllnbBasicAttr, Long> implements NcbsCcllnbBasicAttrService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnbBasicAttrServiceImpl.class);

    @Resource
    private NcbsCcllnbBasicAttrRepository ncbsCcllnbBasicAttrRepository;


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
    @Override
    public Page<NcbsCcllnbBasicAttrOutVO> listPage(BaseQuery<NcbsCcllnbBasicAttr> vo, UserVO userVO) throws Exception {
        Page<NcbsCcllnbBasicAttr> page = super.listPage(vo);
        //查询结果转换成vo返回
        List<NcbsCcllnbBasicAttr> list = page.getContent();
        List<NcbsCcllnbBasicAttrOutVO> listVo = new ArrayList<>();
        NcbsCcllnbBasicAttrOutVO ncbsCcllnbBasicAttrOutVO = null;


        for (NcbsCcllnbBasicAttr ncbsCcllnbBasicAttr : list) {
            ncbsCcllnbBasicAttrOutVO = new NcbsCcllnbBasicAttrOutVO();
            BeanCopyUtil.copyObject(ncbsCcllnbBasicAttr, ncbsCcllnbBasicAttrOutVO);
            listVo.add(ncbsCcllnbBasicAttrOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }


    /**
     * 详情查询
     *
     * @param id 主键
     * @return NcbsCcllnbBasicAttrOutVO
     * @throws Exception
     * @see
     * @since
     */
    @Override
    public NcbsCcllnbBasicAttrOutVO queryInfoById(Long id) {
        NcbsCcllnbBasicAttr ncbsCcllnbBasicAttr = this.ncbsCcllnbBasicAttrRepository.queryById(id);
        NcbsCcllnbBasicAttrOutVO vo = new NcbsCcllnbBasicAttrOutVO();
        BeanCopyUtil.copyObject(ncbsCcllnbBasicAttr, vo);

        return vo;
    }


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
    public void deleteById(Long id) {
        ncbsCcllnbBasicAttrRepository.deleteById(id);
    }


    /**
     * 保存修改
     *
     * @param ncbsCcllnbBasicAttr 实体
     * @param userVO              用户
     * @return NcbsCcllnbBasicAttr
     * @throws
     * @see
     * @since
     */
    @Override
    public NcbsCcllnbBasicAttr saveOrUpdate(NcbsCcllnbBasicAttr ncbsCcllnbBasicAttr, UserVO userVO) {
        if (Objects.isNull(ncbsCcllnbBasicAttr.getId())) {

            ncbsCcllnbBasicAttr.setCreateTime(new Date());
            ncbsCcllnbBasicAttr.setCreateUserId(userVO.getUserId());
            ncbsCcllnbBasicAttr.setCreateUserName(userVO.getFullName());

        } else {
            // 更新
            NcbsCcllnbBasicAttr temp = selectById(ncbsCcllnbBasicAttr.getId());
            ncbsCcllnbBasicAttr.setCreateTime(temp.getCreateTime());
            ncbsCcllnbBasicAttr.setCreateUserId(temp.getCreateUserId());
            ncbsCcllnbBasicAttr.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCcllnbBasicAttr.setUpdateTime(new Date());
        ncbsCcllnbBasicAttr.setUpdateUserId(userVO.getUserId());
        ncbsCcllnbBasicAttr.setUpdateUserName(userVO.getFullName());

        return super.saveOrUpdate(ncbsCcllnbBasicAttr);
    }


    /**
     * 批量删除
     *
     * @param ids 主键集合
     * @return
     * @throws Exception
     * @see
     * @since
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = "utaxTransactionManager")
    public void deleteAllByIds(List<Long> ids) {
        for (Long id : ids) {
            this.deleteById(id);
        }
    }


}