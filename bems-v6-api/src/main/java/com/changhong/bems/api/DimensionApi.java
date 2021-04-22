package com.changhong.bems.api;

import com.changhong.bems.dto.DimensionDto;
import com.changhong.bems.dto.KeyValueDto;
import com.changhong.bems.dto.StrategyDto;
import com.changhong.sei.core.api.BaseEntityApi;
import com.changhong.sei.core.api.FindAllApi;
import com.changhong.sei.core.api.FindByPageApi;
import com.changhong.sei.core.dto.ResultData;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * 预算维度(Dimension)API
 *
 * @author sei
 * @since 2021-04-22 12:54:24
 */
@Valid
@FeignClient(name = "bems-v6", path = DimensionApi.PATH)
public interface DimensionApi extends BaseEntityApi<DimensionDto>, FindAllApi<DimensionDto> {
    String PATH = "dimension";

    /**
     * 获取所有预制的维度代码
     *
     * @return 策略清单
     */
    @GetMapping(path = "findAllCodes")
    @ApiOperation(value = "获取所有维度代码", notes = "获取所有预制的维度代码")
    ResultData<Set<KeyValueDto>> findAllCodes();
}