package com.platform.ems.service.impl;


import com.platform.ems.domain.MatManProdProgress;
import com.platform.ems.mapper.MatManProdProgressMapper;
import com.platform.ems.service.IMatManProdProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("all")
public class MatManProdProgressServiceImpl implements IMatManProdProgressService {

    @Autowired
    private MatManProdProgressMapper matManProdProgressMapper;

    @Override
    public List<MatManProdProgress> selectMatManProgressList(MatManProdProgress matManProdProgress) {
        List<MatManProdProgress> list = matManProdProgressMapper.selectMatManProgressList(matManProdProgress);
        return list;
    }
}
