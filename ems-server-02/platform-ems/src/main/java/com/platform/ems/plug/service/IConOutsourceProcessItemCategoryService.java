package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConOutsourceProcessItemCategory;
import com.platform.ems.plug.domain.ConDiscountType;

/**
 * 行类别_外发加工发料单/收货单Service接口
 *
 * @author linhongwei
 * @date 2021-06-19
 */
public interface IConOutsourceProcessItemCategoryService extends IService<ConOutsourceProcessItemCategory>{
    /**
     * 查询行类别_外发加工发料单/收货单
     *
     * @param sid 行类别_外发加工发料单/收货单ID
     * @return 行类别_外发加工发料单/收货单
     */
    public ConOutsourceProcessItemCategory selectConOutsourceProcessItemCategoryById(Long sid);

    /**
     * 查询行类别_外发加工发料单/收货单列表
     *
     * @param conOutsourceProcessItemCategory 行类别_外发加工发料单/收货单
     * @return 行类别_外发加工发料单/收货单集合
     */
    public List<ConOutsourceProcessItemCategory> selectConOutsourceProcessItemCategoryList(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory);

    /**
     * 新增行类别_外发加工发料单/收货单
     *
     * @param conOutsourceProcessItemCategory 行类别_外发加工发料单/收货单
     * @return 结果
     */
    public int insertConOutsourceProcessItemCategory(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory);

    /**
     * 修改行类别_外发加工发料单/收货单
     *
     * @param conOutsourceProcessItemCategory 行类别_外发加工发料单/收货单
     * @return 结果
     */
    public int updateConOutsourceProcessItemCategory(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory);

    /**
     * 变更行类别_外发加工发料单/收货单
     *
     * @param conOutsourceProcessItemCategory 行类别_外发加工发料单/收货单
     * @return 结果
     */
    public int changeConOutsourceProcessItemCategory(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory);

    /**
     * 批量删除行类别_外发加工发料单/收货单
     *
     * @param sids 需要删除的行类别_外发加工发料单/收货单ID
     * @return 结果
     */
    public int deleteConOutsourceProcessItemCategoryByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conOutsourceProcessItemCategory
    * @return
    */
    int changeStatus(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory);

    /**
     * 更改确认状态
     * @param conOutsourceProcessItemCategory
     * @return
     */
    int check(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory);


    /**  获取下拉列表 */
    List<ConOutsourceProcessItemCategory> getConOutsourceProcessItemCategoryList();
}
