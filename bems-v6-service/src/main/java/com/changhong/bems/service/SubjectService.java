package com.changhong.bems.service;

import com.changhong.bems.commons.Constants;
import com.changhong.bems.dao.CategoryDao;
import com.changhong.bems.dao.PeriodDao;
import com.changhong.bems.dao.SubjectDao;
import com.changhong.bems.dao.SubjectOrganizationDao;
import com.changhong.bems.dto.*;
import com.changhong.bems.entity.Category;
import com.changhong.bems.entity.Period;
import com.changhong.bems.entity.Subject;
import com.changhong.bems.entity.SubjectOrganization;
import com.changhong.bems.service.client.CorporationManager;
import com.changhong.bems.service.client.CurrencyManager;
import com.changhong.bems.service.client.OrganizationManager;
import com.changhong.sei.basic.sdk.UserAuthorizeManager;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.dto.serach.SearchFilter;
import com.changhong.sei.core.log.LogUtil;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.core.service.DataAuthEntityService;
import com.changhong.sei.core.service.bo.OperateResult;
import com.changhong.sei.core.service.bo.OperateResultWithData;
import com.changhong.sei.util.IdGenerator;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 预算主体(Subject)业务逻辑实现类
 *
 * @author sei
 * @since 2021-04-22 12:54:25
 */
@Service
public class SubjectService extends BaseEntityService<Subject> implements DataAuthEntityService {
    @Autowired
    private SubjectDao dao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private PeriodDao periodDao;
    @Autowired
    private SubjectOrganizationDao subjectOrganizationDao;
    @Autowired
    private StrategyService strategyService;
    @Autowired
    private StrategyPeriodService strategyPeriodService;
    @Autowired
    private UserAuthorizeManager userAuthorizeManager;
    @Autowired
    private CurrencyManager currencyManager;
    @Autowired
    private CorporationManager corporationManager;
    @Autowired(required = false)
    private OrganizationManager organizationManager;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected BaseEntityDao<Subject> getDao() {
        return dao;
    }

    /**
     * 从平台基础应用获取一般用户有权限的数据实体Id清单
     * 对于数据权限对象的业务实体，需要override，使用BASIC提供的通用工具来获取
     *
     * @param entityClassName 权限对象实体类型
     * @param featureCode     功能项代码
     * @param userId          用户Id
     * @return 数据实体Id清单
     */
    @Override
    public List<String> getNormalUserAuthorizedEntitiesFromBasic(String entityClassName, String featureCode, String userId) {
        return userAuthorizeManager.getNormalUserAuthorizedEntities(entityClassName, featureCode, userId);
    }

    /**
     * 获取币种数据
     *
     * @return 查询结果
     */
    public ResultData<List<CurrencyDto>> findCurrencies() {
        return currencyManager.findAllUnfrozen();
    }

    /**
     * 获取当前用户有权限的公司
     *
     * @return 当前用户有权限的公司
     */
    public ResultData<List<CorporationDto>> findUserAuthorizedCorporations() {
        return corporationManager.findUserAuthorizedCorporations();
    }

    /**
     * 批量维护时公司列表
     * 用户有权限的公司,且未配置相应类型主体的公司
     *
     * @return 当前用户有权限的公司
     */
    public ResultData<List<CorporationDto>> findCorporations(Classification classification) {
        ResultData<List<CorporationDto>> resultData = this.findUserAuthorizedCorporations();
        if (resultData.successful()) {
            // 用户当前有权限的公司清单
            List<CorporationDto> corporations = resultData.getData();

            List<Subject> subjectList = dao.findListByProperty(Subject.FIELD_CLASSIFICATION, classification);
            if (CollectionUtils.isNotEmpty(subjectList)) {
                // 已配置主体的公司代码清单
                Set<String> subjectCorpSet = subjectList.stream().map(Subject::getCorporationCode).collect(Collectors.toSet());
                // 排除已配置主体的公司
                List<CorporationDto> corpList = corporations.stream().filter(corp -> !subjectCorpSet.contains(corp.getCode())).collect(Collectors.toList());
                return ResultData.success(corpList);
            }
        }
        return resultData;
    }

