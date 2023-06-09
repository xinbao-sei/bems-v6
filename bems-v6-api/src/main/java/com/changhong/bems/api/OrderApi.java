package com.changhong.bems.api;

import com.changhong.bems.dto.*;
import com.changhong.sei.core.api.BaseEntityApi;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.flow.FlowInvokeParams;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 预算申请单(Order)API
 *
 * @author sei
 * @since 2021-04-25 15:13:58
 */
@Valid
@FeignClient(name = "bems-v6", path = OrderApi.PATH)
public interface OrderApi extends BaseEntityApi<OrderDto> {
    String PATH = "order";

    /**
     * 获取组织机构树(不包含冻结)
     *
     * @return 组织机构树清单
     */
    @GetMapping(path = "findOrgTree")
    @ApiOperation(value = "获取组织机构树(不包含冻结)", notes = "获取组织机构树(不包含冻结)")
    ResultData<List<OrganizationDto>> findOrgTree();

    /**
     * 分页查询下达注入订单
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @PostMapping(path = "findInjectionByPage", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "分页查询下达注入订单", notes = "分页查询下达注入订单")
    ResultData<PageResult<OrderDto>> findInjectionByPage(@RequestBody OrderSearch search);

    /**
     * 分页查询下达调整订单
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @PostMapping(path = "findAdjustmentByPage", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "分页查询下达调整订单", notes = "分页查询下达调整订单")
    ResultData<PageResult<OrderDto>> findAdjustmentByPage(@RequestBody OrderSearch search);

    /**
     * 分页查询分解调整订单
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @PostMapping(path = "findSplitByPage", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "分页查询分解调整订单", notes = "分页查询分解调整订单")
    ResultData<PageResult<OrderDto>> findSplitByPage(@RequestBody OrderSearch search);

    /**
     * 通过单据Id获取单据行项
     *
     * @param orderId 单据Id
     * @return 业务实体
     */
    @PostMapping(path = "getOrderItems/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "通过单据Id获取单据行项", notes = "通过单据Id分页获取单据行项")
    ResultData<PageResult<OrderDetailDto>> getOrderItems(@PathVariable("orderId") String orderId, @RequestBody Search search);

    /**
     * 通过单据Id清空单据行项
     *
     * @param orderId 单据Id
     * @return 业务实体
     */
    @PostMapping(path = "clearOrderItems")
    @ApiOperation(value = "通过单据Id清空单据行项", notes = "通过单据Id清空单据行项")
    ResultData<Void> clearOrderItems(@RequestParam("orderId") String orderId);

    /**
     * 通过单据行项id删除行项
     *
     * @param detailIds 单据Id
     * @return 业务实体
     */
    @DeleteMapping(path = "removeOrderItems", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "通过单据行项id删除行项", notes = "通过单据行项id删除行项")
    ResultData<Void> removeOrderItems(@RequestBody String[] detailIds);

    /**
     * 预算分解分组行项id删除
     *
     * @param groupId 分解分组行项Id
     * @return 业务实体
     */
    @DeleteMapping(path = "removeSplitOrderItems")
    @ApiOperation(value = "预算分解分组行项id删除", notes = "预算分解分组行项id删除")
    ResultData<Void> removeSplitOrderItems(@RequestParam("groupId") String groupId);

    /**
     * 通过单据Id检查预算主体和类型是否被修改
     *
     * @param orderId 单据Id
     * @return 检查结果
     */
    @GetMapping(path = "checkDimension")
    @ApiOperation(value = "检查预算主体和类型是否修改", notes = "通过单据Id检查预算主体和类型是否被修改")
    ResultData<Void> checkDimension(@RequestParam("orderId") String orderId,
                                    @RequestParam("subjectId") String subjectId,
                                    @RequestParam("categoryId") String categoryId);

    /**
     * 添加预算申请单行项明细
     *
     * @param order 业务实体DTO
     * @return 返回订单头id
     */
    @PostMapping(path = "addOrderDetails", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "添加预算申请单行项明细", notes = "批量添加一个预算申请单行项明细")
    ResultData<String> addOrderDetails(@RequestBody @Valid AddOrderDetail order);

    /**
     * 更新预算申请单行项金额
     *
     * @param detailId 申请单行项id
     * @param amount   金额
     * @return 返回订单头id
     */
    @PostMapping(path = "updateDetailAmount")
    @ApiOperation(value = "更新预算申请单行项金额", notes = "检查并更新预算申请单行项金额")
    ResultData<OrderDetailDto> updateDetailAmount(@RequestParam("detailId") String detailId, @RequestParam("amount") double amount);

    /**
     * 获取一个预算申请单
     *
     * @param orderId 申请单id
     * @return 返回订单头
     */
    @GetMapping(path = "getHead")
    @ApiOperation(value = "获取一个预算申请单", notes = "获取一个预算申请单")
    ResultData<OrderDto> getOrderHead(@RequestParam("id") String orderId);

    /**
     * 保存预算申请单
     *
     * @param order 业务实体DTO
     * @return 返回订单头id
     */
    @PostMapping(path = "saveOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存预算申请单", notes = "保存一个预算申请单")
    ResultData<OrderDto> saveOrder(@RequestBody @Valid OrderDto order);

    /**
     * 检查是否存在注入类型预制单
     *
     * @return 返回检查结果
     */
    @GetMapping(path = "checkInjectPrefab")
    @ApiOperation(value = "检查是否存在注入类型预制单", notes = "检查是否存在注入类型预制单")
    ResultData<List<OrderDto>> checkInjectPrefab();

    /**
     * 检查是否存在调整类型预制单
     *
     * @return 返回检查结果
     */
    @GetMapping(path = "checkAdjustPrefab")
    @ApiOperation(value = "检查是否存在调整类型预制单", notes = "检查是否存在调整类型预制单")
    ResultData<List<OrderDto>> checkAdjustPrefab();

    /**
     * 检查是否存在分解类型预制单
     *
     * @return 返回检查结果
     */
    @GetMapping(path = "checkSplitPrefab")
    @ApiOperation(value = "检查是否存在分解类型预制单", notes = "检查是否存在分解类型预制单")
    ResultData<List<OrderDto>> checkSplitPrefab();

    /**
     * 检查行项是否存储未处理或有错误的
     * 主要用于流程中编辑时的下一步控制
     *
     * @return 返回检查结果
     */
    @GetMapping(path = "checkProcessed")
    @ApiOperation(value = "检查行项是否存储未处理或有错误的", notes = "检查行项是否存储未处理或有错误的.主要用于流程中编辑时的下一步控制")
    ResultData<Void> checkProcessed(@RequestParam("orderId") String orderId);

    /**
     * 获取申请单调整数据
     *
     * @return 返回调整数据
     */
    @GetMapping(path = "getAdjustData")
    @ApiOperation(value = "获取申请单调整数据", notes = "获取申请单调整数据")
    ResultData<Map<String, Number>> getAdjustData(@RequestParam("orderId") String orderId);

    /**
     * 获取申请单合计金额
     *
     * @return 返回调整数据
     */
    @GetMapping(path = "getSumAmount")
    @ApiOperation(value = "获取申请单合计金额", notes = "获取申请单合计金额")
    ResultData<Double> getSumAmount(@RequestParam("orderId") String orderId);

    /**
     * 分页查询预算分解上级期间预算
     *
     * @param param 查询参数
     * @return 上级期间预算
     */
    @PostMapping(path = "querySplitGroup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "分页查询预算分解上级期间预算", notes = "分页查询预算分解上级期间预算")
    ResultData<PageResult<OrderDetailDto>> querySplitGroup(@RequestBody @Valid SplitDetailQuickQueryParam param);

    /**
     * excel文件数据导入
     *
     * @return 检查结果
     */
    @PostMapping(path = "import")
    @ApiOperation(value = "导入excel文件预算数据", notes = "导入excel文件预算数据")
    ResultData<String> importBudge(@RequestPart("order") AddOrderDetail order, @RequestPart("file") MultipartFile file);

    /**
     * 导出预算订单明细数据
     *
     * @return 导出的明细数据
     */
    @GetMapping(path = "export")
    @ApiOperation(value = "导出预算订单明细数据", notes = "导出预算订单明细数据")
    ResultData<Map<String, Object>> exportBudgeDetails(@RequestParam("orderId") String orderId);

    /**
     * 获取预算模版格式数据
     *
     * @param categoryId 预算类型id
     * @return 预算模版格式数据
     */
    @GetMapping(path = "getBudgetTemplate")
    @ApiOperation(value = "获取预算模版格式数据", notes = "获取预算模版格式数据")
    ResultData<List<String>> getBudgetTemplate(@RequestParam("categoryId") String categoryId);

    /**
     * 获取预算维度主数据
     *
     * @param subjectId 预算主体id
     * @param dimCode   预算维度代码
     * @return 导出预算模版数据
     */
    @GetMapping(path = "getDimensionValues")
    @ApiOperation(value = "获取预算维度主数据", notes = "获取预算维度主数据(导入用)")
    ResultData<Map<String, Object>> getDimensionValues(@RequestParam("subjectId") String subjectId, @RequestParam("dimCode") String dimCode);

    /**
     * 已确认的预算申请单直接生效
     *
     * @param orderId 申请单id
     * @return 返回处理结果
     */
    @PostMapping(path = "effectiveOrder")
    @ApiOperation(value = "预算申请单生效", notes = "已确认的预算申请单直接生效")
    ResultData<OrderDto> effectiveOrder(@RequestParam("orderId") String orderId);

    /**
     * 获取订单处理状态
     * 前端轮询调用,以获取实时处理进度
     *
     * @param orderId 订单id
     * @return 处理状态
     */
    @GetMapping(path = "getProcessingStatus")
    @ApiOperation(value = "获取订单处理状态", notes = "获取订单处理状态.前端轮询调用,以获取实时处理进度")
    ResultData<OrderStatistics> getProcessingStatus(@RequestParam("orderId") String orderId);

    /**
     * 预算调整时按行项创建预算池
     *
     * @param detailId 申请行项id
     * @return 返回处理结果
     */
    @PostMapping(path = "createPool")
    @ApiOperation(value = "预算调整时创建预算池", notes = "预算调整时按行项创建预算池")
    ResultData<OrderDetailDto> createPool(@RequestParam("detailId") String detailId);

    ///////////////////////流程集成 start//////////////////////////////

    /**
     * 工作流获取条件属性说明
     *
     * @param businessModelCode 业务实体代码
     * @param all               是否查询全部
     * @return POJO属性说明Map
     */
    @GetMapping(path = "properties")
    @ApiOperation(value = "工作流获取条件属性说明", notes = "工作流获取条件属性说明")
    ResultData<Map<String, String>> properties(@RequestParam("businessModelCode") String businessModelCode,
                                               @RequestParam("all") Boolean all);

    /**
     * 获取条件POJO属性键值对
     *
     * @param businessModelCode 业务实体代码
     * @param id                单据id
     * @return POJO属性说明Map
     */
    @GetMapping(path = "propertiesAndValues")
    @ApiOperation(value = "通过业务实体代码,业务ID获取条件POJO属性键值对", notes = "测试")
    ResultData<Map<String, Object>> propertiesAndValues(@RequestParam("businessModelCode") String businessModelCode,
                                                        @RequestParam("id") String id,
                                                        @RequestParam(name = "all", required = false) Boolean all);

    /**
     * 获取条件POJO属性初始化值键值对
     *
     * @param businessModelCode 业务实体代码
     * @return POJO属性说明Map
     */
    @GetMapping(path = "initPropertiesAndValues")
    @ApiOperation(value = "通过业务实体代码获取条件POJO属性初始化值键值对", notes = "测试")
    ResultData<Map<String, Object>> initPropertiesAndValues(@RequestParam("businessModelCode") String businessModelCode);

    /**
     * 重置单据状态
     *
     * @param businessModelCode 业务实体代码
     * @param id                单据id
     * @param status            状态
     * @return 返回结果
     */
    @PostMapping(path = "resetState")
    @ApiOperation(value = "通过业务实体代码及单据ID重置业务单据流程状态", notes = "测试")
    ResultData<Boolean> resetState(@RequestParam("businessModelCode") String businessModelCode,
                                   @RequestParam("id") String id,
                                   @RequestParam("status") String status);

    /**
     * 流程结束事件,生效预算申请单
     *
     * @param flowInvokeParams 服务、事件输入参数VO
     * @return 操作结果
     */
    @PostMapping(path = "flowEndEvent", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "预算流程结束事件", notes = "流程结束事件,生效预算申请单")
    ResultData<Boolean> flowEndEvent(@RequestBody FlowInvokeParams flowInvokeParams);


    /**
     * 移动端页面属性
     */
    @GetMapping("formPropertiesAndValues")
    @ApiOperation(value = "移动端流程接口", notes = "移动端流程接口")
    ResultData<Map<String, Object>> formPropertiesAndValues(@RequestParam("businessModelCode") String businessModelCode,
                                                            @RequestParam("id") String id);
    ///////////////////////流程集成 end//////////////////////////////
}