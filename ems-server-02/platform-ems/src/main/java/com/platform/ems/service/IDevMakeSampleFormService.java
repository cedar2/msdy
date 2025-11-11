package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevMakeSampleForm;

import java.util.List;

/**
 * 打样准许单Service接口
 *
 * @author linhongwei
 * @date 2022-03-24
 */
public interface IDevMakeSampleFormService extends IService<DevMakeSampleForm> {
    /**
     * 查询打样准许单
     *
     * @param makeSampleFormSid 打样准许单ID
     * @return 打样准许单
     */
    public DevMakeSampleForm selectDevMakeSampleFormById(Long makeSampleFormSid);

    /**
     * 查询打样准许单列表
     *
     * @param devMakeSampleForm 打样准许单
     * @return 打样准许单集合
     */
    public List<DevMakeSampleForm> selectDevMakeSampleFormList(DevMakeSampleForm devMakeSampleForm);

    /**
     * 新增打样准许单
     *
     * @param devMakeSampleForm 打样准许单
     * @return 结果
     */
    public DevMakeSampleForm insertDevMakeSampleForm(DevMakeSampleForm devMakeSampleForm);

    /**
     * 修改打样准许单
     *
     * @param devMakeSampleForm 打样准许单
     * @return 结果
     */
    public int updateDevMakeSampleForm(DevMakeSampleForm devMakeSampleForm);

    /**
     * 变更打样准许单
     *
     * @param devMakeSampleForm 打样准许单
     * @return 结果
     */
    public int changeDevMakeSampleForm(DevMakeSampleForm devMakeSampleForm);

    /**
     * 批量删除打样准许单
     *
     * @param makeSampleFormSids 需要删除的打样准许单ID
     * @return 结果
     */
    public int deleteDevMakeSampleFormByIds(List<Long> makeSampleFormSids);

    /**
     * 更改确认状态
     *
     * @param devMakeSampleForm
     * @return
     */
    int check(DevMakeSampleForm devMakeSampleForm);

    int updateHandleStatus(DevMakeSampleForm devMakeSampleForm);

}
