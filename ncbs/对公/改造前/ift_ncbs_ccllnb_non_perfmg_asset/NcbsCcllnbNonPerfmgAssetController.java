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
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbNonPerfmgAsset;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnbNonPerfmgAssetService;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbNonPerfmgAssetOutVO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 资产损失:临时表-对公贷款不良资产登记簿表控制层
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:04
 */
@Component
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("ncbs-ccllnb-non-perfmg-asset")
public class NcbsCcllnbNonPerfmgAssetController extends ControllerSupport {
    
    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnbNonPerfmgAssetController.class);
    
    /**
     * 服务对象
     */
    @Resource
    private NcbsCcllnbNonPerfmgAssetService ncbsCcllnbNonPerfmgAssetService;

    
    /**
    * 详情
        * @param id 主键ID
        * @return ResultVO
    * @throws Exception
    * @see
    * @since
    */
    @GET
    @Path("get/{id}")
    public ResultVo<NcbsCcllnbNonPerfmgAssetOutVO> get(@PathParam("id") Long id) throws Exception {

        NcbsCcllnbNonPerfmgAssetOutVO ncbsCcllnbNonPerfmgAssetOutVO = ncbsCcllnbNonPerfmgAssetService.queryInfoById(id);
        return super.success(ncbsCcllnbNonPerfmgAssetOutVO);
    }
    
    
    /**
    * 保存-更新
        * @param ncbsCcllnbNonPerfmgAsset 实体数据
        * @return ResultVO
    * @throws Exception
    * @see
    * @since
    */
    @POST
    @Path("save-update")
    @AspLog(title = "资产损失:临时表-对公贷款不良资产登记簿-保存", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo<NcbsCcllnbNonPerfmgAsset> saveOrUpdate(@RequestBody BaseInput<NcbsCcllnbNonPerfmgAsset> ncbsCcllnbNonPerfmgAsset) throws Exception {
        return super.success(ncbsCcllnbNonPerfmgAssetService.saveOrUpdate(ncbsCcllnbNonPerfmgAsset.getBizData(), getLoginUser()));
    }

    
    /**
    * 分页查询
        * @param query 查询条件
        * @return ResultVO
    * @throws Exception
    * @see
    * @since
    */
    @POST
    @Path("page-list")
    public ResultVo<BaseData<NcbsCcllnbNonPerfmgAssetOutVO>> pageList(@RequestBody BaseQuery<NcbsCcllnbNonPerfmgAsset> query) throws Exception {
        
        Page<NcbsCcllnbNonPerfmgAssetOutVO> ncbsCcllnbNonPerfmgAssetList = ncbsCcllnbNonPerfmgAssetService.listPage(query,getLoginUser());
        return super.success(query.condition, ncbsCcllnbNonPerfmgAssetList);
    }
    
    
    /**
    * 通过ID删除
        * @param id 主键ID
        * @return ResultVO
    * @throws Exception
    * @see
    * @since
    */
    @POST
    @Path("delete-by-id/{id}")
    @AspLog(title = "资产损失:临时表-对公贷款不良资产登记簿-删除", bizType = AuditOpBizType.DELETE, isSaveRequestData = false)
    public ResultVo deleteById(@PathParam("id") Long id) throws Exception {
        ncbsCcllnbNonPerfmgAssetService.deleteById(id);
        return ResultVo.success();
    }

    
    /**
    * 通过ID批量删除
        * @param ids 主键ID
        * @return ResultVO
    * @throws Exception
    * @see
    * @since
    */
    @POST
    @Path("delete-all-by-id")
    @AspLog(title = "资产损失:临时表-对公贷款不良资产登记簿-批量删除", bizType = AuditOpBizType.DELETE_ALL, isSaveRequestData = false)
    public ResultVo deleteAllById(@RequestBody List<Long> ids) throws Exception {
        ncbsCcllnbNonPerfmgAssetService.deleteAllByIds(ids);
        return ResultVo.success();
    }


}