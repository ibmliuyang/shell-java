package com.cebbank.tms.wenjing.ift.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.cebbank.poin.core.log.CSPSLogFactory;
import com.cebbank.poin.core.log.CSPSLogger;
import com.cebbank.tms.wenjing.common.annotation.AspLog;
import com.cebbank.tms.wenjing.common.base.BaseData;
import com.cebbank.tms.wenjing.common.base.BaseInput;
import com.cebbank.tms.wenjing.common.base.BaseQuery;
import com.cebbank.tms.wenjing.common.base.ResultVo;
import com.cebbank.tms.wenjing.common.constant.UTaxConstant;
import com.cebbank.tms.wenjing.common.constant.enums.AuditOpBizType;
import com.cebbank.tms.wenjing.common.handler.ControllerSupport;
import com.cebbank.tms.wenjing.dppt.utils.SysInterfaceLogUtil;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnbNonPerfmgAsset;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnbNonPerfmgAssetService;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnbNonPerfmgAssetOutVO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产损失:临时表-对公贷款不良资产表控制层
 */
@Component
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("ncbs-ccllnb-non-perfmg-asset")
public class NcbsCcllnbNonPerfmgAssetController extends ControllerSupport {
    
    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnbNonPerfmgAssetController.class);
    
    @Resource
    private NcbsCcllnbNonPerfmgAssetService ncbsCcllnbNonPerfmgAssetService;

    @GET
    @Path("get/{id}")
    public ResultVo<NcbsCcllnbNonPerfmgAssetOutVO> get(@PathParam("id") Long id) throws Exception {
        NcbsCcllnbNonPerfmgAssetOutVO outVO = ncbsCcllnbNonPerfmgAssetService.queryInfoById(id);
        return super.success(outVO);
    }
    
    @POST
    @Path("save-update")
    @AspLog(title = "对公贷款不良资产表-保存", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo<NcbsCcllnbNonPerfmgAsset> saveOrUpdate(@RequestBody BaseInput<NcbsCcllnbNonPerfmgAsset> entity) throws Exception {
        return super.success(ncbsCcllnbNonPerfmgAssetService.saveOrUpdate(entity.getBizData(), getLoginUser()));
    }

    @POST
    @Path("page-list")
    public ResultVo<BaseData<NcbsCcllnbNonPerfmgAssetOutVO>> pageList(@RequestBody BaseQuery<NcbsCcllnbNonPerfmgAsset> query) throws Exception {
        Page<NcbsCcllnbNonPerfmgAssetOutVO> list = ncbsCcllnbNonPerfmgAssetService.listPage(query, getLoginUser());
        return super.success(query.condition, list);
    }
    
    @POST
    @Path("delete-by-id/{id}")
    @AspLog(title = "对公贷款不良资产表-删除", bizType = AuditOpBizType.DELETE, isSaveRequestData = false)
    public ResultVo deleteById(@PathParam("id") Long id) throws Exception {
        ncbsCcllnbNonPerfmgAssetService.deleteById(id);
        return ResultVo.success();
    }

    @POST
    @Path("delete-all-by-id")
    @AspLog(title = "对公贷款不良资产表-批量删除", bizType = AuditOpBizType.DELETE_ALL, isSaveRequestData = false)
    public ResultVo deleteAllById(@RequestBody List<Long> ids) throws Exception {
        ncbsCcllnbNonPerfmgAssetService.deleteAllByIds(ids);
        return ResultVo.success();
    }

    /**
     * 数据同步接口
     */
    @Path("/sync")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @AspLog(title = "对公贷款不良资产表-数据同步", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo<DataSyncResult> sync(DataSyncRequest request) throws Exception {
        Long startTime = System.currentTimeMillis();
        List<String> errList = new ArrayList<>();
        String interfaceName = "对公贷款不良资产表";
        String tableName = request != null ? request.getTableName() : "";
        
        log.info(StrUtil.format("开始处理对公贷款不良资产表数据同步请求: {}", JSON.toJSONString(request)));
        
        try {
            // 参数验证
            if (request == null) {
                errList.add("请求参数不能为空");
                log.error("请求参数不能为空");
            } else {
                if (StrUtil.isBlank(request.getFilePath())) {
                    errList.add("文件路径不能为空");
                    log.error("文件路径不能为空");
                }
                
                if (StrUtil.isBlank(request.getProcessDate())) {
                    errList.add("处理日期不能为空");
                    log.error("处理日期不能为空");
                }
                
                if (StrUtil.isBlank(request.getTableName())) {
                    errList.add("表名不能为空");
                    log.error("表名不能为空");
                }
            }
            
            // 如果参数验证有错误，直接返回
            if (!errList.isEmpty()) {
                Long endTime = System.currentTimeMillis();
                Long consumingTime = endTime - startTime;
                SysInterfaceLogUtil.insertIFTLog("", tableName, interfaceName, JSON.toJSONString(request), JSON.toJSONString(errList), getLoginUser(), consumingTime);
                return ResultVo.error(StrUtil.join(UTaxConstant.COMMA, errList));
            }
            
            // 调用服务层处理数据同步
            DataSyncResult result = ncbsCcllnbNonPerfmgAssetService.syncData(request);
            log.info(StrUtil.format("对公贷款不良资产表数据同步完成: {}", JSON.toJSONString(result)));
            
            Long endTime = System.currentTimeMillis();
            Long consumingTime = endTime - startTime;
            SysInterfaceLogUtil.insertIFTLog("", request.getTableName(), interfaceName, JSON.toJSONString(request), JSON.toJSONString(result), getLoginUser(), consumingTime);
            return ResultVo.success(result);
            
        } catch (Exception e) {
            log.error(StrUtil.format("对公贷款不良资产表数据同步失败: {}", e.getMessage()), e);
            errList.add("数据同步失败: " + e.getMessage());
            
            Long endTime = System.currentTimeMillis();
            Long consumingTime = endTime - startTime;
            SysInterfaceLogUtil.insertIFTLog("", tableName, interfaceName, JSON.toJSONString(request), JSON.toJSONString(errList), getLoginUser(), consumingTime);
            return ResultVo.error(StrUtil.join(UTaxConstant.COMMA, errList));
        }
    }
}