package com.changhong.bems.sdk.client;

import com.changhong.bems.dto.BudgetPoolAmountDto;
import com.changhong.bems.dto.BudgetRequest;
import com.changhong.bems.dto.BudgetResponse;
import com.changhong.sei.core.dto.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * 预算(Budget)API
 *
 * @author sei
 * @since 2021-04-22 12:54:28
 */
@Valid
@FeignClient(name = "bems-v6", path = BudgetApiClient.PATH)
public interface BudgetApiClient {
    String PATH = "budget";

    /**
     * 使用预算
     * 包含占用和释放
     *
     * @param request 使用预算请求
     * @return 使用预算结果
     */
    @PostMapping(path = "use", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResultData<List<BudgetResponse>> use(@RequestBody @Validated BudgetRequest request);

    /**
     * 通过预算池代码获取一个预算池
     *
     * @param poolCode 预算池code
     * @return 预算池
     */
    @GetMapping(path = "getPoolByCode")
    ResultData<BudgetPoolAmountDto> getPoolByCode(@RequestParam("poolCode") String poolCode);

    /**
     * 通过预算池代码获取一个预算池
     *
     * @param poolCodes 预算池code
     * @return 预算池
     */
    @GetMapping(path = "getPoolsByCode")
    ResultData<List<BudgetPoolAmountDto>> getPoolsByCode(@RequestBody List<String> poolCodes);
}