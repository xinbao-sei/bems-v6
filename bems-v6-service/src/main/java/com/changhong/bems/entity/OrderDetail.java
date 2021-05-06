package com.changhong.bems.entity;

import com.changhong.sei.core.entity.ITenant;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 预算行项(OrderDetail)实体类
 *
 * @author sei
 * @since 2021-04-25 15:13:58
 */
@Entity
@Table(name = "order_detail")
@DynamicInsert
@DynamicUpdate
public class OrderDetail extends BaseAttribute implements ITenant, Serializable, Cloneable {
    private static final long serialVersionUID = -90286046160801596L;
    public static final String FIELD_ORDER_ID = "orderId";
    public static final String FIELD_ATTRIBUTE_HASH = "attributeHash";
    /**
     * 预算申请单id
     */
    @Column(name = "order_id")
    private String orderId;
    /**
     * 金额
     */
    @Column(name = "amount")
    private Double amount = 0d;
    /**
     * 预算池编码
     */
    @Column(name = "pool_code")
    private String poolCode;
    /**
     * 预算池金额
     */
    @Column(name = "pool_amount")
    private Double poolAmount = 0d;
    /**
     * 来源预算池编码
     */
    @Column(name = "origin_pool_code")
    private String originPoolCode;
    /**
     * 来源预算池金额
     */
    @Column(name = "origin_pool_amount")
    private Double originPoolAmount = 0d;
    /**
     * 属性值hash
     */
    @Column(name = "attribute_hash")
    private Long attributeHash = -1L;
    /**
     * 租户代码
     */
    @Column(name = "tenant_code")
    private String tenantCode;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPoolCode() {
        return poolCode;
    }

    public void setPoolCode(String poolCode) {
        this.poolCode = poolCode;
    }

    public Double getPoolAmount() {
        return poolAmount;
    }

    public void setPoolAmount(Double poolAmount) {
        this.poolAmount = poolAmount;
    }

    public String getOriginPoolCode() {
        return originPoolCode;
    }

    public void setOriginPoolCode(String originPoolCode) {
        this.originPoolCode = originPoolCode;
    }

    public Double getOriginPoolAmount() {
        return originPoolAmount;
    }

    public void setOriginPoolAmount(Double originPoolAmount) {
        this.originPoolAmount = originPoolAmount;
    }

    public Long getAttributeHash() {
        long result = 1;
        result = 31 * result + this.getItem().hashCode();
        result = 31 * result + this.getPeriod().hashCode();
        result = 31 * result + this.getOrg().hashCode();
        result = 31 * result + this.getProject().hashCode();

        result = 31 * result + this.getUdf1().hashCode();
        result = 31 * result + this.getUdf2().hashCode();
        result = 31 * result + this.getUdf3().hashCode();
        result = 31 * result + this.getUdf4().hashCode();
        result = 31 * result + this.getUdf5().hashCode();
        attributeHash = result;
        return attributeHash;
    }

    public void setAttributeHash(Long attributeHash) {
        this.attributeHash = attributeHash;
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
    public OrderDetail clone() {
        try {
            return (OrderDetail) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}