package com.changhong.bems.sdk.client;

import com.changhong.bems.sdk.dto.BudgetRequest;
import com.changhong.bems.sdk.dto.BudgetResponse;
import com.changhong.sei.core.dto.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

}