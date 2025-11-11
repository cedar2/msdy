package com.platform.ems.plug.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConMovementTypeMapper;
import com.platform.ems.plug.domain.ConMovementType;
import com.platform.ems.plug.service.IConMovementTypeService;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * 作业类型(移动类型)Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConMovementTypeServiceImpl extends ServiceImpl<ConMovementTypeMapper, ConMovementType> implements IConMovementTypeService {
    @Autowired
    private ConMovementTypeMapper conMovementTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "作业类型(移动类型)";

    /**
     * 查询作业类型(移动类型)
     *
     * @param sid 作业类型(移动类型)ID
     * @return 作业类型(移动类型)
     */
    @Override
    public ConMovementType selectConMovementTypeById(Long sid) {
        ConMovementType conMovementType = conMovementTypeMapper.selectConMovementTypeById(sid);
        MongodbUtil.find(conMovementType);
        return conMovementType;
    }

    /**
     * 查询作业类型(移动类型)
     *
     * @param sid 作业类型(移动类型)ID
     * @return 作业类型(移动类型)
     */
    @Override
    public ConMovementType conMovementTypeById(Long sid) {
        ConMovementType conMovementType = conMovementTypeMapper.conMovementTypeById(sid);
        MongodbUtil.find(conMovementType);
        return conMovementType;
    }

    /**
     * 列表移动类型)
     *
     * @param
     * @return
     */
    @Override
    public List<ConMovementType> getList(ConMovementType movementType) {
        List<ConMovementType> list = conMovementTypeMapper.getList(movementType);
        list = list.stream().collect(
                collectingAndThen(
                        toCollection(() -> new TreeSet<>(Comparator.comparing(ConMovementType::getMovementTypeCode))), ArrayList::new)
        );
        return list;

    }

    /**
     * 查询作业类型(移动类型)列表
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 作业类型(移动类型)
     */
    @Override
    public List<ConMovementType> selectConMovementTypeList(ConMovementType conMovementType) {
        return conMovementTypeMapper.selectConMovementTypeList(conMovementType);
    }


    /**
     * 查询作业类型(移动类型)列表
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 作业类型(移动类型)
     */
    @Override
    public List<ConMovementType> conMovementTypeList(ConMovementType conMovementType) {
        return conMovementTypeMapper.conMovementTypeList(conMovementType);
    }

    /**
     * 新增作业类型(移动类型)
     * 需要注意编码重复校验
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConMovementType(ConMovementType conMovementType) {
        List<ConMovementType> codeList = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
                .eq(ConMovementType::getCode, conMovementType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConMovementType> nameList = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
                .eq(ConMovementType::getName, conMovementType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conMovementTypeMapper.insert(conMovementType);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conMovementType.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改作业类型(移动类型)
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConMovementType(ConMovementType conMovementType) {
        ConMovementType response = conMovementTypeMapper.conMovementTypeById(conMovementType.getSid());
        int row = conMovementTypeMapper.updateById(conMovementType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conMovementType.getSid(), BusinessType.UPDATE.getValue(), response, conMovementType, TITLE);
        }
        return row;
    }

    /**
     * 变更作业类型(移动类型)
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConMovementType(ConMovementType conMovementType) {
        List<ConMovementType> nameList = conMovementTypeMapper.selectList(new QueryWrapper<ConMovementType>().lambda()
                .eq(ConMovementType::getName, conMovementType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conMovementType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conMovementType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConMovementType response = conMovementTypeMapper.conMovementTypeById(conMovementType.getSid());
        int row = conMovementTypeMapper.updateAllById(conMovementType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conMovementType.getSid(), BusinessType.CHANGE.getValue(), response, conMovementType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除作业类型(移动类型)
     *
     * @param sids 需要删除的作业类型(移动类型)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConMovementTypeByIds(List<Long> sids) {
        return conMovementTypeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conMovementType
     * @return
     */
    @Override
    public int changeStatus(ConMovementType conMovementType) {
        int row = 0;
        Long[] sids = conMovementType.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conMovementType.setSid(id);
                row = conMovementTypeMapper.updateById(conMovementType);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conMovementType.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conMovementType.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conMovementType
     * @return
     */
    @Override
    public int check(ConMovementType conMovementType) {
        int row = 0;
        Long[] sids = conMovementType.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conMovementType.setSid(id);
                row = conMovementTypeMapper.updateById(conMovementType);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conMovementType.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


    /**
     * 下拉框列表
     */
    @Override
    public List<ConMovementType> getConMovementTypeList() {
        return conMovementTypeMapper.getConMovementTypeList();
    }

    @Override
    public List<ConMovementType> getMovementList(ConMovementType conMovementType) {
        return conMovementTypeMapper.getMovementList(conMovementType);
    }
}
