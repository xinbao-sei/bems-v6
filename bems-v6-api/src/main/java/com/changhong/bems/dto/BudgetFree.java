package com.changhong.bems.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 实现功能：预算释放
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-05-14 16:37
 */
@ApiModel(description = "预算释放DTO")
public class BudgetFree implements Serializable {
    private static final long serialVersionUID = 176718134255627368L;

    /**
     * 业务事件
     */
    @NotBlank
    @ApiModelProperty(value = "业务事件")
    private String eventCode;
    /**
     * 业务id
     */
    @NotBlank
    @ApiModelProperty(value = "业务id")
    private String bizId;
    /**
     * 占用金额
     */
    @ApiModelProperty(value = "占用金额")
    private double amount = 0;

    public BudgetFree() {
    }

    public BudgetFree(String eventCode, String bizId) {
        this.eventCode = eventCode;
        this.bizId = bizId;
    }

    public String getEventCode() {
        return eventCode;
    }

    public BudgetFree setEventCode(String eventCode) {
        this.eventCode = eventCode;
        return this;
    }

    public String getBizId() {
        return bizId;
    }

    public BudgetFree setBizId(String bizId) {
        this.bizId = bizId;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public BudgetFree setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BudgetFree.class.getSimpleName() + "[", "]")
                .add("eventCode='" + eventCode + "'")
                .add("bizId='" + bizId + "'")
                .add("amount=" + amount)
                .toString();
    }
}