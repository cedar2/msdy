package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevMakeSampleFormAttach;

import java.util.List;

/**
 * 打样准许单-附件Service接口
 *
 * @author linhongwei
 * @date 2022-03-24
 */
public interface IDevMakeSampleFormAttachService extends IService<DevMakeSampleFormAttach> {
    /**
     * 查询打样准许单-附件
     *
     * @param attachmentSid 打样准许单-附件ID
     * @return 打样准许单-附件
     */
    public DevMakeSampleFormAttach selectDevMakeSampleFormAttachById(Long attachmentSid);

    /**
     * 查询打样准许单-附件列表
     *
     * @param devMakeSampleFormAttach 打样准许单-附件
     * @return 打样准许单-附件集合
     */
    public List<DevMakeSampleFormAttach> selectDevMakeSampleFormAttachList(DevMakeSampleFormAttach devMakeSampleFormAttach);

    /**
     * 新增打样准许单-附件
     *
     * @param devMakeSampleFormAttach 打样准许单-附件
     * @return 结果
     */
    public int insertDevMakeSampleFormAttach(DevMakeSampleFormAttach devMakeSampleFormAttach);

    /**
     * 修改打样准许单-附件
     *
     * @param devMakeSampleFormAttach 打样准许单-附件
     * @return 结果
     */
    public int updateDevMakeSampleFormAttach(DevMakeSampleFormAttach devMakeSampleFormAttach);

    /**
     * 变更打样准许单-附件
     *
     * @param devMakeSampleFormAttach 打样准许单-附件
     * @return 结果
     */
    public int changeDevMakeSampleFormAttach(DevMakeSampleFormAttach devMakeSampleFormAttach);

    /**
     * 批量删除打样准许单-附件
     *
     * @param attachmentSids 需要删除的打样准许单-附件ID
     * @return 结果
     */
    public int deleteDevMakeSampleFormAttachByIds(List<Long> attachmentSids);

}