    /**
     * 批量创建预算主体
     *
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> batchCreate(Set<String> corpCodes, Classification classification, String strategyId) {
        Subject subject;
        ResultData<List<CorporationDto>> resultData = this.findUserAuthorizedCorporations();
        if (resultData.successful()) {
            CorporationDto corporationDto;
            List<CorporationDto> corporations = resultData.getData();
            Map<String, CorporationDto> corpMap = corporations.stream().collect(Collectors.toMap(CorporationDto::getCode, corp -> corp));
            for (String corpCode : corpCodes) {
                corporationDto = corpMap.get(corpCode);
                if (Objects.isNull(corporationDto)) {
                    continue;
                }
                subject = new Subject();
                subject.setCorporationCode(corpCode);
                if (Classification.DEPARTMENT == classification) {
                    subject.setCorporationName(corporationDto.getName() + ContextUtil.getMessage("subject_org"));
                } else if (Classification.PROJECT == classification) {
                    subject.setCorporationName(corporationDto.getName() + ContextUtil.getMessage("subject_project"));
                } else if (Classification.COST_CENTER == classification) {
                    subject.setCorporationName(corporationDto.getName() + ContextUtil.getMessage("subject_cost"));
                } else {
                    subject.setCorporationName(corporationDto.getName());
                }
                subject.setCurrencyCode(corporationDto.getBaseCurrencyCode());
                subject.setCurrencyName(corporationDto.getBaseCurrencyName());
                subject.setName(subject.getCorporationName());
                subject.setClassification(classification);
                subject.setStrategyId(strategyId);
                subject.setStrategyName(strategyService.getNameByCode(strategyId));
                try {
                    OperateResultWithData<Subject> result = this.save(subject);
                    if (result.notSuccessful()) {
                        LogUtil.error(result.getMessage());
                    }
                } catch (Exception e) {
                    LogUtil.error("公司[" + corpCode + "]在批量创建预算主体时异常", e.getMessage());
                }
            }
            return ResultData.success();
        } else {
            return ResultData.fail(resultData.getMessage());
        }
    }

    /**
     * 按公司代码获取组织机构树(不包含冻结)
     *
     * @param corpCode 公司代码
     * @return 组织机构树清单
     */
    public ResultData<OrganizationDto> findOrgTree(String corpCode) {
        ResultData<OrganizationDto> resultData;
        if (Objects.nonNull(organizationManager)) {
            ResultData<CorporationDto> corpResultData = corporationManager.findByCode(corpCode);
            if (corpResultData.successful()) {
                CorporationDto corporation = corpResultData.getData();
                if (Objects.nonNull(corporation)) {
                    if (StringUtils.isNotBlank(corporation.getOrganizationId())) {
                        resultData = organizationManager.getTree4Unfrozen(corporation.getOrganizationId());
                    } else {
                        // 公司[{0}]对应的组织机构未配置，请检查！
                        resultData = ResultData.fail(ContextUtil.getMessage("subject_00013", corpCode));
                    }
                } else {
                    // 公司[{0}]不存在，请检查！
                    resultData = ResultData.fail(ContextUtil.getMessage("subject_00012", corpCode));
                }
            } else {
                resultData = ResultData.fail(corpResultData.getMessage());
            }
        } else {
            resultData = ResultData.fail(ContextUtil.getMessage("pool_00030"));
        }
        return resultData;
    }

    /**
     * 通过预算主体获取组织机构树(不包含冻结)
     *
     * @return 组织机构树
     */
    public ResultData<List<OrganizationDto>> getOrgTree(String subjectId) {
        ResultData<List<OrganizationDto>> resultData = this.getOrgChildren(subjectId);
        if (resultData.successful()) {
            // 构造成树
            return ResultData.success(buildTree(resultData.getData()));
        } else {
            return ResultData.fail(resultData.getMessage());
        }
    }

