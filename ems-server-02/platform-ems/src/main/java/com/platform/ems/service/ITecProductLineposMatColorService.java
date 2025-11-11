package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecProductLineposMatColor;

import java.util.List;

/**
 * 商品线部位-款色线色Service接口
 *
 * @author linhongwei
 * @date 2021-08-23
 */
public interface ITecProductLineposMatColorService extends IService<TecProductLineposMatColor> {
    /**
     * 查询商品线部位-款色线色
     *
     * @param lineposMatColor 商品线部位-款色线色ID
     * @return 商品线部位-款色线色
     */
    public TecProductLineposMatColor selectTecProductLineposMatColorById(Long lineposMatColor);

    /**
     * 查询商品线部位-款色线色列表
     *
     * @param tecProductLineposMatColor 商品线部位-款色线色
     * @return 商品线部位-款色线色集合
     */
    public List<TecProductLineposMatColor> selectTecProductLineposMatColorList(TecProductLineposMatColor tecProductLineposMatColor);

    /**
     * 新增商品线部位-款色线色
     *
     * @param tecProductLineposMatColor 商品线部位-款色线色
     * @return 结果
     */
    public int insertTecProductLineposMatColor(TecProductLineposMatColor tecProductLineposMatColor);

    /**
     * 修改商品线部位-款色线色
     *
     * @param tecProductLineposMatColor 商品线部位-款色线色
     * @return 结果
     */
    public int updateTecProductLineposMatColor(TecProductLineposMatColor tecProductLineposMatColor);

    /**
     * 变更商品线部位-款色线色
     *
     * @param tecProductLineposMatColor 商品线部位-款色线色
     * @return 结果
     */
    public int changeTecProductLineposMatColor(TecProductLineposMatColor tecProductLineposMatColor);

    /**
     * 批量删除商品线部位-款色线色
     *
     * @param lineposMatColors 需要删除的商品线部位-款色线色ID
     * @return 结果
     */
    public int deleteTecProductLineposMatColorByIds(List<Long> lineposMatColors);

}
