package com.platform.ems.mapper;

import com.platform.ems.domain.MatManProdProgress;

import java.util.List;

public interface MatManProdProgressMapper {
    /**
     * 查询商品生产进度报表
     * @param matManProdProgress
     * @return
     */
    List<MatManProdProgress> selectMatManProgressList(MatManProdProgress matManProdProgress);
}
