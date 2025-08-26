package com.cebbank.tms.wenjing.ift.domain;

import com.cebbank.tms.wenjing.common.annotation.In;
import com.cebbank.tms.wenjing.common.constant.UTaxConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产损失:临时表-抵债资产对公贷款关联表实体类
 * <pre>
 * 
 * </pre>
 * @author 系统重构
 * @version V2.0.0
 * @see
 * @since
 * @date 2025-08-25 10:00:00
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
@Table(name = "tp_ncbs_cgmdab_cl_loan_rlvc")
public class NcbsCgmdabClLoanRlvc implements Serializable {
    private static final long serialVersionUID = 562307720733063924L;
    /**
    * 保存修改时，入参自动去除系统字段
    */   
    public static final String FIELD_SAVE = "createUserId,createUserName,createTime,updateUserId,updateUserName,updateTime,isValid,reserved1,reserved2,reserved3,reserved4,reserved5,reserved6,reserved7,reserved8,reserved9,reserved10";
    public static final String FIELD_SEARCH = "createUserId,createUserName,createTime,updateUserId,updateUserName,updateTime,isValid,reserved1,reserved2,reserved3,reserved4,reserved5,reserved6,reserved7,reserved8,reserved9,reserved10";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
            
    /**        
    * 抵债资产编号
    */
    @Column(name = "commute_debt_ast_no")
    private String commuteDebtAstNo;
        
    /**        
    * 贷款借据号
    */
    @Column(name = "ln_due_bill_no")
    private String lnDueBillNo;
        
    /**        
    * 明细序号
    */
    @Column(name = "dtl_seri_no")
    private Integer dtlSeriNo;
        
    /**        
    * 信贷资产减值准备
    */
    @Column(name = "cred_ast_impai_provis")
    private BigDecimal credAstImpaiProvis;
        
    /**        
    * 内部户账号
    */
    @Column(name = "inte_acct_acct_no")
    private String inteAcctAcctNo;
        
    /**        
    * 接收费用
    */
    @Column(name = "rcv_cost")
    private BigDecimal rcvCost;
        
    /**        
    * 支付补价
    */
    @Column(name = "pay_comp_price")
    private BigDecimal payCompPrice;
        
    /**        
    * 还本金额
    */
    @Column(name = "repay_pri_amt")
    private BigDecimal repayPriAmt;
        
    /**        
    * 还息金额
    */
    @Column(name = "repay_int_amt")
    private BigDecimal repayIntAmt;
        
    /**        
    * 抵债资产关联状态
    */
    @Column(name = "commute_debt_ast_assoc_stat")
    private String commuteDebtAstAssocStat;
        
    /**        
    * 减值归还本金
    */
    @Column(name = "depr_retu_pri")
    private BigDecimal deprRetuPri;
        
    /**        
    * 减值归还利息
    */
    @Column(name = "depr_retu_int")
    private BigDecimal deprRetuInt;
        
    /**        
    * 归还正常本金
    */
    @Column(name = "retu_regu_pri")
    private BigDecimal retuReguPri;
        
    /**        
    * 归还逾期本金
    */
    @Column(name = "retu_overd_pri")
    private BigDecimal retuOverdPri;
        
    /**        
    * 归还呆滞本金
    */
    @Column(name = "retu_dead_ln_pri")
    private BigDecimal retuDeadLnPri;
        
    /**        
    * 归还呆账本金
    */
    @Column(name = "retu_bad_debt_pri")
    private BigDecimal retuBadDebtPri;
        
    /**        
    * 归还表内应计利息
    */
    @Column(name = "retu_rece_accru_int")
    private BigDecimal retuReceAccruInt;
        
    /**        
    * 归还表内应收欠息
    */
    @Column(name = "retu_rece_deb_int")
    private BigDecimal retuReceDebInt;
        
    /**        
    * 归还表内应计罚息
    */
    @Column(name = "retu_rece_accru_pena_int")
    private BigDecimal retuReceAccruPenaInt;
        
    /**        
    * 归还表内应收罚息
    */
    @Column(name = "retu_rece_pena_int")
    private BigDecimal retuRecePenaInt;
        
    /**        
    * 归还表内应计复息
    */
    @Column(name = "retu_on_bal_accru_compou_int")
    private BigDecimal retuOnBalAccruCompouInt;
        
    /**        
    * 归还表内应收复息
    */
    @Column(name = "retu_on_bal_compou_int")
    private BigDecimal retuOnBalCompouInt;
        
    /**        
    * 归还已核销本金
    */
    @Column(name = "retu_wri_off_pri")
    private BigDecimal retuWriOffPri;
        
    /**        
    * 信贷资产减值准备账号
    */
    @Column(name = "cred_ast_impai_provis_acct_no")
    private String credAstImpaiProvisAcctNo;
        
    /**        
    * 待处理抵债资产发生额
    */
    @Column(name = "to_be_deal_with_commute_debt_ast_occ_amt")
    private BigDecimal toBeDealWithCommuteDebtAstOccAmt;
        
    /**        
    * 合同流水号
    */
    @Column(name = "contr_no")
    private String contrNo;
        
    /**        
    * 客户号
    */
    @Column(name = "cust_no")
    private String custNo;
        
