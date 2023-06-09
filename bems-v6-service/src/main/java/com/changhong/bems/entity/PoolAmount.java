package com.changhong.bems.entity;

import com.changhong.bems.dto.OperationType;
import com.changhong.sei.core.entity.BaseAuditableEntity;
import com.changhong.sei.core.entity.BaseEntity;
import com.changhong.sei.core.entity.ITenant;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 预算池金额(PoolAmount)实体类
 *
 * @author sei
 * @since 2021-04-25 15:14:00
 */
@Entity
@Table(name = "pool_amount")
@DynamicInsert
@DynamicUpdate
public class PoolAmount extends BaseEntity implements ITenant, Serializable {
    private static final long serialVersionUID = 434318292053003122L;
    public static final String FIELD_POOL_ID = "poolId";
    public static final String FIELD_POOL_CODE = "poolCode";
    public static final String FIELD_INTERNAL = "internal";
    public static final String FIELD_OPERATION = "operation";
    /**
     * 预算池id
     */
    @Column(name = "pool_id")
    private String poolId;
    /**
     * 预算池编码
     */
    @Column(name = "pool_code")
    private String poolCode;
    /**
     * 是否是预算内部操作
     * 内部操作: 预算调整,预算分解,预算结转
     * 外部操作: 总额新增注入,外部系统使用
     */
    @Column(name = "internal")
    private Boolean internal = Boolean.TRUE;
    /**
     * 操作类型
     */
    @Column(name = "operation_type")
    @Enumerated(EnumType.STRING)
    private OperationType operation;
    /**
     * 金额
     */
    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;

    @Version
    @Column(name = "optimistic_lock")
    private Integer version = 0;
    /**
     * 租户代码
     */
    @Column(name = "tenant_code")
    private String tenantCode;

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    public String getPoolCode() {
        return poolCode;
    }

    public void setPoolCode(String poolCode) {
        this.poolCode = poolCode;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operationType) {
        this.operation = operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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