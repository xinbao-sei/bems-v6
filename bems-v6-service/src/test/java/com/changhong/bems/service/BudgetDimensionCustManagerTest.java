package com.changhong.bems.service;

import com.changhong.bems.dto.OrganizationDto;
import com.changhong.bems.dto.ProjectDto;
import com.changhong.bems.service.cust.BudgetDimensionCustManager;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.test.BaseUnit5Test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-10-11 09:21
 */
class BudgetDimensionCustManagerTest extends BaseUnit5Test {
    @Autowired
    private BudgetDimensionCustManager budgetDimensionCustManager;

    @Test
    void getBudgetItems() {
    }

    @Test
    void getPeriods() {
    }

    @Test
    void getOrgTree() {
        String subjectId = "7F757D51-532E-11EC-ABD0-0242C0A84424";
        ResultData<List<OrganizationDto>> resultData = budgetDimensionCustManager.getOrgTree(subjectId);
        System.out.println(resultData);
    }

    @Test
    void getProjects() {
        String subjectId = "C81A4E58-BBD4-11EB-A896-0242C0A84429";
        ResultData<List<ProjectDto>> resultData = budgetDimensionCustManager.getProjects(subjectId, "test", null);
        System.out.println(resultData);
    }

    @Test
    void getDimensionValues() {

    }
}