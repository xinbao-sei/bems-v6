package com.changhong.bems.entity;

import com.changhong.bems.dto.PeriodType;
import com.changhong.sei.core.entity.BaseEntity;
import com.changhong.sei.core.entity.ICodeUnique;
import com.changhong.sei.core.entity.ITenant;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预算池(Pool)实体类
 *
 * @author sei
 * @since 2021-04-22 12:54:27
 */
@Entity
@Table(name = "pool")
@DynamicInsert
@DynamicUpdate
public class Pool extends BaseEntity implements ITenant, ICodeUnique, Serializable {
    private static final long serialVersionUID = 345291355065499642L;
    public static final String FIELD_SUBJECT_ID = "subjectId";
    public static final String FIELD_ATTRIBUTE_CODE = "attributeCode";
    public static final String FIELD_PERIOD_TYPE = "periodType";
    public static final String FIELD_ACTIVED = "actived";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";

    /**
     * 代码
     */
    @Column(name = "code", updatable = false)
    private String code;
    /**
     * 预算主体id
     */
    @Column(name = "subject_id", updatable = false)
    private String subjectId;
    /**
     * 预算维度属性code
     */
    @Column(name = "attribute_code", updatable = false)
    private Long attributeCode;
    /**
     * 币种代码
     */
    @Column(name = "currency_code", updatable = false)
    private String currencyCode;
    /**
     * 币种名称
     */
    @Column(name = "currency_name", updatable = false)
    private String currencyName;
    /**
     * 归口管理部门
     */
    @Column(name = "manage_org")
    private String manageOrg;
    /**
     * 归口管理部门名称
     */
    @Column(name = "manage_org_name")
    private String manageOrgName;
    /**
     * 期间分类
     */
    @Column(name = "period_category", updatable = false)
    @Enumerated(EnumType.STRING)
    private PeriodType periodType;
    /**
     * 所属年度
     */
    @Column(name = "year")
    private Integer year;
    /**
     * 起始日期
     */
    @Column(name = "start_date")
    private LocalDate startDate;
    /**
     * 截止日期
     */
    @Column(name = "end_date")
    private LocalDate endDate;
    /**
     * 是否启用
     */
    @Column(name = "is_actived")
    private Boolean actived = Boolean.TRUE;
    /**
     * 是否可延期
     */
    @Column(name = "is_delay")
    private Boolean delay = Boolean.FALSE;
    /**
     * 总额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount = BigDecimal.ZERO;
    /**
     * 使用量
     */
    @Column(name = "used_amount")
    private BigDecimal usedAmount = BigDecimal.ZERO;
    /**
     * 当前可用余额
     */
    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;
    /**
     * 创建时间
     */
    @Column(name = "created_date", updatable = false)
    protected LocalDateTime createdDate;
    /**
     * 租户代码
     */
    @Column(name = "tenant_code")
    private String tenantCode;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public Long getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(Long attributeCode) {
        this.attributeCode = attributeCode;
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

    public String getManageOrg() {
        return manageOrg;
    }

    public void setManageOrg(String manageOrg) {
        this.manageOrg = manageOrg;
    }

    public String getManageOrgName() {
        return manageOrgName;
    }

    public void setManageOrgName(String manageOrgName) {
        this.manageOrgName = manageOrgName;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getActived() {
        return actived;
    }

    public void setActived(Boolean actived) {
        this.actived = actived;
    }

    public Boolean getDelay() {
        return delay;
    }

    public void setDelay(Boolean delay) {
        this.delay = delay;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String getTenantCode() {
        return tenantCode;
    }

    @Override
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

}