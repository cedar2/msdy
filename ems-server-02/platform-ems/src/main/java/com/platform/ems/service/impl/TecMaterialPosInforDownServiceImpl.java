package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.TecMaterialPosInforDownMapper;
import com.platform.ems.domain.TecMaterialPosInforDown;
import com.platform.ems.service.ITecMaterialPosInforDownService;

/**
 * 商品尺寸-部位（套装的下装）Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-27
 */
@Service
@SuppressWarnings("all")
public class TecMaterialPosInforDownServiceImpl extends ServiceImpl<TecMaterialPosInforDownMapper,TecMaterialPosInforDown>  implements ITecMaterialPosInforDownService {
    @Autowired
    private TecMaterialPosInforDownMapper tecMaterialPosInforDownMapper;

    /**
     * 查询商品尺寸-部位（套装的下装）
     * 
     * @param clientId 商品尺寸-部位（套装的下装）ID
     * @return 商品尺寸-部位（套装的下装）
     */
    @Override
    public TecMaterialPosInforDown selectTecMaterialPosInforDownById(String clientId) {
        return tecMaterialPosInforDownMapper.selectTecMaterialPosInforDownById(clientId);
    }

    /**
     * 查询商品尺寸-部位（套装的下装）列表
     * 
     * @param tecMaterialPosInforDown 商品尺寸-部位（套装的下装）
     * @return 商品尺寸-部位（套装的下装）
     */
    @Override
    public List<TecMaterialPosInforDown> selectTecMaterialPosInforDownList(TecMaterialPosInforDown tecMaterialPosInforDown) {
        return tecMaterialPosInforDownMapper.selectTecMaterialPosInforDownList(tecMaterialPosInforDown);
    }

    /**
     * 新增商品尺寸-部位（套装的下装）
     * 需要注意编码重复校验
     * @param tecMaterialPosInforDown 商品尺寸-部位（套装的下装）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecMaterialPosInforDown(TecMaterialPosInforDown tecMaterialPosInforDown) {
        return tecMaterialPosInforDownMapper.insert(tecMaterialPosInforDown);
    }

    /**
     * 修改商品尺寸-部位（套装的下装）
     * 
     * @param tecMaterialPosInforDown 商品尺寸-部位（套装的下装）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecMaterialPosInforDown(TecMaterialPosInforDown tecMaterialPosInforDown) {
        return tecMaterialPosInforDownMapper.updateById(tecMaterialPosInforDown);
    }

    /**
     * 批量删除商品尺寸-部位（套装的下装）
     * 
     * @param clientIds 需要删除的商品尺寸-部位（套装的下装）ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecMaterialPosInforDownByIds(List<String> clientIds) {
        return tecMaterialPosInforDownMapper.deleteBatchIds(clientIds);
    }


}
