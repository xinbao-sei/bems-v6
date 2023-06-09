package com.changhong.bems.dto;

import com.changhong.sei.core.dto.BaseEntityDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 预算类型(Category)DTO类
 *
 * @author sei
 * @since 2021-04-22 12:54:27
 */
@ApiModel(description = "预算类型DTO")
public class CategoryDto extends BaseEntityDto {
    private static final long serialVersionUID = -16657201188691998L;
    /**
     * 名称
     */
    @NotBlank
    @Size(max = 50)
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    /**
     * 预算分类
     */
    @NotNull
    @ApiModelProperty(value = "预算分类", required = true)
    private Classification classification;
    /**
     * 类型分类
     */
    @NotNull
    @ApiModelProperty(value = "类型分类", required = true)
    private CategoryType type;
    /**
     * 预算主体id
     */
    @Size(max = 36)
    @ApiModelProperty(value = "预算主体id", example = "为空时，为通用类型")
    private String subjectId;
    /**
     * 预算主体名称
     */
    @ApiModelProperty(value = "预算主体名称")
    private String subjectName;
    /**
     * 期间类型
     */
    @NotNull
    @ApiModelProperty(value = "期间类型", required = true)
    private PeriodType periodType;
    /**
     * 订单类型清单
     */
    @NotNull
    @ApiModelProperty(value = "支持的订单类型清单", required = true)
    private OrderCategory[] orderCategories;
    /**
     * 是否冻结
     */
    @ApiModelProperty(value = "是否冻结")
    private Boolean frozen = Boolean.FALSE;
    /**
     * 参考id
     */
    @ApiModelProperty(value = "参考id")
    private String referenceId;
    /**
     * 租户代码
     */
    @ApiModelProperty(value = "租户代码")
    private String tenantCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
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

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public OrderCategory[] getOrderCategories() {
        return orderCategories;
    }

    public void setOrderCategories(OrderCategory[] orderCategories) {
        this.orderCategories = orderCategories;
    }

    public Boolean getFrozen() {
        return frozen;
    }

    public void setFrozen(Boolean frozen) {
        this.frozen = frozen;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }
}