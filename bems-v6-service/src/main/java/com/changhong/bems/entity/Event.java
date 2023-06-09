package com.changhong.bems.entity;

import com.changhong.sei.core.dto.IRank;
import com.changhong.sei.core.entity.BaseAuditableEntity;
import com.changhong.sei.core.entity.ICodeUnique;
import com.changhong.sei.core.entity.IFrozen;
import com.changhong.sei.core.entity.ITenant;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 预算事件(Event)实体类
 *
 * @author sei
 * @since 2021-04-22 12:54:29
 */
@Entity
@Table(name = "event_")
@DynamicInsert
@DynamicUpdate
public class Event extends BaseAuditableEntity implements ITenant, ICodeUnique, IRank, IFrozen, Serializable {
    private static final long serialVersionUID = -57036484686343107L;

    /**
     * 代码
     */
    @Column(name = "code")
    private String code;
    /**
     * 名称
     */
    @Column(name = "name")
    private String name;
    /**
     * 标签名(多个用逗号分隔)
     */
    @Column(name = "label_name")
    private String label;
    /**
     * 业务来源
     */
    @Column(name = "biz_from")
    private String bizFrom;
    /**
     * 冻结
     */
    @Column(name = "frozen_")
    private Boolean frozen = Boolean.FALSE;
    /**
     * 系统必要
     */
    @Column(name = "required")
    private Boolean required = Boolean.FALSE;
    /**
     * 排序
     */
    @Column(name = "rank_")
    private Integer rank = 0;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBizFrom() {
        return bizFrom;
    }

    public void setBizFrom(String bizFrom) {
        this.bizFrom = bizFrom;
    }

    @Override
    public Boolean getFrozen() {
        return frozen;
    }

    @Override
    public void setFrozen(Boolean frozen) {
        this.frozen = frozen;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
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