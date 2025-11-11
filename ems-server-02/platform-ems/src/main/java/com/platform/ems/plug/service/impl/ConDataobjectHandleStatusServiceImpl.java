package com.platform.ems.plug.service.impl;

import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConDataobjectHandleStatusMapper;
import com.platform.ems.plug.domain.ConDataobjectHandleStatus;
import com.platform.ems.plug.service.IConDataobjectHandleStatusService;

/**
 * 数据对象类别与处理状态Service业务层处理
 *
 * @author linhongwei
 * @date 2022-06-23
 */
@Service
@SuppressWarnings("all")
public class ConDataobjectHandleStatusServiceImpl extends ServiceImpl<ConDataobjectHandleStatusMapper, ConDataobjectHandleStatus> implements IConDataobjectHandleStatusService {
    @Autowired
    private ConDataobjectHandleStatusMapper conDataobjectHandleStatusMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "数据对象类别与处理状态";

    /**
     * 查询数据对象类别与处理状态
     *
     * @param sid 数据对象类别与处理状态ID
     * @return 数据对象类别与处理状态
     */
    @Override
    public ConDataobjectHandleStatus selectConDataobjectHandleStatusById(Long sid) {
        ConDataobjectHandleStatus conDataobjectHandleStatus = conDataobjectHandleStatusMapper.selectConDataobjectHandleStatusById(sid);
        MongodbUtil.find(conDataobjectHandleStatus);
        return conDataobjectHandleStatus;
    }

    /**
     * 查询数据对象类别与处理状态列表
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 数据对象类别与处理状态
     */
    @Override
    public List<ConDataobjectHandleStatus> selectConDataobjectHandleStatusList(ConDataobjectHandleStatus conDataobjectHandleStatus) {
        if (StrUtil.isNotEmpty(conDataobjectHandleStatus.getDataobjectHandleStatus())) {
            conDataobjectHandleStatus.setDataobjectHandleStatus(conDataobjectHandleStatus.getDataobjectHandleStatus() + ";");
        }
        return conDataobjectHandleStatusMapper.selectConDataobjectHandleStatusList(conDataobjectHandleStatus);
    }

    /**
     * 查询数据对象类别与处理状态分组按数据对象类别
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 数据对象类别与处理状态
     */
    @Override
    public List<ConDataobjectHandleStatus> selectConDataobjectHandleStatusGroup(ConDataobjectHandleStatus conDataobjectHandleStatus) {
        return conDataobjectHandleStatusMapper.selectConDataobjectHandleStatusGroup(conDataobjectHandleStatus);
    }

    /*
        唯一性校验
     */
    private void checkUnique(ConDataobjectHandleStatus conDataobjectHandleStatus){
        QueryWrapper<ConDataobjectHandleStatus> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConDataobjectHandleStatus::getDataobjectCategorySid,conDataobjectHandleStatus.getDataobjectCategorySid());
//        queryWrapper.lambda().eq(ConDataobjectHandleStatus::getDataobjectHandleStatus,conDataobjectHandleStatus.getDataobjectHandleStatus());
        if (conDataobjectHandleStatus.getSid() != null){
            queryWrapper.lambda().ne(ConDataobjectHandleStatus::getSid,conDataobjectHandleStatus.getSid());
        }
        List<ConDataobjectHandleStatus> list = conDataobjectHandleStatusMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0){
            throw new BaseException("该数据对象类别已存在，请检查！");
        }
    }

    /**
     * 新增数据对象类别与处理状态
     * 需要注意编码重复校验
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDataobjectHandleStatus(ConDataobjectHandleStatus conDataobjectHandleStatus) {
        checkUnique(conDataobjectHandleStatus);
        String[] dataobjectHandleStatusArray = conDataobjectHandleStatus.getDataobjectHandleStatusList();
        if (dataobjectHandleStatusArray == null) {
            throw new CheckedException("对应处理状态不能为空");
        }
        String handleStatus = "";
        for (String str : dataobjectHandleStatusArray) {
            handleStatus += str + ";";
        }
//        String substring = handleStatus.substring(0, handleStatus.lastIndexOf(";"));
        conDataobjectHandleStatus.setDataobjectHandleStatus(handleStatus);
        int row = conDataobjectHandleStatusMapper.insert(conDataobjectHandleStatus);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDataobjectHandleStatus.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改数据对象类别与处理状态
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDataobjectHandleStatus(ConDataobjectHandleStatus conDataobjectHandleStatus) {
        checkUnique(conDataobjectHandleStatus);
        ConDataobjectHandleStatus response = conDataobjectHandleStatusMapper.selectConDataobjectHandleStatusById(conDataobjectHandleStatus.getSid());
        int row = conDataobjectHandleStatusMapper.updateById(conDataobjectHandleStatus);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDataobjectHandleStatus.getSid(), BusinessType.UPDATE.getValue(), response, conDataobjectHandleStatus, TITLE);
        }
        return row;
    }

    /**
     * 变更数据对象类别与处理状态
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDataobjectHandleStatus(ConDataobjectHandleStatus conDataobjectHandleStatus) {
        checkUnique(conDataobjectHandleStatus);
        ConDataobjectHandleStatus response = conDataobjectHandleStatusMapper.selectConDataobjectHandleStatusById(conDataobjectHandleStatus.getSid());

        String[] dataobjectHandleStatusArray = conDataobjectHandleStatus.getDataobjectHandleStatusList();
        if (dataobjectHandleStatusArray == null || dataobjectHandleStatusArray.length == 0) {
            throw new CheckedException("对应处理状态不能为空");
        }
        String handleStatus = "";
        for (String str : dataobjectHandleStatusArray) {
            handleStatus += str + ";";
        }
        conDataobjectHandleStatus.setDataobjectHandleStatus(handleStatus);

        int row = conDataobjectHandleStatusMapper.updateAllById(conDataobjectHandleStatus);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDataobjectHandleStatus.getSid(), BusinessType.CHANGE.getValue(), response, conDataobjectHandleStatus, TITLE);
        }
        return row;
    }

    /**
     * 批量删除数据对象类别与处理状态
     *
     * @param sids 需要删除的数据对象类别与处理状态ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDataobjectHandleStatusByIds(List<Long> sids) {
        return conDataobjectHandleStatusMapper.deleteBatchIds(sids);
    }

    /**
     * 获取下拉框
     *
     * @param conDataobjectHandleStatus 数据对象类别与处理状态
     * @return 数据对象类别与处理状态
     */
    @Override
    public List<ConDataobjectHandleStatus> getList(ConDataobjectHandleStatus conDataobjectHandleStatus) {
        return conDataobjectHandleStatusMapper.selectConDataobjectHandleStatusList(conDataobjectHandleStatus);
    }

}
