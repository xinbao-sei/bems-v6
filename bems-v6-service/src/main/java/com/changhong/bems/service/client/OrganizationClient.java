package com.changhong.bems.service.client;

import com.changhong.bems.dto.OrganizationDto;
import com.changhong.sei.core.dto.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-04-22 23:16
 */
@FeignClient(name = "sei-basic", path = "organization")
public interface OrganizationClient {

    /**
     * 通过代码获取组织机构
     *
     * @param code 代码
     * @return 组织机构
     */
    @GetMapping(path = "findByCode")
    ResultData<OrganizationDto> findByCode(@RequestParam("code") String code);

    /**
     * 获取组织机构树(不包含冻结)
     *
     * @return 组织机构树清单
     */
    @GetMapping(path = "findOrgTreeWithoutFrozen")
    ResultData<List<OrganizationDto>> findOrgTreeWithoutFrozen();

    /**
     * 根据指定的节点id获取树
     *
     * @param nodeId 节点ID
     * @return 返回已指定节点ID为根的树
     */
    @GetMapping(path = "getTree4Unfrozen")
    ResultData<OrganizationDto> getTree4Unfrozen(@RequestParam("nodeId") String nodeId);

    /**
     * 通过组织机构id获取组织机构清单
     *
     * @param nodeId 组织机构id
     * @return 组织机构清单（非树形）
     */
    @GetMapping(path = "getChildrenNodes4Unfrozen")
    ResultData<List<OrganizationDto>> getChildrenNodes4Unfrozen(@RequestParam("nodeId") String nodeId);

    /**
     * 通过组织机构id清单获取下级组织机构清单
     *
     * @param orgIds 组织机构id清单
     * @return 组织机构清单（非树形）
     */
    @PostMapping(path = "getChildrenNodes4UnfrozenByIds", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResultData<List<OrganizationDto>> getChildrenNodes4UnfrozenByIds(@RequestBody Set<String> orgIds);

    /**
     * 获取一个节点的所有父节点
     *
     * @param nodeId      节点Id
     * @param includeSelf 是否包含本节点
     * @return 父节点清单
     */
    @GetMapping(path = "getParentNodes")
    ResultData<List<OrganizationDto>> getParentNodes(@RequestParam("nodeId") String nodeId, @RequestParam("includeSelf") boolean includeSelf);

    /**
     * 通过id集合获取组织机构清单
     *
     * @param orgIds id集合
     * @return 组织机构
     */
    @PostMapping(path = "findOrganizationByIds", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResultData<List<OrganizationDto>> findOrganizationByIds(@RequestBody Set<String> orgIds);
}
