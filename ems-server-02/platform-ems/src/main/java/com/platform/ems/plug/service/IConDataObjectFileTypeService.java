package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDataObjectFileType;

/**
 * 数据对象&附件类型对照Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConDataObjectFileTypeService extends IService<ConDataObjectFileType>{
    /**
     * 查询数据对象&附件类型对照
     * 
     * @param sid 数据对象&附件类型对照ID
     * @return 数据对象&附件类型对照
     */
    public ConDataObjectFileType selectConDataObjectFileTypeById(Long sid);

    /**
     * 查询数据对象&附件类型对照列表
     * 
     * @param conDataObjectFileType 数据对象&附件类型对照
     * @return 数据对象&附件类型对照集合
     */
    public List<ConDataObjectFileType> selectConDataObjectFileTypeList(ConDataObjectFileType conDataObjectFileType);

    /**
     * 新增数据对象&附件类型对照
     * 
     * @param conDataObjectFileType 数据对象&附件类型对照
     * @return 结果
     */
    public int insertConDataObjectFileType(ConDataObjectFileType conDataObjectFileType);

    /**
     * 修改数据对象&附件类型对照
     * 
     * @param conDataObjectFileType 数据对象&附件类型对照
     * @return 结果
     */
    public int updateConDataObjectFileType(ConDataObjectFileType conDataObjectFileType);

    /**
     * 变更数据对象&附件类型对照
     *
     * @param conDataObjectFileType 数据对象&附件类型对照
     * @return 结果
     */
    public int changeConDataObjectFileType(ConDataObjectFileType conDataObjectFileType);

    /**
     * 批量删除数据对象&附件类型对照
     * 
     * @param sids 需要删除的数据对象&附件类型对照ID
     * @return 结果
     */
    public int deleteConDataObjectFileTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conDataObjectFileType
    * @return
    */
    int changeStatus(ConDataObjectFileType conDataObjectFileType);

    /**
     * 更改确认状态
     * @param conDataObjectFileType
     * @return
     */
    int check(ConDataObjectFileType conDataObjectFileType);

}
