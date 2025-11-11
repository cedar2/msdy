package com.platform.ems.service.impl;

import com.platform.ems.domain.TecMaterialPosInfor;
import com.platform.ems.mapper.TecMaterialPosInforMapper;
import com.platform.ems.service.ITecMaterialPosInforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品尺寸-部位Service业务层处理
 *
 * @author olive
 * @date 2021-02-21
 */
@Service
@SuppressWarnings("all")
public class TecMaterialPosInforServiceImpl implements ITecMaterialPosInforService {
    @Autowired
    private TecMaterialPosInforMapper tecMaterialPosInforMapper;

    /**
     * 查询商品尺寸-部位
     *
     * @param clientId 商品尺寸-部位ID
     * @return 商品尺寸-部位
     */
    @Override
    public TecMaterialPosInfor selectTecMaterialPosInforById(Long clientId) {
        return tecMaterialPosInforMapper.selectTecMaterialPosInforById(clientId);
    }

    /**
     * 查询商品尺寸-部位列表
     *
     * @param tecMaterialPosInfor 商品尺寸-部位
     * @return 商品尺寸-部位
     */
    @Override
    public List<TecMaterialPosInfor> selectTecMaterialPosInforList(TecMaterialPosInfor tecMaterialPosInfor) {
        return tecMaterialPosInforMapper.selectTecMaterialPosInforList(tecMaterialPosInfor);
    }

    /**
     * 新增商品尺寸-部位
     *
     * @param tecMaterialPosInfor 商品尺寸-部位
     * @return 结果
     */
    @Override
    public int insertTecMaterialPosInfor(TecMaterialPosInfor tecMaterialPosInfor) {
        return tecMaterialPosInforMapper.insert(tecMaterialPosInfor);
    }

    /**
     * 修改商品尺寸-部位
     *
     * @param tecMaterialPosInfor 商品尺寸-部位
     * @return 结果
     */
    @Override
    public int updateTecMaterialPosInfor(TecMaterialPosInfor tecMaterialPosInfor) {
        return tecMaterialPosInforMapper.updateTecMaterialPosInfor(tecMaterialPosInfor);
    }

    /**
     * 批量删除商品尺寸-部位
     *
     * @param clientIds 需要删除的商品尺寸-部位ID
     * @return 结果
     */
    @Override
    public int deleteTecMaterialPosInforByIds(Long[] materialPosInfoSids) {
        return tecMaterialPosInforMapper.deleteTecMaterialPosInforByIds(materialPosInfoSids);
    }

    /**
     * 删除商品尺寸-部位信息
     *
     * @param clientId 商品尺寸-部位ID
     * @return 结果
     */
    @Override
    public int deleteTecMaterialPosInforById(Long clientId) {
        return tecMaterialPosInforMapper.deleteTecMaterialPosInforById(clientId);
    }

    @Override
    public int deleteTecMaterialPosInforBySizeId(List<Long> materialSizeSid) {
        return tecMaterialPosInforMapper.deleteTecMaterialPosInforBySizeId(materialSizeSid);
    }

    @Override
    public   List<Long> selectSidByParentids(Long id){
        return tecMaterialPosInforMapper.selectPosInforIds(id);
    }
}
