package com.changhong.bems.service;

import com.changhong.bems.commons.Constants;
import com.changhong.bems.dto.*;
import com.changhong.bems.entity.*;
import com.changhong.bems.service.client.OrganizationManager;
import com.changhong.bems.service.strategy.BudgetExecutionStrategy;
import com.changhong.bems.service.strategy.DimensionMatchStrategy;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.SearchFilter;
import com.changhong.sei.core.util.JsonUtils;
import com.changhong.sei.exception.ServiceException;
import com.changhong.sei.util.ArithUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-05-14 17:41
 */
@Service
public class BudgetService {
    private static final Logger LOG = LoggerFactory.getLogger(BudgetService.class);

    @Autowired
    private PoolService poolService;
    @Autowired
    private ExecutionRecordService executionRecordService;
    @Autowired
    private DimensionService dimensionService;
    @Autowired
    private StrategyService strategyService;
    @Autowired(required = false)
    private OrganizationManager organizationManager;

    /**
     * 使用预算
     * 包含占用和释放
     * 1.处理释放数据
     * 2.检查占用是否需要释放处理
     * 2.1.若需要释放处理,则进行是否处理
     * 2.2.若不需要释放处理,则进行占用处理
     * 3.占用处理
     *
     * @param request 使用预算请求
     * @return 使用预算结果(只对占用预算结果进行返回, 释放不返回结果)
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<List<BudgetResponse>> use(BudgetRequest request) {
        if (LOG.isInfoEnabled()) {
            LOG.info("预算占用: {}", JsonUtils.toJson(request));
        }
        ResultData<List<BudgetResponse>> result = ResultData.success();
        try {
            // 预算释放数据
            List<BudgetFree> freeList = request.getFreeList();
            if (CollectionUtils.isNotEmpty(freeList)) {
                for (BudgetFree free : freeList) {
                    if (0 == free.getAmount()) {
                        // 按原先占用记录释放全部金额
                        this.freeBudget(free.getEventCode(), free.getBizId());
                    } else {
                        // 按原先占用记录释放指定金额
                        this.freeBudget(free.getEventCode(), free.getBizId(), free.getAmount());
                    }
                }
            }

            // 预算占用数据
            List<BudgetUse> useList = request.getUseList();
            if (CollectionUtils.isNotEmpty(useList)) {
                List<BudgetResponse> responses = new ArrayList<>();
                boolean success = true;
                ResultData<BudgetResponse> resultData;
                for (BudgetUse budgetUse : useList) {
                    resultData = this.useBudget(budgetUse);
                    success = resultData.successful();
                    if (success) {
                        responses.add(resultData.getData());
                    } else {
                        result = ResultData.fail(resultData.getMessage());
                        // 回滚事务
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        break;
                    }
                }
                if (success) {
                    result = ResultData.success(responses);
                }
            }
        } catch (Exception e) {
            // 回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error("预算占用异常", e);
            result = ResultData.fail("预算占用异常: " + ExceptionUtils.getRootCauseMessage(e));
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("预算占用结果: {}", JsonUtils.toJson(result));
        }
        return result;
    }

    /**
     * 占用预算
     * 编辑情况:释放原先占用,再按新数据占用
     *
     * @param useBudget 占用数据
     * @return 返回占用结果
     */
    private ResultData<BudgetResponse> useBudget(BudgetUse useBudget) {
        // TODO 检查参数合法性
        // 事件代码
        String eventCode = useBudget.getEventCode();
        // 业务id
        String bizId = useBudget.getBizId();

        // 释放原先占用
        this.freeBudget(eventCode, bizId);

        // 再按新数据占用
        /*
        1.按公司代码查询预算主体清单;
        2.维度匹配,匹配规则:
        a.获取所有维度及对应维度策略
        b.获取当前请求维度
        c.获取当前请求维度策略,若是一致性匹配的加入预算池查询条件;非一致性匹配的加入后续策略处理
        d.按预算主体,占用时间范围,一致性维度作为条件查询满足条件的预算池
        3.找出最优预算池
         */
        // 预算占用日期
        LocalDate useDate = LocalDate.parse(useBudget.getDate(), DateTimeFormatter.ISO_DATE);
        // 按占用数据获取维度
        Map<String, SearchFilter> otherDimensions = this.getOtherDimensionFilters(useBudget);
        // 组装所使用到的维度清单 -> 生成维度组合
        Set<String> codes = new HashSet<>(otherDimensions.keySet());
        codes.add(Constants.DIMENSION_CODE_ITEM);
        codes.add(Constants.DIMENSION_CODE_PERIOD);
        // 使用到的维度,按asci码排序,逗号(,)分隔
        StringJoiner joiner = new StringJoiner(",");
        codes.stream().sorted().forEach(joiner::add);
        final String attribute = joiner.toString();
        // 查询满足条件的预算池(非必要维度)
        final Collection<SearchFilter> otherDimFilters = otherDimensions.values();
        // 按预算占用参数获取预算池大致范围
        final List<PoolAttributeView> poolAttributes = poolService.getBudgetPools(attribute, useDate, useBudget, otherDimFilters);
        if (CollectionUtils.isEmpty(poolAttributes)) {
            return ResultData.fail(ContextUtil.getMessage("pool_00009", JsonUtils.toJson(useBudget)));
        }

        // 获取最优预算池
        ResultData<PoolAttributeView> resultData = this.getOptimalPool(attribute, useBudget, poolAttributes);
        if (resultData.failed()) {
            return ResultData.fail(resultData.getMessage());
        }
        PoolAttributeView pool = resultData.getData();
        Strategy strategy = strategyService.findOne(pool.getStrategyId());
        if (Objects.isNull(strategy)) {
            // 预算占用时,执行策略[{0}]不存在!
            return ResultData.fail(ContextUtil.getMessage("pool_00014", pool.getStrategyName()));
        }
        try {
            Class<?> clazz = Class.forName(strategy.getClassPath());
            if (BudgetExecutionStrategy.class.isAssignableFrom(clazz)) {
                // 策略实例
                BudgetExecutionStrategy executionStrategy = (BudgetExecutionStrategy) ContextUtil.getBean(clazz);
                return executionStrategy.execution(pool, useBudget, otherDimFilters);
            } else {
                return ResultData.fail("预算执行策略[" + strategy.getName() + "]配置错误.");
            }
        } catch (ClassNotFoundException | BeansException e) {
            return ResultData.fail("预算执行策略执行异常: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    /**
     * 释放全部占用金额
     *
     * @param eventCode 业务事件代码
     * @param bizId     业务id
     */
    private void freeBudget(String eventCode, String bizId) {
        // 检查占用是否需要释放
        List<ExecutionRecord> records = executionRecordService.getUseRecords(eventCode, bizId);
        if (CollectionUtils.isNotEmpty(records)) {
            ExecutionRecord newRecord;
            for (ExecutionRecord record : records) {
                // 为保证占用幂等,避免重复释放,更新记录已释放标记
                executionRecordService.updateFreed(record.getId());

                newRecord = record.clone();
                newRecord.setOperation(OperationType.FREED);
                newRecord.setId(null);
                newRecord.setOpUserAccount(null);
                newRecord.setOpUserName(null);
                newRecord.setBizRemark("释放: " + newRecord.getBizRemark());
                // 释放记录
                poolService.recordLog(newRecord);
            }
        }
    }

    /**
     * 释放指定金额
     *
     * @param eventCode 业务事件代码
     * @param bizId     业务id
     * @param amount    释放金额
     */
    private void freeBudget(String eventCode, String bizId, double amount) {
        // 检查占用是否需要释放
        List<ExecutionRecord> records = executionRecordService.getUseRecords(eventCode, bizId);
        if (CollectionUtils.isNotEmpty(records)) {
            ExecutionRecord newRecord;
            // 剩余释放金额
            double balance = amount;
            for (ExecutionRecord record : records) {
                if (balance <= 0) {
                    continue;
                }
                // 为保证占用幂等,避免重复释放,更新记录已释放标记
                executionRecordService.updateFreed(record.getId());

                newRecord = record.clone();
                if (record.getAmount() > balance) {
                    // 释放当前记录部分金额
                    newRecord.setAmount(balance);
                    balance = 0;
                } else {
                    // 释放当前记录全部金额
                    balance = ArithUtils.sub(balance, record.getAmount());
                }
                newRecord.setId(null);
                newRecord.setOperation(OperationType.FREED);
                newRecord.setOpUserAccount(null);
                newRecord.setOpUserName(null);
                newRecord.setBizRemark("释放: " + newRecord.getBizRemark());
                // 释放记录
                poolService.recordLog(newRecord);
            }
        }
    }

    /**
     * 按占用参数获取其他维度条件
     * 期间和科目为预制默认维度匹配,不在本范围中
     */
    private Map<String, SearchFilter> getOtherDimensionFilters(BudgetUse use) {
        // 占用的维度代码
        Map<String, SearchFilter> dimFilterMap = new HashMap<>();
        // 组织机构
        String org = use.getOrg();
        if (Objects.nonNull(org)) {
            org = org.trim();
            if (StringUtils.isNotBlank(org) && !StringUtils.equalsIgnoreCase(Constants.NONE, org)) {
                dimFilterMap.put(DimensionAttribute.FIELD_ORG, this.doDimensionStrategy(use, DimensionAttribute.FIELD_ORG, org));
            }
        }
        // 预算项目
        String project = use.getProject();
        if (Objects.nonNull(project)) {
            project = project.trim();
            if (StringUtils.isNotBlank(project) && !StringUtils.equalsIgnoreCase(Constants.NONE, project)) {
                dimFilterMap.put(DimensionAttribute.FIELD_PROJECT, this.doDimensionStrategy(use, DimensionAttribute.FIELD_PROJECT, project));
            }
        }
        // 自定义1
        String udf1 = use.getUdf1();
        if (Objects.nonNull(udf1)) {
            udf1 = udf1.trim();
            if (StringUtils.isNotBlank(udf1) && !StringUtils.equalsIgnoreCase(Constants.NONE, udf1)) {
                dimFilterMap.put(DimensionAttribute.FIELD_UDF1, this.doDimensionStrategy(use, DimensionAttribute.FIELD_UDF1, udf1));
            }
        }
        // 自定义2
        String udf2 = use.getUdf2();
        if (Objects.nonNull(udf2)) {
            udf2 = udf2.trim();
            if (StringUtils.isNotBlank(udf2) && !StringUtils.equalsIgnoreCase(Constants.NONE, udf2)) {
                dimFilterMap.put(DimensionAttribute.FIELD_UDF2, this.doDimensionStrategy(use, DimensionAttribute.FIELD_UDF2, udf2));
            }
        }
        // 自定义3
        String udf3 = use.getUdf3();
        if (Objects.nonNull(udf3)) {
            udf3 = udf3.trim();
            if (StringUtils.isNotBlank(udf3) && !StringUtils.equalsIgnoreCase(Constants.NONE, udf3)) {
                dimFilterMap.put(DimensionAttribute.FIELD_UDF3, this.doDimensionStrategy(use, DimensionAttribute.FIELD_UDF3, udf3));
            }
        }
        // 自定义4
        String udf4 = use.getUdf4();
        if (Objects.nonNull(udf4)) {
            udf4 = udf4.trim();
            if (StringUtils.isNotBlank(udf4) && !StringUtils.equalsIgnoreCase(Constants.NONE, udf4)) {
                dimFilterMap.put(DimensionAttribute.FIELD_UDF4, this.doDimensionStrategy(use, DimensionAttribute.FIELD_UDF4, udf4));
            }
        }
        // 自定义5
        String udf5 = use.getUdf5();
        if (Objects.nonNull(udf5)) {
            udf5 = udf5.trim();
            if (StringUtils.isNotBlank(udf5) && !StringUtils.equalsIgnoreCase(Constants.NONE, udf5)) {
                dimFilterMap.put(DimensionAttribute.FIELD_UDF5, this.doDimensionStrategy(use, DimensionAttribute.FIELD_UDF5, udf5));
            }
        }
        return dimFilterMap;
    }

    /**
     * 按预算维度策略获取过滤条件
     *
     * @param dimCode  维度代码
     * @param dimValue 维度值
     * @return 返回维度策略获取过滤条件
     * @throws ServiceException 异常
     */
    private SearchFilter doDimensionStrategy(BudgetUse budgetUse, String dimCode, String dimValue) {
        Dimension dimension = dimensionService.findByCode(dimCode);
        if (Objects.isNull(dimension)) {
            // 维度[{0}]不存在
            throw new ServiceException(ContextUtil.getMessage("dimension_00002", dimCode));
        }
        Strategy strategy = strategyService.findOne(dimension.getStrategyId());
        if (Objects.isNull(strategy)) {
            // 策略[{0}]不存在!
            throw new ServiceException(ContextUtil.getMessage("strategy_00004", dimension.getStrategyId()));
        }

        ServiceException exception;
        String className = strategy.getClassPath();
        try {
            Class<?> clazz = Class.forName(className);
            if (DimensionMatchStrategy.class.isAssignableFrom(clazz)) {
                // 策略实例
                DimensionMatchStrategy matchStrategy = (DimensionMatchStrategy) ContextUtil.getBean(clazz);
                // 策略结果
                ResultData<Object> resultData = matchStrategy.getMatchValue(budgetUse, dimension, dimValue);
                if (resultData.successful()) {
                    SearchFilter filter;
                    Object obj = resultData.getData();
                    if (Objects.nonNull(obj)) {
                        if (obj instanceof Collection) {
                            filter = new SearchFilter(dimCode, obj, SearchFilter.Operator.IN);
                        } else if (obj.getClass().isArray()) {
                            filter = new SearchFilter(dimCode, obj, SearchFilter.Operator.IN);
                        } else {
                            filter = new SearchFilter(dimCode, obj);
                        }
                        return filter;
                    } else {
                        exception = new ServiceException("预算维度[" + dimension.getName() + "]策略条件不能返回为Null.");
                    }
                } else {
                    exception = new ServiceException("预算维度[" + dimension.getName() + "]策略条件错误: " + resultData.getMessage());
                }
            } else {
                exception = new ServiceException("预算维度[" + dimension.getName() + "]策略配置错误.");
            }
        } catch (ClassNotFoundException | BeansException e) {
            exception = new ServiceException("按预算维度策略获取过滤条件异常", e);
        }
        throw exception;
    }

    /**
     * 按使用优先级,获取最优预算池
     * 当存在组织时,优先按组织树路径向上查找排序
     * 再按期间类型枚举下标排序
     *
     * @param attribute      预算维度属性
     * @param useBudget      预算占用数据
     * @param poolAttributes 占用预算池范围清单
     * @return 预算池使用优先级
     */
    private ResultData<PoolAttributeView> getOptimalPool(String attribute, BudgetUse useBudget, List<PoolAttributeView> poolAttributes) {
        List<PoolAttributeView> pools = null;
        // 组织id
        String orgId = useBudget.getOrg();
        // 检查是否包含组织维度
        if (attribute.contains(Constants.DIMENSION_CODE_ORG)) {
            if (Objects.isNull(organizationManager)) {
                return ResultData.fail("对组织维度检查,OrganizationManager不能为空.");
            }
            // 按id进行映射方便后续使用
            Map<String, OrganizationDto> orgMap = null;
            if (Objects.nonNull(orgId)) {
                orgId = orgId.trim();
                if (StringUtils.isNotBlank(orgId) && !StringUtils.equalsIgnoreCase(Constants.NONE, orgId)) {
                    // 获取指定节点的所有父节点(含自己)
                    ResultData<List<OrganizationDto>> resultData = organizationManager.getParentNodes(orgId, Boolean.TRUE);
                    if (resultData.successful()) {
                        List<OrganizationDto> orgList = resultData.getData();
                        if (CollectionUtils.isNotEmpty(orgList)) {
                            // 组织id映射
                            orgMap = orgList.stream().collect(Collectors.toMap(OrganizationDto::getId, o -> o));
                            orgList.clear();
                        }
                    } else {
                        return ResultData.fail(resultData.getMessage());
                    }
                }
            }
            if (Objects.isNull(orgMap)) {
                // 预算占用时,组织维度值不能为空!
                return ResultData.fail(ContextUtil.getMessage("pool_00012"));
            }

            /*
            组织机构向上查找规则:
            1.按组织机构树路径,从预算占用的节点开始,向上依次查找
            2.当按组织节点找到存在的预算池,不管余额是否满足,都将停止向上查找
             */
            String parentId = orgId;
            OrganizationDto org = orgMap.get(parentId);
            while (Objects.nonNull(org)) {
                String oId = org.getId();
                // 按组织id匹配预算池
                pools = poolAttributes.stream().filter(p -> StringUtils.equals(oId, p.getOrg())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(pools)) {
                    break;
                } else {
                    // 没有可用的预算池,继续查找上级组织的预算池
                    parentId = org.getParentId();
                    if (StringUtils.isNotBlank(parentId)) {
                        org = orgMap.get(parentId);
                    } else {
                        org = null;
                    }
                }
            }
        } else {
            pools = poolAttributes;
        }

        if (CollectionUtils.isEmpty(pools)) {
            // 预算占用时,未找到满足条件[{0}]的预算池!
            return ResultData.fail(ContextUtil.getMessage("pool_00009", useBudget));
        }
        // 按期间类型下标进行排序: 下标值越大优先级越高
        pools = pools.stream().sorted(Comparator.comparingInt(p -> p.getPeriodType().ordinal())).collect(Collectors.toList());
        return ResultData.success(pools.get(0));
    }
}
