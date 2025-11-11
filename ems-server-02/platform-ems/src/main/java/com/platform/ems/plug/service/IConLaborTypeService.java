package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDiscountType;
import com.platform.ems.plug.domain.ConLaborType;

/**
 * 工价类型Service接口
 *
 * @author chenkw
 * @date 2021-06-10
 */
public interface IConLaborTypeService extends IService<ConLaborType>{
    /**
     * 查询工价类型
     *
     * @param laborTypeSid 工价类型ID
     * @return 工价类型
     */
    public ConLaborType selectConLaborTypeById(Long laborTypeSid);

    /**
     * 查询工价类型列表
     *
     * @param conLaborType 工价类型
     * @return 工价类型集合
     */
    public List<ConLaborType> selectConLaborTypeList(ConLaborType conLaborType);

    /**
     * 新增工价类型
     *
     * @param conLaborType 工价类型
     * @return 结果
     */
    public int insertConLaborType(ConLaborType conLaborType);

    /**
     * 修改工价类型
     *
     * @param conLaborType 工价类型
     * @return 结果
     */
    public int updateConLaborType(ConLaborType conLaborType);

    /**
     * 变更工价类型
     *
     * @param conLaborType 工价类型
     * @return 结果
     */
    public int changeConLaborType(ConLaborType conLaborType);

    /**
     * 批量删除工价类型
     *
     * @param laborTypeSids 需要删除的工价类型ID
     * @return 结果
     */
    public int deleteConLaborTypeByIds(List<Long> laborTypeSids);

    /**
    * 启用/停用
    * @param conLaborType
    * @return
    */
    int changeStatus(ConLaborType conLaborType);

    /**
     * 更改确认状态
     * @param conLaborType
     * @return
     */
    int check(ConLaborType conLaborType);

    /**  获取下拉列表 */
    List<ConLaborType> getConLaborTypeList();

    /**
     * 根据sid 查编码
     * @param laborTypeSid
     * @return
     */
    String selectConLaborTypeCodeBySid(Long laborTypeSid);

}