    /**
     * 通过预算主体获取组织机构清单(不包含冻结)
     *
     * @return 组织机构子节点清单
     */
    public ResultData<List<OrganizationDto>> getOrgChildren(String subjectId) {
        Subject subject = dao.findOne(subjectId);
        if (Objects.isNull(subject)) {
            // 未找到预算主体
            return ResultData.fail(ContextUtil.getMessage("subject_00003"));
        }
        if (Objects.nonNull(organizationManager)) {
            // 通过组织机构id获取组织机构清单
            ResultData<List<OrganizationDto>> resultData;
            if (Objects.equals(Classification.DEPARTMENT, subject.getClassification())) {
                // 获取分配的组织机构清单
                List<SubjectOrganization> subjectOrganizations = this.getSubjectOrganizations(subjectId);
                if (CollectionUtils.isNotEmpty(subjectOrganizations)) {
                    Set<String> nodeIds = subjectOrganizations.stream().map(SubjectOrganization::getOrgId).collect(Collectors.toSet());
                    resultData = organizationManager.getChildrenNodes4UnfrozenByIds(nodeIds);
                } else {
                    // 预算主体[{0}]未维护适用组织范围!
                    resultData = ResultData.fail(ContextUtil.getMessage("subject_00007", subject.getName()));
                }
            } else if (Objects.equals(Classification.PROJECT, subject.getClassification())) {
                ResultData<CorporationDto> corpResultData = corporationManager.findByCode(subject.getCorporationCode());
                if (corpResultData.successful()) {
                    CorporationDto corporation = corpResultData.getData();
                    if (Objects.nonNull(corporation)) {
                        if (StringUtils.isNotBlank(corporation.getOrganizationId())) {
                            resultData = organizationManager.getChildrenNodes4Unfrozen(corporation.getOrganizationId());
                        } else {
                            // 预算主体[{0}]配置的公司[{0}]对应的组织机构未配置，请检查！
                            resultData = ResultData.fail(ContextUtil.getMessage("subject_00009", subject.getName(), subject.getCorporationCode()));
                        }
                    } else {
                        // 预算主体[{0}]配置的公司[{0}]不存在，请检查！
                        resultData = ResultData.fail(ContextUtil.getMessage("subject_00008", subject.getName(), subject.getCorporationCode()));
                    }
                } else {
                    resultData = ResultData.fail(corpResultData.getMessage());
                }
            } else if (Objects.equals(Classification.COST_CENTER, subject.getClassification())) {
                // TODO 成本中心接口
                resultData = ResultData.fail(ContextUtil.getMessage("开发中"));
            } else {
                // 不支持的预算分类
                resultData = ResultData.fail(ContextUtil.getMessage("subject_00010"));
            }
            return resultData;
        } else {
            return ResultData.fail(ContextUtil.getMessage("pool_00030"));
        }
    }

    /**
     * 按组织级主体id获取分配的组织机构
     *
     * @param subjectId 组织级主体id
     * @return 分配的组织机构清单
     */
    public List<SubjectOrganization> getSubjectOrganizations(String subjectId) {
        return subjectOrganizationDao.findListByProperty(SubjectOrganization.FIELD_SUBJECT_ID, subjectId);
    }

