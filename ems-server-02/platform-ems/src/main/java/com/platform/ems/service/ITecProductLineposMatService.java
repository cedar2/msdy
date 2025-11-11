package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecProductLineposMat;

import java.util.List;

/**
 * 商品线部位-线料Service接口
 *
 * @author linhongwei
 * @date 2021-10-21
 */
public interface ITecProductLineposMatService extends IService<TecProductLineposMat> {
    /**
     * 查询商品线部位-线料
     *
     * @param lineposMatSid 商品线部位-线料ID
     * @return 商品线部位-线料
     */
    public TecProductLineposMat selectTecProductLineposMatById(Long lineposMatSid);

    /**
     * 查询商品线部位-线料列表
     *
     * @param tecProductLineposMat 商品线部位-线料
     * @return 商品线部位-线料集合
     */
    public List<TecProductLineposMat> selectTecProductLineposMatList(TecProductLineposMat tecProductLineposMat);

    /**
     * 新增商品线部位-线料
     *
     * @param tecProductLineposMat 商品线部位-线料
     * @return 结果
     */
    public int insertTecProductLineposMat(TecProductLineposMat tecProductLineposMat);

    /**
     * 修改商品线部位-线料
     *
     * @param tecProductLineposMat 商品线部位-线料
     * @return 结果
     */
    public int updateTecProductLineposMat(TecProductLineposMat tecProductLineposMat);

    /**
     * 变更商品线部位-线料
     *
     * @param tecProductLineposMat 商品线部位-线料
     * @return 结果
     */
    public int changeTecProductLineposMat(TecProductLineposMat tecProductLineposMat);

    /**
     * 批量删除商品线部位-线料
     *
     * @param lineposMatSids 需要删除的商品线部位-线料ID
     * @return 结果
     */
    public int deleteTecProductLineposMatByIds(List<Long> lineposMatSids);

}
