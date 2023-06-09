package com.changhong.bems.dto;

import com.changhong.sei.core.dto.BaseEntityDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 预算维度属性(DimensionAttribute)DTO类
 *
 * @author sei
 * @since 2021-04-22 12:54:29
 */
@ApiModel(description = "预算维度属性DTO")
public class BaseAttributeDto extends BaseEntityDto {
    private static final long serialVersionUID = 874180418915412119L;
    /**
     * 属性
     * 使用到的维度字段名,按asci码排序,逗号(,)分隔
     */
    @ApiModelProperty(value = "维度属性")
    protected String attribute;
    /**
     * 属性值hash
     */
    @ApiModelProperty(value = "维度属性值hash")
    protected Long attributeCode = -1L;
    /**
     * 预算期间
     */
    @ApiModelProperty(value = "预算期间")
    protected String period;
    /**
     * 预算期间名称
     */
    @ApiModelProperty(value = "预算期间名称")
    protected String periodName;
    /**
     * 预算科目
     */
    @ApiModelProperty(value = "预算科目")
    protected String item;
    /**
     * 预算科目名称
     */
    @ApiModelProperty(value = "预算科目名称")
    protected String itemName;
    /**
     * 组织
     */
    @ApiModelProperty(value = "组织")
    protected String org;
    /**
     * 组织名称
     */
    @ApiModelProperty(value = "组织名称")
    protected String orgName;
    /**
     * 项目
     */
    @ApiModelProperty(value = "项目")
    protected String project;
    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    protected String projectName;
    /**
     * 成本中心
     */
    @ApiModelProperty(value = "成本中心")
    protected String costCenter;
    /**
     * 成本中心名称
     */
    @ApiModelProperty(value = "成本中心名称")
    protected String costCenterName;
    /**
     * 自定义1
     */
    @ApiModelProperty(value = "自定义1")
    protected String udf1;
    /**
     * 自定义1名称
     */
    @ApiModelProperty(value = "自定义1名称")
    protected String udf1Name;
    /**
     * 自定义2
     */
    @ApiModelProperty(value = "自定义2")
    protected String udf2;
    /**
     * 自定义2名称
     */
    @ApiModelProperty(value = "自定义2名称")
    protected String udf2Name;
    /**
     * 自定义3
     */
    @ApiModelProperty(value = "自定义3")
    protected String udf3;
    /**
     * 自定义3名称
     */
    @ApiModelProperty(value = "自定义3名称")
    protected String udf3Name;
    /**
     * 自定义4
     */
    @ApiModelProperty(value = "自定义4")
    protected String udf4;
    /**
     * 自定义4名称
     */
    @ApiModelProperty(value = "自定义4名称")
    protected String udf4Name;
    /**
     * 自定义5
     */
    @ApiModelProperty(value = "自定义5")
    protected String udf5;
    /**
     * 自定义5名称
     */
    @ApiModelProperty(value = "自定义5名称")
    protected String udf5Name;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Long getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(Long attributeCode) {
        this.attributeCode = attributeCode;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf1Name() {
        return udf1Name;
    }

    public void setUdf1Name(String udf1Name) {
        this.udf1Name = udf1Name;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf2Name() {
        return udf2Name;
    }

    public void setUdf2Name(String udf2Name) {
        this.udf2Name = udf2Name;
    }

    public String getUdf3() {
        return udf3;
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }

    public String getUdf3Name() {
        return udf3Name;
    }

    public void setUdf3Name(String udf3Name) {
        this.udf3Name = udf3Name;
    }

    public String getUdf4() {
        return udf4;
    }

    public void setUdf4(String udf4) {
        this.udf4 = udf4;
    }

    public String getUdf4Name() {
        return udf4Name;
    }

    public void setUdf4Name(String udf4Name) {
        this.udf4Name = udf4Name;
    }

    public String getUdf5() {
        return udf5;
    }

    public void setUdf5(String udf5) {
        this.udf5 = udf5;
    }

    public String getUdf5Name() {
        return udf5Name;
    }

    public void setUdf5Name(String udf5Name) {
        this.udf5Name = udf5Name;
    }
}