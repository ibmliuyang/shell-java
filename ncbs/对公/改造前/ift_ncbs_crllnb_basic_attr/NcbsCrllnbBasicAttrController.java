package com.cebbank.tms.wenjing.ift.controller;

import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.annotation.AspLog;
import com.cebbank.tms.wenjing.common.base.BaseData;
import com.cebbank.tms.wenjing.common.base.BaseInput;
import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.ResultVo;
import com.cebbank.tms.wenjing.common.constant.enums.AuditOpBizType;
import com.cebbank.tms.wenjing.common.handler.ControllerSupport;
import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnbBasicAttr;
import com.cebbank.tms.wenjing.ift.service.NcbsCrllnbBasicAttrService;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCrllnbBasicAttrOutVO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 资产损失:临时表-零售贷款账户基础属性表表控制层
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
@Component
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("ncbs-crllnb-basic-attr")
public class NcbsCrllnbBasicAttrController extends ControllerSupport {

    private static CSPSLogger log = CSPSLogFactory.get(NcbsCrllnbBasicAttrController.class);

    /**
     * 服务对象
     */
    @Resource
    private NcbsCrllnbBasicAttrService ncbsCrllnbBasicAttrService;


    /**
     * 详情
     *
     * @param id 主键ID
     * @return ResultVO
     * @throws Exception
     * @see
     * @since
     */
    @GET
    @Path("get/{id}")
    public ResultVo<NcbsCrllnbBasicAttrOutVO> get(@PathParam("id") Long id) {

        NcbsCrllnbBasicAttrOutVO ncbsCrllnbBasicAttrOutVO = ncbsCrllnbBasicAttrService.queryInfoById(id);
        return super.success(ncbsCrllnbBasicAttrOutVO);
    }


    /**
     * 保存-更新
     *
     * @param ncbsCrllnbBasicAttr 实体数据
     * @return ResultVO
     * @throws Exception
     * @see
     * @since
     */
    @POST
    @Path("save-update")
    @AspLog(title = "资产损失:临时表-零售贷款账户基础属性表-保存", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo<NcbsCrllnbBasicAttr> saveOrUpdate(@RequestBody BaseInput<NcbsCrllnbBasicAttr> ncbsCrllnbBasicAttr) {
        return super.success(ncbsCrllnbBasicAttrService.saveOrUpdate(ncbsCrllnbBasicAttr.getBizData(), getLoginUser()));
    }


    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return ResultVO
     * @throws Exception
     * @see
     * @since
     */
    @POST
    @Path("page-list")
    public ResultVo<BaseData<NcbsCrllnbBasicAttrOutVO>> pageList(@RequestBody BaseQuery<NcbsCrllnbBasicAttr> query) throws Exception {
        Page<NcbsCrllnbBasicAttrOutVO> ncbsCrllnbBasicAttrList = ncbsCrllnbBasicAttrService.listPage(query, getLoginUser());
        return super.success(query.condition, ncbsCrllnbBasicAttrList);
    }


    /**
     * 通过ID删除
     *
     * @param id 主键ID
     * @return ResultVO
     * @throws Exception
     * @see
     * @since
     */
    @POST
    @Path("delete-by-id/{id}")
    @AspLog(title = "资产损失:临时表-零售贷款账户基础属性表-删除", bizType = AuditOpBizType.DELETE, isSaveRequestData = false)
    public ResultVo deleteById(@PathParam("id") Long id) {
        ncbsCrllnbBasicAttrService.deleteById(id);
        return ResultVo.success();
    }


    /**
     * 通过ID批量删除
     *
     * @param ids 主键ID
     * @return ResultVO
     * @throws Exception
     * @see
     * @since
     */
    @POST
    @Path("delete-all-by-id")
    @AspLog(title = "资产损失:临时表-零售贷款账户基础属性表-批量删除", bizType = AuditOpBizType.DELETE_ALL, isSaveRequestData = false)
    public ResultVo deleteAllById(@RequestBody List<Long> ids) {
        ncbsCrllnbBasicAttrService.deleteAllByIds(ids);
        return ResultVo.success();
    }


}