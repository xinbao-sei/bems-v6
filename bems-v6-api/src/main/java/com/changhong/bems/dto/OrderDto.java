package com.changhong.bems.dto;

import com.changhong.sei.core.dto.BaseEntityDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 预算申请单(Order)DTO类
 *
 * @author sei
 * @since 2021-04-25 15:13:57
 */
@ApiModel(description = "预算申请单DTO")
public class OrderDto extends BaseEntityDto {
    private static final long serialVersionUID = 927785272928546873L;
    /**
     * 申请单号
     */
    @ApiModelProperty(value = "申请单号")
    private String code;
    /**
     * 预算主体id
     */
    @ApiModelProperty(value = "预算主体id")
    private String subjectId;
    /**
     * 预算主体名称
     */
    @ApiModelProperty(value = "预算主体名称")
    private String subjectName;
    /**
     * 币种代码
     */
    @ApiModelProperty(value = "币种代码")
    private String currencyCode;
    /**
     * 币种名称
     */
    @ApiModelProperty(value = "币种名称")
    private String currencyName;
    /**
     * 预算类型id
     */
    @ApiModelProperty(value = "预算类型id")
    private String categoryId;
    /**
     * 预算类型名称
     */
    @ApiModelProperty(value = "预算类型名称")
    private String categoryName;
    /**
     * 期间分类
     */
    @ApiModelProperty(value = "期间分类")
    private PeriodType periodType;
    /**
     * 订单类型
     */
    @ApiModelProperty(value = "订单类型")
    private OrderCategory orderCategory;
    /**
     * 申请金额
     */
    @ApiModelProperty(value = "申请金额")
    private Double applyAmount = 0d;
    /**
     * 申请组织id
     */
    @ApiModelProperty(value = "申请组织id")
    private String applyOrgId;
    /**
     * 申请组织代码
     */
    @ApiModelProperty(value = "申请组织代码")
    private String applyOrgCode;
    /**
     * 申请组织名称
     */
    @ApiModelProperty(value = "申请组织名称")
    private String applyOrgName;
    /**
     * 申请人代码
     */
    @ApiModelProperty(value = "申请人代码")
    private String applyUserAccount;
    /**
     * 申请人名称
     */
    @ApiModelProperty(value = "申请人名称")
    private String applyUserName;
    /**
     * 归口管理组织Id
     */
    @ApiModelProperty(value = "归口管理组织id")
    private String managerOrgId;
    /**
     * 归口管理组织代码
     */
    @ApiModelProperty(value = "归口管理组织代码")
    private String managerOrgCode;
    /**
     * 归口管理组织名称
     */
    @ApiModelProperty(value = "归口管理组织名称")
    private String managerOrgName;
    /**
     * 备注说明
     */
    @ApiModelProperty(value = "备注说明")
    private String remark;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private OrderStatus status;

    @ApiModelProperty(value = "订单行项明细")
    private OrderDetailDto orderDetails;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public OrderCategory getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(OrderCategory orderCategory) {
        this.orderCategory = orderCategory;
    }

    public Double getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(Double applyAmount) {
        this.applyAmount = applyAmount;
    }

    public String getApplyOrgId() {
        return applyOrgId;
    }

    public void setApplyOrgId(String applyOrgId) {
        this.applyOrgId = applyOrgId;
    }

    public String getApplyOrgCode() {
        return applyOrgCode;
    }

    public void setApplyOrgCode(String applyOrgCode) {
        this.applyOrgCode = applyOrgCode;
    }

    public String getApplyOrgName() {
        return applyOrgName;
    }

    public void setApplyOrgName(String applyOrgName) {
        this.applyOrgName = applyOrgName;
    }

    public String getApplyUserAccount() {
        return applyUserAccount;
    }

    public void setApplyUserAccount(String applyUserAccount) {
        this.applyUserAccount = applyUserAccount;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getManagerOrgId() {
        return managerOrgId;
    }

    public void setManagerOrgId(String managerOrgId) {
        this.managerOrgId = managerOrgId;
    }

    public String getManagerOrgCode() {
        return managerOrgCode;
    }

    public void setManagerOrgCode(String managerOrgCode) {
        this.managerOrgCode = managerOrgCode;
    }

    public String getManagerOrgName() {
        return managerOrgName;
    }

    public void setManagerOrgName(String managerOrgName) {
        this.managerOrgName = managerOrgName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderDetailDto getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetailDto orderDetails) {
        this.orderDetails = orderDetails;
    }
}