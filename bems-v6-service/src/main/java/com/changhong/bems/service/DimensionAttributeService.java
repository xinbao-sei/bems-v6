package com.changhong.bems.service;

import com.changhong.bems.dao.DimensionAttributeDao;
import com.changhong.bems.entity.BaseAttribute;
import com.changhong.bems.entity.DimensionAttribute;
import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.core.dto.serach.SearchFilter;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.exception.ServiceException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


/**
 * 预算维度属性(DimensionAttribute)业务逻辑实现类
 *
 * @author sei
 * @since 2021-04-22 12:54:29
 */
@Service
public class DimensionAttributeService extends BaseEntityService<DimensionAttribute> {
    @Autowired
    private DimensionAttributeDao dao;

    @Override
    protected BaseEntityDao<DimensionAttribute> getDao() {
        return dao;
    }

    /**
     * 添加一个预算维度属性
     *
     * @param attribute 属性
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultData<String> add(DimensionAttribute attribute) {
        if (Objects.isNull(attribute)) {
            // 添加的预算维度属性不能为空
            return ResultData.fail(ContextUtil.getMessage("dimension_attribute_00001"));
        }
        String id;
        DimensionAttribute attr = getAttribute(attribute.getSubjectId(), attribute);
        if (Objects.nonNull(attr)) {
            id = attr.getId();
        } else {
            this.save(attribute);
            id = attribute.getId();
        }
        return ResultData.success(id);
    }

    /**
     * 按属性维度hash获取
     * 按维度属性值一一匹配
     */
    public DimensionAttribute getAttribute(String subjectId, BaseAttribute attribute) {
        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(DimensionAttribute.FIELD_SUBJECT_ID, subjectId));
        search.addFilter(new SearchFilter(DimensionAttribute.FIELD_ATTRIBUTE_HASH, attribute.getAttributeHash()));
        return dao.findOneByFilters(search);
    }

    /**
     * 按属性维度获取
     * 主要用于预算使用时,无期间维度查找预算
     */
    public List<DimensionAttribute> getAttributes(DimensionAttribute attribute) {
        String attrStr = attribute.getAttribute();
        Search search = Search.createSearch();
        search.addFilter(new SearchFilter(DimensionAttribute.FIELD_SUBJECT_ID, attribute.getSubjectId()));
        search.addFilter(new SearchFilter(DimensionAttribute.FIELD_ATTRIBUTE, attribute.getAttribute()));
        // 预算科目
        if (StringUtils.contains(attrStr, attribute.getItem())) {
            search.addFilter(new SearchFilter(DimensionAttribute.FIELD_ITEM, attribute.getItem()));
        }
        // 预算期间
        if (StringUtils.contains(attrStr, attribute.getPeriod())) {
            search.addFilter(new SearchFilter(DimensionAttribute.FIELD_PERIOD, attribute.getPeriod()));
        }
        // 组织机构
        if (StringUtils.contains(attrStr, attribute.getOrg())) {
            search.addFilter(new SearchFilter(DimensionAttribute.FIELD_ORG, attribute.getOrg()));
        }
        // 预算项目
        if (StringUtils.contains(attrStr, attribute.getProject())) {
            search.addFilter(new SearchFilter(DimensionAttribute.FIELD_PROJECT, attribute.getProject()));
        }
        // 自定义1
        if (StringUtils.contains(attrStr, attribute.getUdf1())) {
            search.addFilter(new SearchFilter(DimensionAttribute.FIELD_UDF1, attribute.getUdf1()));
        }
        // 自定义2
        if (StringUtils.contains(attrStr, attribute.getUdf2())) {
            search.addFilter(new SearchFilter(DimensionAttribute.FIELD_UDF2, attribute.getUdf2()));
        }
        // 自定义3
        if (StringUtils.contains(attrStr, attribute.getUdf3())) {
            search.addFilter(new SearchFilter(DimensionAttribute.FIELD_UDF3, attribute.getUdf3()));
        }
        // 自定义4
        if (StringUtils.contains(attrStr, attribute.getUdf4())) {
            search.addFilter(new SearchFilter(DimensionAttribute.FIELD_UDF4, attribute.getUdf4()));
        }
        // 自定义5
        if (StringUtils.contains(attrStr, attribute.getUdf5())) {
            search.addFilter(new SearchFilter(DimensionAttribute.FIELD_UDF5, attribute.getUdf5()));
        }
        return dao.findByFilters(search);
    }

}