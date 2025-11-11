package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.TecMaterialPosSizeDownMapper;
import com.platform.ems.domain.TecMaterialPosSizeDown;
import com.platform.ems.service.ITecMaterialPosSizeDownService;

/**
 * 商品尺寸-部位-尺码-尺寸（下装）Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-27
 */
@Service
@SuppressWarnings("all")
public class TecMaterialPosSizeDownServiceImpl extends ServiceImpl<TecMaterialPosSizeDownMapper,TecMaterialPosSizeDown>  implements ITecMaterialPosSizeDownService {
    @Autowired
    private TecMaterialPosSizeDownMapper tecMaterialPosSizeDownMapper;

    /**
     * 查询商品尺寸-部位-尺码-尺寸（下装）
     * 
     * @param materialPosSizeSid 商品尺寸-部位-尺码-尺寸（下装）ID
     * @return 商品尺寸-部位-尺码-尺寸（下装）
     */
    @Override
    public TecMaterialPosSizeDown selectTecMaterialPosSizeDownById(Long materialPosSizeSid) {
        return tecMaterialPosSizeDownMapper.selectTecMaterialPosSizeDownById(materialPosSizeSid);
    }

    /**
     * 查询商品尺寸-部位-尺码-尺寸（下装）列表
     * 
     * @param tecMaterialPosSizeDown 商品尺寸-部位-尺码-尺寸（下装）
     * @return 商品尺寸-部位-尺码-尺寸（下装）
     */
    @Override
    public List<TecMaterialPosSizeDown> selectTecMaterialPosSizeDownList(TecMaterialPosSizeDown tecMaterialPosSizeDown) {
        return tecMaterialPosSizeDownMapper.selectTecMaterialPosSizeDownList(tecMaterialPosSizeDown);
    }

    /**
     * 新增商品尺寸-部位-尺码-尺寸（下装）
     * 需要注意编码重复校验
     * @param tecMaterialPosSizeDown 商品尺寸-部位-尺码-尺寸（下装）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecMaterialPosSizeDown(TecMaterialPosSizeDown tecMaterialPosSizeDown) {
        return tecMaterialPosSizeDownMapper.insert(tecMaterialPosSizeDown);
    }

    /**
     * 修改商品尺寸-部位-尺码-尺寸（下装）
     * 
     * @param tecMaterialPosSizeDown 商品尺寸-部位-尺码-尺寸（下装）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecMaterialPosSizeDown(TecMaterialPosSizeDown tecMaterialPosSizeDown) {
        return tecMaterialPosSizeDownMapper.updateById(tecMaterialPosSizeDown);
    }

    /**
     * 批量删除商品尺寸-部位-尺码-尺寸（下装）
     * 
     * @param materialPosSizeSids 需要删除的商品尺寸-部位-尺码-尺寸（下装）ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecMaterialPosSizeDownByIds(List<Long> materialPosSizeSids) {
        return tecMaterialPosSizeDownMapper.deleteBatchIds(materialPosSizeSids);
    }


}
