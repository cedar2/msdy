package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDiscountType;

/**
 * 折扣类型Service接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface IConDiscountTypeService extends IService<ConDiscountType>{
    /**
     * 查询折扣类型
     *
     * @param sid 折扣类型ID
     * @return 折扣类型
     */
    public ConDiscountType selectConDiscountTypeById(Long sid);

    /**
     * 查询折扣类型列表
     *
     * @param conDiscountType 折扣类型
     * @return 折扣类型集合
     */
    public List<ConDiscountType> selectConDiscountTypeList(ConDiscountType conDiscountType);

    /**
     * 新增折扣类型
     *
     * @param conDiscountType 折扣类型
     * @return 结果
     */
    public int insertConDiscountType(ConDiscountType conDiscountType);

    /**
     * 修改折扣类型
     *
     * @param conDiscountType 折扣类型
     * @return 结果
     */
    public int updateConDiscountType(ConDiscountType conDiscountType);

    /**
     * 变更折扣类型
     *
     * @param conDiscountType 折扣类型
     * @return 结果
     */
    public int changeConDiscountType(ConDiscountType conDiscountType);

    /**
     * 批量删除折扣类型
     *
     * @param sids 需要删除的折扣类型ID
     * @return 结果
     */
    public int deleteConDiscountTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conDiscountType
    * @return
    */
    int changeStatus(ConDiscountType conDiscountType);

    /**
     * 更改确认状态
     * @param conDiscountType
     * @return
     */
    int check(ConDiscountType conDiscountType);

    /**  获取下拉列表 */
    List<ConDiscountType> getConDiscountTypeList();
}
