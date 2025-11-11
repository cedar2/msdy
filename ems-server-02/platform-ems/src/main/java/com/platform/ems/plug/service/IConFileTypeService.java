package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConFileType;

import java.util.List;

/**
 * 附件类型Service接口
 *
 * @author chenkw
 * @date 2021-07-05
 */
public interface IConFileTypeService extends IService<ConFileType> {
    /**
     * 查询附件类型
     *
     * @param sid 附件类型ID
     * @return 附件类型
     */
    public ConFileType selectConFileTypeById(Long sid);

    /**
     * 查询附件类型列表
     *
     * @param conFileType 附件类型
     * @return 附件类型集合
     */
    public List<ConFileType> selectConFileTypeList(ConFileType conFileType);

    /**
     * 新增附件类型
     *
     * @param conFileType 附件类型
     * @return 结果
     */
    public int insertConFileType(ConFileType conFileType);

    /**
     * 修改附件类型
     *
     * @param conFileType 附件类型
     * @return 结果
     */
    public int updateConFileType(ConFileType conFileType);

    /**
     * 变更附件类型
     *
     * @param conFileType 附件类型
     * @return 结果
     */
    public int changeConFileType(ConFileType conFileType);

    /**
     * 批量删除附件类型
     *
     * @param sids 需要删除的附件类型ID
     * @return 结果
     */
    public int deleteConFileTypeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conFileType
     * @return
     */
    int changeStatus(ConFileType conFileType);

    /**
     * 更改确认状态
     *
     * @param conFileType
     * @return
     */
    int check(ConFileType conFileType);

    /**
     * 款项类别下拉框列表
     */
    List<ConFileType> getConFileTypeList();
}
