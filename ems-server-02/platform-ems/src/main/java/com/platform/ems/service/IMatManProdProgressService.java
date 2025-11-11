package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.MatManProdProgress;

import java.util.List;

public interface IMatManProdProgressService{

    /**
     * 查询商品生产进度报表
     * @param matManProdProgress
     * @return
     */
    List<MatManProdProgress> selectMatManProgressList(MatManProdProgress matManProdProgress);
}