    /**
     * 更新一个预算主体冻结状态
     *
     * @param id id
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> updateFrozen(String id, boolean state) {
        Subject subject = dao.findOne(id);
        if (Objects.isNull(subject)) {
            // 业务领域不存在.
            return ResultData.fail(ContextUtil.getMessage("subject_00003", id));
        }
        subject.setFrozen(state);
        dao.save(subject);
        return ResultData.success();
    }

    /**
     * 数据保存操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperateResultWithData<Subject> save(Subject entity) {
        boolean isNew = entity.isNew();

        Subject existed = dao.findByProperty(Subject.FIELD_NAME, entity.getName());
        if (Objects.nonNull(existed) && !StringUtils.equals(entity.getId(), existed.getId())) {
            // 已存在预算主体
            return OperateResultWithData.operationFailure("subject_00005", existed.getName());
        }

        if (StringUtils.isBlank(entity.getCode())) {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            StringBuilder firstPinyin = new StringBuilder();
            char[] hanyuArr = entity.getName().trim().toCharArray();
            try {
                for (char c : hanyuArr) {
                    if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                        String[] pys = PinyinHelper.toHanyuPinyinStringArray(c, format);
                        firstPinyin.append(pys[0].charAt(0));
                    } else {
                        firstPinyin.append(c);
                    }
                }
            } catch (Exception e) {
                LogUtil.error("拼音转换异常", e);
                firstPinyin = new StringBuilder("" + IdGenerator.nextId());
            }
            entity.setCode(firstPinyin.toString());
        }
        if (Objects.equals(Classification.PROJECT, entity.getClassification())) {
            // 检查同一公司下有且只有一个项目级主体
            Search search = Search.createSearch();
            search.addFilter(new SearchFilter(Subject.FIELD_CORP_CODE, entity.getCorporationCode()));
            search.addFilter(new SearchFilter(Subject.FIELD_CLASSIFICATION, Classification.PROJECT));
            existed = dao.findFirstByFilters(search);
            if (Objects.nonNull(existed) && !StringUtils.equals(entity.getId(), existed.getId())) {
                // 公司[{0}]下已存在一个项目级主体[{1}].
                return OperateResultWithData.operationFailure("subject_00011", entity.getCorporationCode(), existed.getName());
            }
        } else if (Objects.equals(Classification.COST_CENTER, entity.getClassification())) {
            // 检查同一公司下有且只有一个成本中心级主体
            Search search = Search.createSearch();
            search.addFilter(new SearchFilter(Subject.FIELD_CORP_CODE, entity.getCorporationCode()));
            search.addFilter(new SearchFilter(Subject.FIELD_CLASSIFICATION, Classification.COST_CENTER));
            existed = dao.findFirstByFilters(search);
            if (Objects.nonNull(existed) && !StringUtils.equals(entity.getId(), existed.getId())) {
                // 公司[{0}]下已存在一个成本中心级主体[{1}].
                return OperateResultWithData.operationFailure("subject_00014", entity.getCorporationCode(), existed.getName());
            }
        } else if (Objects.equals(Classification.DEPARTMENT, entity.getClassification())) {
            // 若是组织级预算的部门级预算,则需要检查是否维护组织机构
            if (entity.getIsDepartment()) {
                if (CollectionUtils.isEmpty(entity.getOrgList())) {
                    // 组织级预算主体需维护适用组织范围
                    return OperateResultWithData.operationFailure("subject_00006");
                }
            } else {
                ResultData<CorporationDto> resultData = corporationManager.findByCode(entity.getCorporationCode());
                if (resultData.failed()) {
                    return OperateResultWithData.operationFailure(resultData.getMessage());
                } else {
                    CorporationDto corporationDto = resultData.getData();
                    if (StringUtils.isNotBlank(corporationDto.getOrganizationId())) {
                        Set<OrganizationDto> orgs = new HashSet<>();
                        OrganizationDto org = new OrganizationDto();
                        org.setId(corporationDto.getOrganizationId());
                        orgs.add(org);
                        entity.setOrgList(orgs);
                    } else {
                        // 公司[{0}]未关联组织机构.
                        return OperateResultWithData.operationFailure("subject_00016", corporationDto.getName());
                    }
                }
            }
        }
        // 持久化
        dao.save(entity);

        if (Boolean.TRUE == isNew) {
            // 新增后处理.初始化预算主体科目策略
            strategyPeriodService.initStrategyPeriod(entity.getId());

            // 保存组织级主体关联的组织机构
            if (Objects.equals(Classification.DEPARTMENT, entity.getClassification())) {
                List<OrganizationDto> orgDtoList;
                Set<String> orgIds = entity.getOrgList().stream().map(OrganizationDto::getId).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(orgIds)) {
                    List<SubjectOrganization> soList = subjectOrganizationDao.findByFilter(new SearchFilter(SubjectOrganization.FIELD_ORG_ID, orgIds, SearchFilter.Operator.IN));
                    if (CollectionUtils.isNotEmpty(soList)) {
                        // 回滚事务
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        // 新增主体
                        SubjectOrganization so = soList.get(0);
                        Subject subject = dao.findOne(so.getSubjectId());
                        if (Objects.nonNull(subject)) {
                            // 组织机构[{0}]已在预算主体[{1}]中.
                            return OperateResultWithData.operationFailure("subject_00015", so.getOrgName(), subject.getName());
                        } else {
                            return OperateResultWithData.operationFailure("subject_00015", so.getOrgName(), so.getSubjectId());
                        }
                    }

                    ResultData<List<OrganizationDto>> orgResult = organizationManager.findOrganizationByIds(orgIds);
                    if (orgResult.successful()) {
                        orgDtoList = orgResult.getData();
                        if (CollectionUtils.isNotEmpty(orgDtoList)) {
                            SubjectOrganization org;
                            soList = new ArrayList<>();
                            for (OrganizationDto orgDto : orgDtoList) {
                                org = new SubjectOrganization();
                                org.setSubjectId(entity.getId());
                                org.setOrgId(orgDto.getId());
                                org.setOrgCode(orgDto.getCode());
                                org.setOrgName(orgDto.getName());
                                org.setOrgNamePath(orgDto.getNamePath());
                                org.setTenantCode(entity.getTenantCode());
                                soList.add(org);
                            }
                            subjectOrganizationDao.save(soList);
                        }
                    }
                }
                //entity = this.findOne(entity.getId());
            }
        } else {
            // 编辑后处理
            // 清除策略缓存
            this.cleanStrategyCache(entity.getId(), null);
        }
        return OperateResultWithData.operationSuccessWithData(entity);
    }

    /**
     * 主键删除
     *
     * @param id 主键
     * @return 返回操作结果对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OperateResult delete(String id) {
        Subject subject = dao.findOne(id);
        if (Objects.isNull(subject)) {
            // 预算主体[{0}]不存在!
            return OperateResult.operationFailure("subject_00003", id);
        }
        Period period = periodDao.findFirstByProperty(Period.FIELD_SUBJECT_ID, id);
        if (Objects.nonNull(period)) {
            // 已被预算期间[{0}]使用,禁止删除!
            return OperateResult.operationFailure("subject_00002", period.getName());
        }
        Category category = categoryDao.findFirstByProperty(Category.FIELD_SUBJECT_ID, id);
        if (Objects.nonNull(category)) {
            // 已被预算类型[{0}]使用,禁止删除!
            return OperateResult.operationFailure("subject_00001", category.getName());
        }

        if (Classification.DEPARTMENT == subject.getClassification()) {
            // 组织级预算主体,需先移除关联的组织
            List<SubjectOrganization> soList = this.getSubjectOrganizations(id);
            if (CollectionUtils.isNotEmpty(soList)) {
                subjectOrganizationDao.deleteAll(soList);
            }
        }

        // 删除主体
        dao.delete(subject);
        return OperateResult.operationSuccess();
    }

    /**
     * 基于主键查询单一数据对象
     *
     * @param id 主体id
     */
    @Override
    public Subject findOne(String id) {
        Subject subject = dao.findOne(id);
        if (Objects.nonNull(subject)) {
            if (Objects.equals(Classification.DEPARTMENT, subject.getClassification())) {
                List<SubjectOrganization> list = this.getSubjectOrganizations(id);
                if (CollectionUtils.isNotEmpty(list)) {
                    OrganizationDto org;
                    Set<OrganizationDto> orgSet = new HashSet<>(list.size());
                    for (SubjectOrganization so : list) {
                        org = new OrganizationDto();
                        org.setId(so.getOrgId());
                        org.setName(so.getOrgName());
                        org.setNamePath(so.getOrgNamePath());
                        orgSet.add(org);
                    }
                    subject.setOrgList(orgSet);
                }
            }
        }
        return subject;
    }