    /**        
    * 备用金额
    */
    @Column(name = "revol_fund_amt")
    private BigDecimal revolFundAmt;
        
    /**        
    * 备用金额1
    */
    @Column(name = "revol_amt1")
    private BigDecimal revolAmt1;
        
    /**        
    * 备用金额2
    */
    @Column(name = "revol_amt2")
    private BigDecimal revolAmt2;
        
    /**        
    * 备用字段
    */
    @Column(name = "rese_fie")
    private String reseFie;
        
    /**        
    * 备用字段1
    */
    @Column(name = "rese_fie_1")
    private String reseFie1;
        
    /**        
    * 备用字段2
    */
    @Column(name = "rese_fie_2")
    private String reseFie2;
        
    /**        
    * 处置费用
    */
    @Column(name = "dispo_cost")
    private BigDecimal dispoCost;
        
    /**        
    * 待还处置费用
    */
    @Column(name = "to_be_retu_dispo_cost")
    private BigDecimal toBeRetuDispoCost;
        
    /**        
    * 归还表外应计利息
    */
    @Column(name = "retu_col_accru_int")
    private BigDecimal retuColAccruInt;
        
    /**        
    * 归还表外应收欠息
    */
    @Column(name = "retu_col_deb_int")
    private BigDecimal retuColDebInt;
        
    /**        
    * 归还表外应计罚息
    */
    @Column(name = "retu_col_accru_pena_int")
    private BigDecimal retuColAccruPenaInt;
        
    /**        
    * 归还表外应收罚息
    */
    @Column(name = "retu_col_pena_int")
    private BigDecimal retuColPenaInt;
        
    /**        
    * 归还表外应计复息
    */
    @Column(name = "retu_off_bal_accru_compou_int")
    private BigDecimal retuOffBalAccruCompouInt;
        
    /**        
    * 归还表外应收复息
    */
    @Column(name = "retu_off_bal_compou_int")
    private BigDecimal retuOffBalCompouInt;
        
    /**        
    * 归还已核销利息
    */
    @Column(name = "retu_wri_off_int")
    private BigDecimal retuWriOffInt;
        
    /**        
    * 归还已核销本金利息
    */
    @Column(name = "retu_alr_wri_off_pri_int")
    private BigDecimal retuAlrWriOffPriInt;
        
    /**        
    * 归还已核销利息复息
    */
    @Column(name = "retu_wri_off_int_compou_int")
    private BigDecimal retuWriOffIntCompouInt;
        
    /**        
    * 维护柜员
    */
    @Column(name = "mainte_tell")
    private String mainteTell;
        
    /**        
    * 维护机构
    */
    @Column(name = "mainte_inst")
    private String mainteInst;
        
    /**        
    * 维护日期
    */
    @Column(name = "mainte_dt")
    private String mainteDt;
        
    /**        
    * 维护时间
    */
    @Column(name = "mainte_tm")
    private String mainteTm;
        
    /**        
    * 记录状态
    */
    @Column(name = "record_stat")
    private String recordStat;
        
    /**        
    * 机构代码
    */
    @Column(name = "group_no")
    @In(separator=UTaxConstant.COMMA)
    private String groupNo;
        
    /**        
    * 机构名称
    */
    @Column(name = "group_name")
    private String groupName;
        
    /**        
    * 纳税人识别号
    */
    @Column(name = "taxpayer_no")
    @In(separator=UTaxConstant.COMMA)
    private String taxpayerNo;
        
    /**        
    * 纳税人名称
    */
    @Column(name = "taxpayer")
    private String taxpayer;
        
    /**        
    * 创建人ID
    */
    @Column(name = "create_user_id")
    private String createUserId;
        
    /**        
    * 创建人名称
    */
    @Column(name = "create_user_name")
    private String createUserName;
        
    /**        
    * 创建时间
    */
    @Column(name = "create_time")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
        
    /**        
    * 更新人ID
    */
    @Column(name = "update_user_id")
    private String updateUserId;
        
    /**        
    * 更新人名称
    */
    @Column(name = "update_user_name")
    private String updateUserName;
        
    /**        
    * 更新时间
    */
    @Column(name = "update_time")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
        
    /**        
    * 预留字段1
    */
    @Column(name = "reserved1")
    private String reserved1;
        
    /**        
    * 预留字段2
    */
    @Column(name = "reserved2")
    private String reserved2;
        
    /**        
    * 预留字段3
    */
    @Column(name = "reserved3")
    private String reserved3;
        
    /**        
    * 预留字段4
    */
    @Column(name = "reserved4")
    private String reserved4;
        
    /**        
    * 预留字段5
    */
    @Column(name = "reserved5")
    private String reserved5;
        
    /**        
    * 预留字段6
    */
    @Column(name = "reserved6")
    private String reserved6;
        
    /**        
    * 预留字段7
    */
    @Column(name = "reserved7")
    private String reserved7;
        
    /**        
    * 预留字段8
    */
    @Column(name = "reserved8")
    private String reserved8;
        
    /**        
    * 预留字段9
    */
    @Column(name = "reserved9")
    private String reserved9;
        
    /**        
    * 预留字段10
    */
    @Column(name = "reserved10")
    private String reserved10;

}