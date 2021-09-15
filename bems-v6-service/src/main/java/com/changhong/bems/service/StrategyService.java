package com.changhong.bems.service;

import com.changhong.bems.dao.StrategyDao;
import com.changhong.bems.dto.StrategyCategory;
import com.changhong.bems.entity.Dimension;
import com.changhong.bems.entity.Strategy;
import com.changhong.bems.entity.Subject;
import com.changhong.bems.entity.SubjectItem;
import com.changhong.bems.service.strategy.*;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.limiter.support.lock.SeiLock;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.core.service.bo.OperateResult;
import com.changhong.sei.core.service.bo.OperateResultWithData;
import com.changhong.sei.util.IdGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 预算策略(Strategy)业务逻辑实现类
 *
 * @author sei
 * @since 2021-04-22 11:12:04
 */
@Service
@CacheConfig(cacheNames = StrategyService.CACHE_KEY)
public class StrategyService extends BaseEntityService<Strategy> {
    @Autowired
    private StrategyDao dao;
    @Autowired
    private DimensionService dimensionService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private SubjectItemService subjectItemService;

    public static final String CACHE_KEY = "bems-v6:strategy";

    @Override
    protected BaseEntityDao<Strategy> getDao() {
        return dao;
    }

    /**
     * 主键删除
     *
     * @param id 主键
     * @return 返回操作结果对象
     */
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public OperateResult delete(String id) {
        Strategy entity = findOne(id);
        if (Objects.nonNull(entity)) {
            Dimension dimension = dimensionService.findFirstByProperty(Dimension.FIELD_STRATEGY_ID, id);
            if (Objects.nonNull(dimension)) {
                // 策略已被维度[{0}]使用,禁止删除
                return OperateResult.operationFailure("strategy_00001", dimension.getName());
            }
            Subject subject = subjectService.findFirstByProperty(Subject.FIELD_STRATEGY_ID, id);
            if (Objects.nonNull(subject)) {
                // 策略已被预算主体[{0}]使用,禁止删除
                return OperateResult.operationFailure("strategy_00002", subject.getName());
            }
            SubjectItem item = subjectItemService.findFirstByProperty(SubjectItem.FIELD_STRATEGY_ID, id);
            if (Objects.nonNull(item)) {
                // 策略已被预算科目[{0}]使用,禁止删除
                return OperateResult.operationFailure("strategy_00003", item.getName());
            }
            dao.delete(entity);
            return OperateResult.operationSuccess("core_service_00028");
        } else {
            return OperateResult.operationWarning("core_service_00029");
        }
    }

    /**
     * 数据保存操作
     */
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public OperateResultWithData<Strategy> save(Strategy entity) {
        if (Objects.nonNull(entity) && StringUtils.isBlank(entity.getCode())) {
            entity.setCode(IdGenerator.uuid2());
        }
        return super.save(entity);
    }

    /**
     * 创建数据保存数据之前额外操作回调方法 默认为空逻辑，子类根据需要覆写添加逻辑即可
     *
     * @param entity 待创建数据对象
     */
    @Override
    protected OperateResultWithData<Strategy> preInsert(Strategy entity) {
        OperateResultWithData<Strategy> result = super.preInsert(entity);
        if (result.successful()) {
            Strategy existed = dao.findByProperty(Strategy.FIELD_NAME, entity.getName());
            if (Objects.nonNull(existed)) {
                // 已存在策略
                return OperateResultWithData.operationFailure("strategy_00005", existed.getName());
            }
            existed = dao.findByProperty(Strategy.FIELD_CLASSPATH, entity.getClassPath());
            if (Objects.nonNull(existed)) {
                // 已存在类路径的策略
                return OperateResultWithData.operationFailure("strategy_00006", existed.getClassPath(), existed.getName());
            }
        }
        return result;
    }

    /**
     * 更新数据保存数据之前额外操作回调方法 默认为空逻辑，子类根据需要覆写添加逻辑即可
     *
     * @param entity 待更新数据对象
     */
    @Override
    protected OperateResultWithData<Strategy> preUpdate(Strategy entity) {
        OperateResultWithData<Strategy> result = super.preUpdate(entity);
        if (result.successful()) {
            Strategy existed = dao.findByProperty(Strategy.FIELD_NAME, entity.getName());
            if (Objects.nonNull(existed) && !StringUtils.equals(entity.getId(), existed.getId())) {
                // 已存在策略
                return OperateResultWithData.operationFailure("strategy_00005", existed.getName());
            }
            existed = dao.findByProperty(Strategy.FIELD_CLASSPATH, entity.getClassPath());
            if (Objects.nonNull(existed) && !StringUtils.equals(entity.getId(), existed.getId())) {
                // 已存在类路径的策略
                return OperateResultWithData.operationFailure("strategy_00006", existed.getClassPath(), existed.getName());
            }
        }
        return result;
    }

