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
import com.cebbank.tms.wenjing.ift.domain.NcbsCrllnaAcct;
import com.cebbank.tms.wenjing.ift.service.NcbsCrllnaAcctService;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCrllnaAcctOutVO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 资产损失:临时表-贷款账户主表表控制层
 * <pre>
 * 
 * </pre>
 * @author zhugang
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-02-17 15:51:05
 */
@Component
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("ncbs-crllna-acct")
public class NcbsCrllnaAcctController extends ControllerSupport {
    
    private static CSPSLogger log = CSPSLogFactory.get(NcbsCrllnaAcctController.class);
    
    /**
     * 服务对象
     */
    @Resource
    private NcbsCrllnaAcctService ncbsCrllnaAcctService;

    
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
    public ResultVo<NcbsCrllnaAcctOutVO> get(@PathParam("id") Long id) throws Exception {

        NcbsCrllnaAcctOutVO ncbsCrllnaAcctOutVO = ncbsCrllnaAcctService.queryInfoById(id);
        return super.success(ncbsCrllnaAcctOutVO);
    }
    
    
    /**
    * 保存-更新
        * @param ncbsCrllnaAcct 实体数据
        * @return ResultVO
    * @throws Exception
    * @see
    * @since
    */
    @POST
    @Path("save-update")
    @AspLog(title = "资产损失:临时表-贷款账户主表-保存", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo<NcbsCrllnaAcct> saveOrUpdate(@RequestBody BaseInput<NcbsCrllnaAcct> ncbsCrllnaAcct) throws Exception {
        return super.success(ncbsCrllnaAcctService.saveOrUpdate(ncbsCrllnaAcct.getBizData(), getLoginUser()));
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
    public ResultVo<BaseData<NcbsCrllnaAcctOutVO>> pageList(@RequestBody BaseQuery<NcbsCrllnaAcct> query) throws Exception {
        
        Page<NcbsCrllnaAcctOutVO> ncbsCrllnaAcctList = ncbsCrllnaAcctService.listPage(query,getLoginUser());
        return super.success(query.condition, ncbsCrllnaAcctList);
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
    @AspLog(title = "资产损失:临时表-贷款账户主表-删除", bizType = AuditOpBizType.DELETE, isSaveRequestData = false)
    public ResultVo deleteById(@PathParam("id") Long id) throws Exception {
        ncbsCrllnaAcctService.deleteById(id);
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
    @AspLog(title = "资产损失:临时表-贷款账户主表-批量删除", bizType = AuditOpBizType.DELETE_ALL, isSaveRequestData = false)
    public ResultVo deleteAllById(@RequestBody List<Long> ids) throws Exception {
        ncbsCrllnaAcctService.deleteAllByIds(ids);
        return ResultVo.success();
    }


}