package com.changhong.bems.service.strategy;

import com.changhong.bems.dto.use.BudgetUse;
import com.changhong.bems.entity.Dimension;
import com.changhong.sei.core.dto.ResultData;

/**
 * 实现功能：预算维度匹配策略
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-05-15 09:32
 */
public interface DimensionMatchStrategy {

    /**
     * 获取维度匹配值
     *
     * @param dimension 维度对象
     * @param dimValue  维度值
     * @return 返回匹配值
     */
    ResultData<Object> getMatchValue(BudgetUse budgetUse, Dimension dimension, String dimValue);
}
