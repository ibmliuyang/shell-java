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
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbChkWriteOff;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnbChkWriteOffService;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbChkWriteOffOutVO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 资产损失:临时表-对公贷款核销登记簿表控制层
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
@Path("ncbs-ccllnb-chk-write-off")
public class NcbsCcllnbChkWriteOffController extends ControllerSupport {
    
    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnbChkWriteOffController.class);
    
    /**
     * 服务对象
     */
    @Resource
    private NcbsCcllnbChkWriteOffService ncbsCcllnbChkWriteOffService;

    
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
    public ResultVo<NcbsCcllnbChkWriteOffOutVO> get(@PathParam("id") Long id) throws Exception {

        NcbsCcllnbChkWriteOffOutVO ncbsCcllnbChkWriteOffOutVO = ncbsCcllnbChkWriteOffService.queryInfoById(id);
        return super.success(ncbsCcllnbChkWriteOffOutVO);
    }
    
    
    /**
    * 保存-更新
        * @param ncbsCcllnbChkWriteOff 实体数据
        * @return ResultVO
    * @throws Exception
    * @see
    * @since
    */
    @POST
    @Path("save-update")
    @AspLog(title = "资产损失:临时表-对公贷款核销登记簿-保存", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo<NcbsCcllnbChkWriteOff> saveOrUpdate(@RequestBody BaseInput<NcbsCcllnbChkWriteOff> ncbsCcllnbChkWriteOff) throws Exception {
        return super.success(ncbsCcllnbChkWriteOffService.saveOrUpdate(ncbsCcllnbChkWriteOff.getBizData(), getLoginUser()));
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
    public ResultVo<BaseData<NcbsCcllnbChkWriteOffOutVO>> pageList(@RequestBody BaseQuery<NcbsCcllnbChkWriteOff> query) throws Exception {
        
        Page<NcbsCcllnbChkWriteOffOutVO> ncbsCcllnbChkWriteOffList = ncbsCcllnbChkWriteOffService.listPage(query,getLoginUser());
        return super.success(query.condition, ncbsCcllnbChkWriteOffList);
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
    @AspLog(title = "资产损失:临时表-对公贷款核销登记簿-删除", bizType = AuditOpBizType.DELETE, isSaveRequestData = false)
    public ResultVo deleteById(@PathParam("id") Long id) throws Exception {
        ncbsCcllnbChkWriteOffService.deleteById(id);
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
    @AspLog(title = "资产损失:临时表-对公贷款核销登记簿-批量删除", bizType = AuditOpBizType.DELETE_ALL, isSaveRequestData = false)
    public ResultVo deleteAllById(@RequestBody List<Long> ids) throws Exception {
        ncbsCcllnbChkWriteOffService.deleteAllByIds(ids);
        return ResultVo.success();
    }


}