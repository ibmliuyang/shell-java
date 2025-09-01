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
import com.cebbank.tms.wenjing.common.exception.BizException;
import com.cebbank.tms.wenjing.common.handler.ControllerSupport;
import com.cebbank.tms.wenjing.dppt.utils.SysInterfaceLogUtil;
import com.cebbank.tms.wenjing.ift.domain.NcbsCcllnaAcct;
import com.cebbank.tms.wenjing.ift.service.NcbsCcllnaAcctService;
import com.cebbank.tms.wenjing.ift.vo.DataSyncRequest;
import com.cebbank.tms.wenjing.ift.vo.DataSyncResult;
import com.cebbank.tms.wenjing.ift.vo.out.NcbsCcllnaAcctOutVO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产损失:临时表-对公贷款户主表表控制层
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
@Path("ncbs-ccllna-acct")
public class NcbsCcllnaAcctController extends ControllerSupport {
    
    private static CSPSLogger log = CSPSLogFactory.get(NcbsCcllnaAcctController.class);
    
    /**
     * 服务对象
     */
    @Resource
    private NcbsCcllnaAcctService ncbsCcllnaAcctService;

    
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
    public ResultVo<NcbsCcllnaAcctOutVO> get(@PathParam("id") Long id) throws Exception {

        NcbsCcllnaAcctOutVO ncbsCcllnaAcctOutVO = ncbsCcllnaAcctService.queryInfoById(id);
        return super.success(ncbsCcllnaAcctOutVO);
    }
    
    
    /**
    * 保存-更新
        * @param ncbsCcllnaAcct 实体数据
        * @return ResultVO
    * @throws Exception
    * @see
    * @since
    */
    @POST
    @Path("save-update")
    @AspLog(title = "资产损失:临时表-对公贷款户主表-保存", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo<NcbsCcllnaAcct> saveOrUpdate(@RequestBody BaseInput<NcbsCcllnaAcct> ncbsCcllnaAcct) throws Exception {
        return super.success(ncbsCcllnaAcctService.saveOrUpdate(ncbsCcllnaAcct.getBizData(), getLoginUser()));
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
    public ResultVo<BaseData<NcbsCcllnaAcctOutVO>> pageList(@RequestBody BaseQuery<NcbsCcllnaAcct> query) throws Exception {
        
        Page<NcbsCcllnaAcctOutVO> ncbsCcllnaAcctList = ncbsCcllnaAcctService.listPage(query,getLoginUser());
        return super.success(query.condition, ncbsCcllnaAcctList);
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
    @AspLog(title = "资产损失:临时表-对公贷款户主表-删除", bizType = AuditOpBizType.DELETE, isSaveRequestData = false)
    public ResultVo deleteById(@PathParam("id") Long id) throws Exception {
        ncbsCcllnaAcctService.deleteById(id);
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
    @AspLog(title = "资产损失:临时表-对公贷款户主表-批量删除", bizType = AuditOpBizType.DELETE_ALL, isSaveRequestData = false)
    public ResultVo deleteAllById(@RequestBody List<Long> ids) throws Exception {
        ncbsCcllnaAcctService.deleteAllByIds(ids);
        return ResultVo.success();
    }

    /**
     * 数据同步接口
     * @param request 数据同步请求参数
     * @return ResultVO
     * @throws Exception
     */
    @POST
    @Path("sync")
    @AspLog(title = "资产损失:临时表-对公贷款户主表-数据同步", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo sync(@RequestBody DataSyncRequest request) throws Exception {
        Long startTime = System.currentTimeMillis();
        List<String> errList = new ArrayList<>();
        String interfaceName = "对公贷款账户主表";
        String tableName = request != null ? request.getTableName() : "";
        
        log.info(StrUtil.format("开始处理对公贷款账户主表数据同步请求:{}", JSON.toJSONString(request)));
        
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
            DataSyncResult result = ncbsCcllnaAcctService.syncData(request);
            log.info(StrUtil.format("对公贷款账户主表数据同步完成:{}", JSON.toJSONString(result)));
            
            Long endTime = System.currentTimeMillis();
            Long consumingTime = endTime - startTime;
            SysInterfaceLogUtil.insertIFTLog("", request.getTableName(), interfaceName, JSON.toJSONString(request), JSON.toJSONString(result), getLoginUser(), consumingTime);
            return ResultVo.success(result);
            
        } catch (Exception e) {
            log.error("对公贷款账户主表数据同步失败", e);
            errList.add("数据导入失败:" + e.getMessage());
            
            Long endTime = System.currentTimeMillis();
            Long consumingTime = endTime - startTime;
            SysInterfaceLogUtil.insertIFTLog("", tableName, interfaceName, JSON.toJSONString(request), JSON.toJSONString(errList), getLoginUser(), consumingTime);
            return ResultVo.error(StrUtil.join(UTaxConstant.COMMA, errList));
        }
    }

    /**
     * 获取需要维护的借据号列表
     * @param request 请求参数
     * @return ResultVo
     * @throws Exception
     */
    @POST
    @Path("getMaintainLnDueBillNos")
    @AspLog(title = "对公贷款账户主表-获取维护借据号", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo<List<String>> getMaintainLnDueBillNos(@RequestBody DataSyncRequest request) throws Exception {
        log.info(StrUtil.format("开始获取维护借据号列表: {}", JSON.toJSONString(request)));
        
        try {
            List<String> lnDueBillNos = ncbsCcllnaAcctService.getMaintainLnDueBillNos();
            log.info(StrUtil.format("获取维护借据号完成，数量: {}", lnDueBillNos.size()));
            return ResultVo.success(lnDueBillNos);
        } catch (Exception e) {
            log.error(StrUtil.format("获取维护借据号失败: {}", e.getMessage()), e);
            return ResultVo.error("获取维护借据号失败: " + e.getMessage());
        }
    }

    /**
     * 获取要过滤掉的借据号列表（贷款属性表中ln_biz_clas为2或3的）
     * @param request 请求参数
     * @return ResultVo
     * @throws Exception
     */
    @POST
    @Path("getExclusionLnDueBillNos")
    @AspLog(title = "对公贷款账户主表-获取过滤借据号", bizType = AuditOpBizType.UPDATE, isSaveRequestData = false)
    public ResultVo<List<String>> getExclusionLnDueBillNos(@RequestBody DataSyncRequest request) throws Exception {
        log.info(StrUtil.format("开始获取过滤借据号列表: {}", JSON.toJSONString(request)));
        
        try {
            List<String> lnDueBillNos = ncbsCcllnaAcctService.getExclusionLnDueBillNos();
            log.info(StrUtil.format("获取过滤借据号完成，数量: {}", lnDueBillNos.size()));
            return ResultVo.success(lnDueBillNos);
        } catch (Exception e) {
            log.error(StrUtil.format("获取过滤借据号失败: {}", e.getMessage()), e);
            return ResultVo.error("获取过滤借据号失败: " + e.getMessage());
        }
    }


}