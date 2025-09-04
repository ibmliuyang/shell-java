package com.cebbank.tms.wenjing.ift.service.impl;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.BaseServiceImpl;
import com.cebbank.tms.wenjing.common.util.BeanCopyUtil;
import com.cebbank.tms.wenjing.common.vo.UserVO;
import com.cebbank.tms.wenjing.ift.dao.NcbsCrllnbBasicAttrRepository;
import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnbBasicAttr;
import com.cebbank.tms.wenjing.ift.service.NcbsCrllnbBasicAttrService;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCrllnbBasicAttrOutVO;
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
 * 资产损失:临时表-零售贷款账户基础属性表表服务实现类
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
@Service
public class NcbsCrllnbBasicAttrServiceImpl extends BaseServiceImpl<NcbsCrllnbBasicAttrRepository, NcbsCrllnbBasicAttr, Long> implements NcbsCrllnbBasicAttrService {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCrllnbBasicAttrServiceImpl.class);

    @Resource
    private NcbsCrllnbBasicAttrRepository ncbsCrllnbBasicAttrRepository;


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
    public Page<NcbsCrllnbBasicAttrOutVO> listPage(BaseQuery<NcbsCrllnbBasicAttr> vo, UserVO userVO) throws Exception {
        Page<NcbsCrllnbBasicAttr> page = super.listPage(vo);
        //查询结果转换成vo返回
        List<NcbsCrllnbBasicAttr> list = page.getContent();
        List<NcbsCrllnbBasicAttrOutVO> listVo = new ArrayList<>();
        NcbsCrllnbBasicAttrOutVO ncbsCrllnbBasicAttrOutVO = null;


        for (NcbsCrllnbBasicAttr ncbsCrllnbBasicAttr : list) {
            ncbsCrllnbBasicAttrOutVO = new NcbsCrllnbBasicAttrOutVO();
            BeanCopyUtil.copyObject(ncbsCrllnbBasicAttr, ncbsCrllnbBasicAttrOutVO);
            listVo.add(ncbsCrllnbBasicAttrOutVO);
        }
        return new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
    }


    /**
     * 详情查询
     *
     * @param id 主键
     * @return NcbsCrllnbBasicAttrOutVO
     * @throws Exception
     * @see
     * @since
     */
    @Override
    public NcbsCrllnbBasicAttrOutVO queryInfoById(Long id) {
        NcbsCrllnbBasicAttr ncbsCrllnbBasicAttr = this.ncbsCrllnbBasicAttrRepository.queryById(id);
        NcbsCrllnbBasicAttrOutVO vo = new NcbsCrllnbBasicAttrOutVO();
        BeanCopyUtil.copyObject(ncbsCrllnbBasicAttr, vo);

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
        ncbsCrllnbBasicAttrRepository.deleteById(id);
    }


    /**
     * 保存修改
     *
     * @param ncbsCrllnbBasicAttr 实体
     * @param userVO              用户
     * @return NcbsCrllnbBasicAttr
     * @throws
     * @see
     * @since
     */
    @Override
    public NcbsCrllnbBasicAttr saveOrUpdate(NcbsCrllnbBasicAttr ncbsCrllnbBasicAttr, UserVO userVO) {
        if (Objects.isNull(ncbsCrllnbBasicAttr.getId())) {

            ncbsCrllnbBasicAttr.setCreateTime(new Date());
            ncbsCrllnbBasicAttr.setCreateUserId(userVO.getUserId());
            ncbsCrllnbBasicAttr.setCreateUserName(userVO.getFullName());

        } else {
            // 更新
            NcbsCrllnbBasicAttr temp = selectById(ncbsCrllnbBasicAttr.getId());
            ncbsCrllnbBasicAttr.setCreateTime(temp.getCreateTime());
            ncbsCrllnbBasicAttr.setCreateUserId(temp.getCreateUserId());
            ncbsCrllnbBasicAttr.setCreateUserName(temp.getCreateUserName());
        }
        ncbsCrllnbBasicAttr.setUpdateTime(new Date());
        ncbsCrllnbBasicAttr.setUpdateUserId(userVO.getUserId());
        ncbsCrllnbBasicAttr.setUpdateUserName(userVO.getFullName());

        return super.saveOrUpdate(ncbsCrllnbBasicAttr);
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