    /**
     * 检查和初始化数据
     * 当检测到租户下不存在维度数据时,默认初始化预制的维度数据
     */
    @Transactional(rollbackFor = Exception.class)
    @SeiLock(key = "'StrategyService:checkAndInit'")
    public List<Strategy> checkAndInit() {
        List<Strategy> strategies = dao.findAll();
        if (CollectionUtils.isEmpty(strategies)) {
            strategies = new ArrayList<>();
            Strategy strategy;
            strategy = new Strategy();
            strategy.setCode(EqualMatchStrategy.class.getSimpleName());
            strategy.setName("维度值一致性匹配");
            strategy.setCategory(StrategyCategory.DIMENSION);
            strategy.setClassPath(EqualMatchStrategy.class.getName());
            strategy.setRemark("维度值完全一致");
            strategy.setRank(0);
            super.save(strategy);
            strategies.add(strategy);
            strategy = new Strategy();
            strategy.setCode(PeriodMatchStrategy.class.getSimpleName());
            strategy.setName("期间关系匹配");
            strategy.setCategory(StrategyCategory.DIMENSION);
            strategy.setClassPath(PeriodMatchStrategy.class.getName());
            strategy.setRemark("标准期间(年,季,月)的客观包含关系");
            strategy.setRank(1);
            super.save(strategy);
            strategies.add(strategy);
            strategy = new Strategy();
            strategy.setCode(OrgTreeMatchStrategy.class.getSimpleName());
            strategy.setName("组织机构树路径匹配");
            strategy.setCategory(StrategyCategory.DIMENSION);
            strategy.setClassPath(OrgTreeMatchStrategy.class.getName());
            strategy.setRemark("在同一条树分支路径上的节点(向上)匹配");
            strategy.setRank(2);
            super.save(strategy);
            strategies.add(strategy);

            strategy = new Strategy();
            strategy.setCode(LimitExecutionStrategy.class.getSimpleName());
            strategy.setName("强控");
            strategy.setCategory(StrategyCategory.EXECUTION);
            strategy.setClassPath(LimitExecutionStrategy.class.getName());
            strategy.setRemark("预算使用严格控制在余额范围内");
            strategy.setRank(3);
            super.save(strategy);
            strategies.add(strategy);
            strategy = new Strategy();
            strategy.setCode(AnnualTotalExecutionStrategy.class.getSimpleName());
            strategy.setName("年度总额控");
            strategy.setCategory(StrategyCategory.EXECUTION);
            strategy.setClassPath(AnnualTotalExecutionStrategy.class.getName());
            strategy.setRemark("允许月度预算超额,但不能超年度预算总额");
            strategy.setRank(4);
            super.save(strategy);
            strategies.add(strategy);
            strategy = new Strategy();
            strategy.setCode(ExcessExecutionStrategy.class.getSimpleName());
            strategy.setName("弱控");
            strategy.setCategory(StrategyCategory.EXECUTION);
            strategy.setClassPath(ExcessExecutionStrategy.class.getName());
            strategy.setRemark("可超额使用预算,即预算池余额不够时可超额使用");
            strategy.setRank(5);
            super.save(strategy);
            strategies.add(strategy);
        }
        return strategies;
    }

    /**
     * 基于主键查询单一数据对象
     */
    @Override
    @Cacheable(key = "#id")
    public Strategy findOne(String id) {
        return dao.findOne(id);
    }

    /**
     * 基于主键集合查询集合数据对象
     */
    @Override
    @Cacheable(key = "'all'")
    public List<Strategy> findAll() {
        return dao.findAll();
    }

    /**
     * 按分类查询策略
     *
     * @param category 分类
     * @return 策略清单
     */
    @Cacheable(key = "#category.name()")
    public List<Strategy> findByCategory(StrategyCategory category) {
        return dao.findListByProperty(Strategy.FIELD_CATEGORY, category);
    }

    /**
     * 获取预算执行控制策略
     *
     * @param subjectId 预算主体id
     * @param itemCode  预算科目代码
     * @return 预算执行控制策略
     */
    @Cacheable(key = "#subjectId + ':' + #itemCode")
    public ResultData<Strategy> getStrategy(String subjectId, String itemCode) {
        // 预算主体策略
        Strategy strategy = null;
        // 预算主体科目
        SubjectItem subjectItem = subjectItemService.getSubjectItem(subjectId, itemCode);
        if (Objects.nonNull(subjectItem)) {
            if (StringUtils.isNotBlank(subjectItem.getStrategyId())) {
                // 预算主体科目策略
                strategy = dao.findOne(subjectItem.getStrategyId());
            }
        }
        if (Objects.isNull(strategy)) {
            Subject subject = subjectService.findOne(subjectId);
            if (Objects.nonNull(subject)) {
                strategy = dao.findOne(subject.getStrategyId());
            } else {
                // 预算主体[{0}]不存在!
                return ResultData.fail(ContextUtil.getMessage("subject_00003", subjectId));
            }
        }
        if (Objects.isNull(strategy)) {
            // 预算占用时,未找到预算主体[{0}]的预算科目[{1}]
            return ResultData.fail(ContextUtil.getMessage("pool_00010", subjectId, itemCode));
        }
        return ResultData.success(strategy);
    }
}