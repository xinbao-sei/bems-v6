package com.changhong.bems.api;

import com.changhong.bems.dto.ItemDto;
import com.changhong.sei.core.api.BaseEntityApi;
import com.changhong.sei.core.api.FindByPageApi;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 预算科目(Item)API
 *
 * @author sei
 * @since 2021-04-22 12:54:30
 */
@Valid
@FeignClient(name = "bems-v6", path = ItemApi.PATH)
public interface ItemApi extends BaseEntityApi<ItemDto>, FindByPageApi<ItemDto> {
    String PATH = "item";

    /**
     * 分页获取预算科目(外部系统集成专用)
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @PostMapping(path = "getBudgetItems", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "分页获取预算科目", notes = "分页获取预算科目(外部系统集成专用)")
    ResultData<PageResult<ItemDto>> getBudgetItems(@RequestBody Search search);
}