package com.changhong.bems.entity;

import com.changhong.bems.dto.OperationType;
import com.changhong.sei.core.entity.BaseEntity;
import com.changhong.sei.core.entity.ITenant;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预算执行记录(ExecutionRecord)实体类
 *
 * @author sei
 * @since 2021-04-25 15:10:03
 */
@Entity
@Table(name = "execution_record")
@DynamicInsert
@DynamicUpdate
public class ExecutionRecord extends BaseEntity implements ITenant, Serializable, Cloneable {
    private static final long serialVersionUID = -28943145565423431L;
    public static final String FIELD_EVENT_CODE = "eventCode";
    public static final String FIELD_BIZ_ID = "bizId";
    public static final String FIELD_OPERATION = "operation";
    /**
     * 预算主体id
     */
    @Column(name = "subject_id", updatable = false)
    private String subjectId;
    /**
     * 预算维度属性id
     */
    @Column(name = "attribute_code", updatable = false)
    private Long attributeCode;
    /**
     * 预算池编码
     */
    @Column(name = "pool_code")
    private String poolCode;
    /**
     * 操作类型
     */
    @Column(name = "operation_type", updatable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operation;
    /**
     * 金额
     */
    @Column(name = "amount", updatable = false)
    private Double amount = 0d;
    /**
     * 是预算池金额
     */
    @Column(name = "is_pool_amount")
    private Boolean isPoolAmount = Boolean.TRUE;
    /**
     * 操作时间
     */
    @Column(name = "operation_time", updatable = false)
    private LocalDateTime opTime;
    /**
     * 操作人账号
     */
    @Column(name = "operation_user_account", updatable = false)
    private String opUserAccount;
    /**
     * 操作人名称
     */
    @Column(name = "operation_user_name", updatable = false)
    private String opUserName;
    /**
     * 业务事件
     */
    @Column(name = "biz_event", updatable = false)
    private String eventCode;
    /**
     * 业务单id
     */
    @Column(name = "biz_id", updatable = false)
    private String bizId;
    /**
     * 业务单编码
     */
    @Column(name = "biz_code", updatable = false)
    private String bizCode;
    /**
     * 业务描述
     */
    @Column(name = "biz_remark", updatable = false)
    private String bizRemark;
    /**
     * 租户代码
     */
    @Column(name = "tenant_code", updatable = false)
    private String tenantCode;

    public ExecutionRecord() {
    }

    public ExecutionRecord(String poolCode, OperationType operation, Double amount, String eventCode) {
        this.poolCode = poolCode;
        this.operation = operation;
        this.amount = amount;
        this.eventCode = eventCode;
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

    public String getPoolCode() {
        return poolCode;
    }

    public void setPoolCode(String poolCode) {
        this.poolCode = poolCode;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public Boolean getIsPoolAmount() {
        return isPoolAmount;
    }

    public void setIsPoolAmount(Boolean poolAmount) {
        isPoolAmount = poolAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getOpTime() {
        return opTime;
    }

    public void setOpTime(LocalDateTime opTime) {
        this.opTime = opTime;
    }

    public String getOpUserAccount() {
        return opUserAccount;
    }

    public void setOpUserAccount(String opUserAccount) {
        this.opUserAccount = opUserAccount;
    }

    public String getOpUserName() {
        return opUserName;
    }

    public void setOpUserName(String opUserName) {
        this.opUserName = opUserName;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String bizEvent) {
        this.eventCode = bizEvent;
    }

    public String getBizRemark() {
        return bizRemark;
    }

    public void setBizRemark(String bizRemark) {
        this.bizRemark = bizRemark;
    }

    @Override
    public String getTenantCode() {
        return tenantCode;
    }

    @Override
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    @Override
    public ExecutionRecord clone() {
        try {
            return (ExecutionRecord) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}