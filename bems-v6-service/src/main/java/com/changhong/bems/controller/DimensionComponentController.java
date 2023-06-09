package com.changhong.bems.controller;

import com.changhong.bems.api.DimensionComponentApi;
import com.changhong.bems.dto.*;
import com.changhong.bems.entity.Item;
import com.changhong.bems.entity.Period;
import com.changhong.bems.service.cust.BudgetDimensionCustManager;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.util.EnumUtils;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 预算维度(Dimension)控制类
 *
 * @author sei
 * @since 2021-04-22 12:54:23
 */
@RestController
@Api(value = "DimensionComponentApi", tags = "预算维度UI组件服务")
@RequestMapping(path = DimensionComponentApi.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class DimensionComponentController implements DimensionComponentApi {
    /**
     * 预算维度组件服务
     */
    @Autowired
    private BudgetDimensionCustManager budgetDimensionCustManager;

    @Autowired
    private ModelMapper modelMapper;


    /**
     * 获取指定预算主体的科目(维度组件专用)
     *
     * @param subjectId 预算主体id
     * @return 子实体清单
     */
    @Override
    public ResultData<List<BudgetItemDto>> getBudgetItems(String subjectId) {
        List<Item> items = budgetDimensionCustManager.getBudgetItems(subjectId);
        return ResultData.success(items.stream().map(s -> modelMapper.map(s, BudgetItemDto.class)).collect(Collectors.toList()));
    }

    /**
     * 按预算主体和期间类型获取期间
     *
     * @param subjectId 预算主体id
     * @param type      预算期间类型
     * @return 期间清单
     */
    @Override
    public ResultData<List<PeriodDto>> getPeriods(String subjectId, String type) {
        List<Period> periods = budgetDimensionCustManager.getPeriods(subjectId, EnumUtils.getEnum(PeriodType.class, type));
        return ResultData.success(periods.stream().map(s -> modelMapper.map(s, PeriodDto.class)).collect(Collectors.toList()));
    }

    /**
     * 按预算主体获取组织机构
     *
     * @param subjectId 预算主体id
     * @return 期间清单
     */
    @Override
    public ResultData<List<OrganizationDto>> getOrgTree(String subjectId) {
        return budgetDimensionCustManager.getOrgTree(subjectId);
    }

    /**
     * 按预算主体获取公司项目
     *
     * @param request 查询公司项目请求
     * @return 公司项目
     */
    @Override
    public ResultData<List<ProjectDto>> getProjects(QueryProjectRequest request) {
        String subjectId = request.getSubjectId();
        String searchValue = request.getSearchValue();
        Set<String> excludeIds = request.getExcludeIds();
        return budgetDimensionCustManager.getProjects(subjectId, searchValue, excludeIds);
    }
}