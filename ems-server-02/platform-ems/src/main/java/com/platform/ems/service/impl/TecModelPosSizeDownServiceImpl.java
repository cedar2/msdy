package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.TecModelPosSizeDownMapper;
import com.platform.ems.domain.TecModelPosSizeDown;
import com.platform.ems.service.ITecModelPosSizeDownService;

/**
 * 版型-部位-尺码-尺寸（下装）Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-25
 */
@Service
@SuppressWarnings("all")
public class TecModelPosSizeDownServiceImpl extends ServiceImpl<TecModelPosSizeDownMapper,TecModelPosSizeDown>  implements ITecModelPosSizeDownService {
    @Autowired
    private TecModelPosSizeDownMapper tecModelPosSizeDownMapper;

    /**
     * 查询版型-部位-尺码-尺寸（下装）
     * 
     * @param modelPositionSizeSid 版型-部位-尺码-尺寸（下装）ID
     * @return 版型-部位-尺码-尺寸（下装）
     */
    @Override
    public TecModelPosSizeDown selectTecModelPosSizeDownById(Long modelPositionSizeSid) {
        return tecModelPosSizeDownMapper.selectTecModelPosSizeDownById(modelPositionSizeSid);
    }

    /**
     * 查询版型-部位-尺码-尺寸（下装）列表
     * 
     * @param tecModelPosSizeDown 版型-部位-尺码-尺寸（下装）
     * @return 版型-部位-尺码-尺寸（下装）
     */
    @Override
    public List<TecModelPosSizeDown> selectTecModelPosSizeDownList(TecModelPosSizeDown tecModelPosSizeDown) {
        return tecModelPosSizeDownMapper.selectTecModelPosSizeDownList(tecModelPosSizeDown);
    }

    /**
     * 新增版型-部位-尺码-尺寸（下装）
     * 需要注意编码重复校验
     * @param tecModelPosSizeDown 版型-部位-尺码-尺寸（下装）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecModelPosSizeDown(TecModelPosSizeDown tecModelPosSizeDown) {
        return tecModelPosSizeDownMapper.insert(tecModelPosSizeDown);
    }

    /**
     * 修改版型-部位-尺码-尺寸（下装）
     * 
     * @param tecModelPosSizeDown 版型-部位-尺码-尺寸（下装）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecModelPosSizeDown(TecModelPosSizeDown tecModelPosSizeDown) {
        return tecModelPosSizeDownMapper.updateById(tecModelPosSizeDown);
    }

    /**
     * 批量删除版型-部位-尺码-尺寸（下装）
     * 
     * @param modelPositionSizeSids 需要删除的版型-部位-尺码-尺寸（下装）ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecModelPosSizeDownByIds(List<Long> modelPositionSizeSids) {
        return tecModelPosSizeDownMapper.deleteBatchIds(modelPositionSizeSids);
    }


}
