package com.changhong.bems.dto;

import com.changhong.sei.core.dto.serializer.EnumJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 预算策略(Strategy)DTO类
 *
 * @author sei
 * @since 2021-04-22 11:12:07
 */
@ApiModel(description = "预算策略DTO")
public class StrategyDto implements Serializable {
    private static final long serialVersionUID = -51089250630553840L;
    /**
     *
     */
    private String id;
    /**
     * 策略代码
     */
    @ApiModelProperty(value = "策略代码")
    private String code;
    /**
     * 策略名称
     */
    @ApiModelProperty(value = "策略名称")
    private String name;
    /**
     * 策略类路径
     */
    @ApiModelProperty(value = "策略类路径")
    private String classPath;
    /**
     * 策略类别
     */
    @JsonSerialize(using = EnumJsonSerializer.class)
    @ApiModelProperty(value = "策略类别")
    private StrategyCategory category;
    /**
     * 策略描述
     */
    @ApiModelProperty(value = "策略描述")
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public StrategyCategory getCategory() {
        return category;
    }

    public void setCategory(StrategyCategory category) {
        this.category = category;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}