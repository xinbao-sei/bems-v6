package com.changhong.bems.service.strategy.impl;

import com.changhong.bems.dto.BudgetUse;
import com.changhong.bems.service.strategy.EqualMatchStrategy;
import com.changhong.sei.core.dto.ResultData;

/**
 * 实现功能：一致性维度匹配策略
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-05-15 09:27
 */
public class DefaultEqualMatchStrategy extends BaseMatchStrategy implements EqualMatchStrategy {
    /**
     * 获取维度匹配值
     *
     * @param dimValue 维度值
     * @return 返回匹配值
     */
    @Override
    public ResultData<Object> getMatchValue(BudgetUse budgetUse, String dimValue) {
        return ResultData.success(dimValue);
    }
}
