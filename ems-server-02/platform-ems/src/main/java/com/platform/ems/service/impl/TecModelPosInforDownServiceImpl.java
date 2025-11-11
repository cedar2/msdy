package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.TecModelPosInforDownMapper;
import com.platform.ems.domain.TecModelPosInforDown;
import com.platform.ems.service.ITecModelPosInforDownService;

/**
 * 版型-部位信息（下装）Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-25
 */
@Service
@SuppressWarnings("all")
public class TecModelPosInforDownServiceImpl extends ServiceImpl<TecModelPosInforDownMapper,TecModelPosInforDown>  implements ITecModelPosInforDownService {
    @Autowired
    private TecModelPosInforDownMapper tecModelPosInforDownMapper;

    /**
     * 查询版型-部位信息（下装）
     * 
     * @param modelPositionInforSid 版型-部位信息（下装）ID
     * @return 版型-部位信息（下装）
     */
    @Override
    public TecModelPosInforDown selectTecModelPosInforDownById(Long modelPositionInforSid) {
        return tecModelPosInforDownMapper.selectTecModelPosInforDownById(modelPositionInforSid);
    }

    /**
     * 查询版型-部位信息（下装）列表
     * 
     * @param tecModelPosInforDown 版型-部位信息（下装）
     * @return 版型-部位信息（下装）
     */
    @Override
    public List<TecModelPosInforDown> selectTecModelPosInforDownList(TecModelPosInforDown tecModelPosInforDown) {
        return tecModelPosInforDownMapper.selectTecModelPosInforDownList(tecModelPosInforDown);
    }

    /**
     * 新增版型-部位信息（下装）
     * 需要注意编码重复校验
     * @param tecModelPosInforDown 版型-部位信息（下装）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecModelPosInforDown(TecModelPosInforDown tecModelPosInforDown) {
        return tecModelPosInforDownMapper.insert(tecModelPosInforDown);
    }

    /**
     * 修改版型-部位信息（下装）
     * 
     * @param tecModelPosInforDown 版型-部位信息（下装）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecModelPosInforDown(TecModelPosInforDown tecModelPosInforDown) {
        return tecModelPosInforDownMapper.updateById(tecModelPosInforDown);
    }

    /**
     * 批量删除版型-部位信息（下装）
     * 
     * @param modelPositionInforSids 需要删除的版型-部位信息（下装）ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecModelPosInforDownByIds(List<Long> modelPositionInforSids) {
        return tecModelPosInforDownMapper.deleteBatchIds(modelPositionInforSids);
    }


}
