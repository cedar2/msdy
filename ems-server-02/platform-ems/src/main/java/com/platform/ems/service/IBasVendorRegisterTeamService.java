package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRegisterTeam;

/**
 * 供应商注册-人员信息Service接口
 *
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRegisterTeamService extends IService<BasVendorRegisterTeam> {
    /**
     * 查询供应商注册-人员信息
     *
     * @param vendorRegisterTeamSid 供应商注册-人员信息ID
     * @return 供应商注册-人员信息
     */
    public BasVendorRegisterTeam selectBasVendorRegisterTeamById(Long vendorRegisterTeamSid);

    /**
     * 查询供应商注册-人员信息列表
     *
     * @param basVendorRegisterTeam 供应商注册-人员信息
     * @return 供应商注册-人员信息集合
     */
    public List<BasVendorRegisterTeam> selectBasVendorRegisterTeamList(BasVendorRegisterTeam basVendorRegisterTeam);

    /**
     * 新增供应商注册-人员信息
     *
     * @param basVendorRegisterTeam 供应商注册-人员信息
     * @return 结果
     */
    public int insertBasVendorRegisterTeam(BasVendorRegisterTeam basVendorRegisterTeam);

    /**
     * 修改供应商注册-人员信息
     *
     * @param basVendorRegisterTeam 供应商注册-人员信息
     * @return 结果
     */
    public int updateBasVendorRegisterTeam(BasVendorRegisterTeam basVendorRegisterTeam);

    /**
     * 变更供应商注册-人员信息
     *
     * @param basVendorRegisterTeam 供应商注册-人员信息
     * @return 结果
     */
    public int changeBasVendorRegisterTeam(BasVendorRegisterTeam basVendorRegisterTeam);

    /**
     * 批量删除供应商注册-人员信息
     *
     * @param vendorRegisterTeamSids 需要删除的供应商注册-人员信息ID
     * @return 结果
     */
    public int deleteBasVendorRegisterTeamByIds(List<Long> vendorRegisterTeamSids);

    /**
     * 由主表查询供应商注册-人员信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-人员信息集合
     */
    public List<BasVendorRegisterTeam> selectBasVendorRegisterTeamListById(Long vendorRegisterSid);

    /**
     * 由主表批量新增供应商注册-人员信息
     *
     * @param basVendorRegisterTeamList List 供应商注册-人员信息
     * @return 结果
     */
    public int insertBasVendorRegisterTeam(List<BasVendorRegisterTeam> basVendorRegisterTeamList, Long vendorRegisterSid);

    /**
     * 批量修改供应商注册-人员信息
     *
     * @param basVendorRegisterTeamList List 供应商注册-人员信息
     * @return 结果
     */
    public int updateBasVendorRegisterTeam(List<BasVendorRegisterTeam> basVendorRegisterTeamList);

    /**
     * 由主表批量修改供应商注册-人员信息
     *
     * @param response List 供应商注册-人员信息 (原来的)
     * @param request  List 供应商注册-人员信息 (更新后的)
     * @return 结果
     */
    public int updateBasVendorRegisterTeam(List<BasVendorRegisterTeam> response, List<BasVendorRegisterTeam> request, Long vendorRegisterSid);

    /**
     * 由主表批量删除供应商注册-人员信息
     *
     * @param vendorRegisterSids 需要删除的供应商注册IDs
     * @return 结果
     */
    public int deleteBasVendorRegisterTeamListByIds(List<Long> vendorRegisterSids);

}
