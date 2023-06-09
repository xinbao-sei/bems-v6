package com.changhong.bems.service;

import com.changhong.bems.commons.Constants;
import com.changhong.bems.dao.CategoryDimensionDao;
import com.changhong.bems.dto.Classification;
import com.changhong.bems.entity.Category;
import com.changhong.bems.entity.CategoryDimension;
import com.changhong.bems.entity.Dimension;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.dto.serach.SearchFilter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 预算类型维度关系(CategoryDimension)业务逻辑实现类
 *
 * @author sei
 * @since 2021-04-22 12:54:24
 */
@Service
public class CategoryDimensionService {
    @Autowired
    private CategoryDimensionDao dao;
    @Autowired
    private DimensionService dimensionService;

    /**
     * 添加必要维度
     *
     * @param category 预算类型
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRequiredDimension(Category category) {
        // 租户代码
        String tenantCode = ContextUtil.getTenantCode();
        CategoryDimension categoryDimension;
        Set<CategoryDimension> dimensionList = new HashSet<>();
        List<Dimension> dimensions = dimensionService.getRequired();
        for (Dimension dimension : dimensions) {
            categoryDimension = new CategoryDimension();
            categoryDimension.setCategoryId(category.getId());
            categoryDimension.setDimensionCode(dimension.getCode());
            categoryDimension.setRank(dimension.getRank());
            categoryDimension.setTenantCode(tenantCode);
            dimensionList.add(categoryDimension);
        }
        // 按预算分类增加必要维度
        if (Objects.equals(Classification.DEPARTMENT, category.getClassification())) {
            categoryDimension = new CategoryDimension();
            categoryDimension.setCategoryId(category.getId());
            categoryDimension.setDimensionCode(Constants.DIMENSION_CODE_ORG);
            categoryDimension.setTenantCode(tenantCode);
            dimensionList.add(categoryDimension);
        } else if (Objects.equals(Classification.PROJECT, category.getClassification())) {
            categoryDimension = new CategoryDimension();
            categoryDimension.setCategoryId(category.getId());
            categoryDimension.setDimensionCode(Constants.DIMENSION_CODE_PROJECT);
            categoryDimension.setTenantCode(tenantCode);
            dimensionList.add(categoryDimension);
        } else if (Objects.equals(Classification.COST_CENTER, category.getClassification())) {
            categoryDimension = new CategoryDimension();
            categoryDimension.setCategoryId(category.getId());
            categoryDimension.setDimensionCode(Constants.DIMENSION_CODE_COST_CENTER);
            categoryDimension.setTenantCode(tenantCode);
            dimensionList.add(categoryDimension);
        }
        dao.save(dimensionList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addReferenceDimension(String categoryId, String referenceId) {
        // 租户代码
        String tenantCode = ContextUtil.getTenantCode();
        CategoryDimension categoryDimension;
        List<CategoryDimension> dimensionList = new ArrayList<>();
        List<CategoryDimension> dimensions = this.getByCategoryId(referenceId);
        for (CategoryDimension dimension : dimensions) {
            categoryDimension = new CategoryDimension();
            categoryDimension.setCategoryId(categoryId);
            categoryDimension.setDimensionCode(dimension.getDimensionCode());
            categoryDimension.setRank(dimension.getRank());
            categoryDimension.setTenantCode(tenantCode);
            dimensionList.add(categoryDimension);
        }
        dao.save(dimensionList);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultData<Void> addCategoryDimension(String categoryId, Set<String> dimensionCodes) {
        // 租户代码
        String tenantCode = ContextUtil.getTenantCode();
        List<CategoryDimension> dimensionList = new ArrayList<>();
        CategoryDimension categoryDimension;
        for (String code : dimensionCodes) {
            Dimension dimension = dimensionService.findByCode(code);
            if (Objects.isNull(dimension)) {
                // 维度不存在
                return ResultData.fail(ContextUtil.getMessage("dimension_00002", code));
            }
            categoryDimension = new CategoryDimension();
            categoryDimension.setCategoryId(categoryId);
            categoryDimension.setDimensionCode(dimension.getCode());
            categoryDimension.setRank(dimension.getRank());
            categoryDimension.setTenantCode(tenantCode);
            dimensionList.add(categoryDimension);
        }
        dao.save(dimensionList);
        return ResultData.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeCategoryDimension(Set<String> ids) {
        dao.delete(ids);
    }

    /**
     * 根据预算类型id和维度代码获取分配关系
     *
     * @param categoryId 预算类型id
     * @return 返回分配关系清单
     */
    public List<CategoryDimension> getByCategoryId(String categoryId) {
        return dao.findListByProperty(CategoryDimension.FIELD_CATEGORY_ID, categoryId);
    }

    /**
     * 通过预算类型获取预算维度代码清单
     *
     * @param categoryIds 预算类型清单
     * @return 预算维度代码清单
     */
    public Set<String> getDimensionCodeByCategory(Set<String> categoryIds) {
        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(CategoryDimension.FIELD_CATEGORY_ID, categoryIds, SearchFilter.Operator.IN));
        List<CategoryDimension> list = dao.findByFilters(search);

        Set<String> dimensionCodeSet;
        if (CollectionUtils.isNotEmpty(list)) {
            dimensionCodeSet = list.stream().map(CategoryDimension::getDimensionCode).collect(Collectors.toSet());
        } else {
            dimensionCodeSet = new HashSet<>();
        }

        return dimensionCodeSet;
    }

    public CategoryDimension getByDimensionCode(String dimensionCode) {
        return dao.findFirstByProperty(CategoryDimension.FIELD_DIMENSION_CODE, dimensionCode);
    }

    /**
     * 根据预算类型id和维度代码获取分配关系
     *
     * @param categoryId 预算类型id
     * @param codes      预算维度代码
     * @return 返回分配关系清单
     */
    public List<CategoryDimension> getCategoryDimensions(String categoryId, Collection<String> codes) {
        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(CategoryDimension.FIELD_CATEGORY_ID, categoryId));
        search.addFilter(new SearchFilter(CategoryDimension.FIELD_DIMENSION_CODE, codes, SearchFilter.Operator.IN));
        return dao.findByFilters(search);
    }

}