    /**
     * 基于主键查询单一数据对象
     *
     * @param id 主体id
     */
    public Subject getSubject(String id) {
        return dao.findOne(id);
    }

    /**
     * 通过公司代码获取预算主体
     * 如果公司存在多个预算主体,则还需要通过组织确定
     * 如果组织为空,则默认返回第一个
     * 如果组织不为空,则按组织树路径向上匹配预算主体上配置的组织
     *
     * @param classification 预算分类
     * @param useBudget      预算占用参数
     * @return 返回预算主体清单
     */
    public Subject getSubject(Classification classification, BudgetUse useBudget) {
        Subject subject = null;
        // 公司代码
        String corpCode = useBudget.getCorpCode();
        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(Subject.FIELD_CORP_CODE, corpCode));
        search.addFilter(new SearchFilter(Subject.FIELD_CLASSIFICATION, classification));
        List<Subject> subjectList = dao.findByFilters(search);
        if (CollectionUtils.isNotEmpty(subjectList)) {
            if (subjectList.size() == 1) {
                subject = subjectList.get(0);
            } else {
                // 组织机构id
                String orgId = useBudget.getOrg();
                if (StringUtils.isBlank(orgId) || StringUtils.equalsIgnoreCase(Constants.NONE, orgId)) {
                    subject = subjectList.get(0);
                } else {
                    // 按id进行映射方便后续使用
                    Map<String, OrganizationDto> orgMap = null;
                    // 获取指定节点的所有父节点(含自己)
                    ResultData<List<OrganizationDto>> resultData = organizationManager.getParentNodes(orgId, Boolean.TRUE);
                    if (resultData.successful()) {
                        List<OrganizationDto> orgList = resultData.getData();
                        if (CollectionUtils.isNotEmpty(orgList)) {
                            // 组织id映射
                            orgMap = orgList.stream().collect(Collectors.toMap(OrganizationDto::getId, o -> o));
                            orgList.clear();
                        }
                    }
                    if (Objects.nonNull(orgMap)) {
                        // 预算主体id清单
                        Set<String> subjectIds = new HashSet<>();
                        Map<String, Subject> subjectMap = new HashMap<>();
                        for (Subject subj : subjectList) {
                            subjectIds.add(subj.getId());
                            subjectMap.put(subj.getId(), subj);
                        }
                        // 按预算主体id清单查询关联的组织机构
                        List<SubjectOrganization> subjectOrgList = subjectOrganizationDao.findByFilter(new SearchFilter(SubjectOrganization.FIELD_SUBJECT_ID, subjectIds, SearchFilter.Operator.IN));
                        // 组织机构id与预算主体映射
                        Map<String, Subject> orgSubjectMap = new HashMap<>();
                        if (CollectionUtils.isNotEmpty(subjectOrgList)) {
                            for (SubjectOrganization so : subjectOrgList) {
                                orgSubjectMap.put(so.getOrgId(), subjectMap.get(so.getSubjectId()));
                            }
                        }
                        /*
                            组织机构向上查找规则:
                            1.按组织机构树路径,从预算占用的节点开始,向上依次查找
                            2.当按组织节点找到存在的预算池,不管余额是否满足,都将停止向上查找
                         */
                        String parentId = orgId;
                        OrganizationDto org = orgMap.get(parentId);
                        while (Objects.nonNull(org)) {
                            // 按组织id匹配预算池
                            subject = orgSubjectMap.get(org.getId());
                            if (Objects.nonNull(subject)) {
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
                    }
                }
            }
        }
        return subject;
    }

    /**
     * 清除策略缓存
     *
     * @param subjectId 预算主体id
     * @param itemCode  预算科目代码
     */
    public void cleanStrategyCache(String subjectId, String itemCode) {
        CompletableFuture.runAsync(() -> {
            try {
                String prefix = Constants.STRATEGY_CACHE_KEY_PREFIX + subjectId;
                if (StringUtils.isNotBlank(itemCode)) {
                    redisTemplate.delete(prefix.concat(":").concat(itemCode));
                } else {
                    Set<String> keys = redisTemplate.keys(prefix.concat(":*"));
                    if (CollectionUtils.isNotEmpty(keys)) {
                        redisTemplate.delete(keys);
                    }
                }
            } catch (Exception e) {
                LogUtil.error("清空预算执行策略缓存异常.", e);
            }
        });
    }

    /**
     * 通过节点清单构建树
     *
     * @param nodes 节点清单
     * @return 树
     */
    private List<OrganizationDto> buildTree(List<OrganizationDto> nodes) {
        List<OrganizationDto> result = new ArrayList<>();
        if (nodes == null || nodes.size() == 0) {
            return result;
        }
        //将输入节点排序
        List<OrganizationDto> sordedNodes = nodes.stream().sorted(Comparator.comparingInt(n -> n.getNodeLevel() + n.getRank())).collect(Collectors.toList());
        //获取清单中的顶级节点
        for (OrganizationDto node : sordedNodes) {
            String parentId = node.getParentId();
            OrganizationDto parent = sordedNodes.stream().filter((n) -> StringUtils.equals(n.getId(), parentId) && !StringUtils.equals(n.getId(), node.getId())).findAny().orElse(null);
            if (parent == null) {
                //递归构造子节点
                findChildren(node, sordedNodes);
                result.add(node);
            }
        }
        return result.stream().sorted(Comparator.comparingInt(OrganizationDto::getRank)).collect(Collectors.toList());
    }


    /**
     * 递归查找子节点并设置子节点
     *
     * @param treeNode 树形节点（顶级节点）
     * @param nodes    节点清单
     * @return 树形节点
     */
    private OrganizationDto findChildren(OrganizationDto treeNode, List<OrganizationDto> nodes) {
        for (OrganizationDto node : nodes) {
            if (treeNode.getId().equals(node.getParentId())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.getChildren().add(findChildren(node, nodes));
            }
        }
        return treeNode;
    }
}