package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBomType;
import com.platform.ems.plug.mapper.ConBomTypeMapper;
import com.platform.ems.plug.service.IConBomTypeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * BOM类型Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBomTypeServiceImpl extends ServiceImpl<ConBomTypeMapper, ConBomType> implements IConBomTypeService {
    @Autowired
    private ConBomTypeMapper conBomTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "BOM类型";

    /**
     * 查询BOM类型
     *
     * @param sid BOM类型ID
     * @return BOM类型
     */
    @Override
    public ConBomType selectConBomTypeById(Long sid) {
        ConBomType conBomType = conBomTypeMapper.selectConBomTypeById(sid);
        MongodbUtil.find(conBomType);
        return conBomType;
    }

    /**
     * 查询BOM类型列表
     *
     * @param conBomType BOM类型
     * @return BOM类型
     */
    @Override
    public List<ConBomType> selectConBomTypeList(ConBomType conBomType) {
        return conBomTypeMapper.selectConBomTypeList(conBomType);
    }

    /**
     * 新增BOM类型
     * 需要注意编码重复校验
     *
     * @param conBomType BOM类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBomType(ConBomType conBomType) {
        List<ConBomType> codeList = conBomTypeMapper.selectList(new QueryWrapper<ConBomType>().lambda()
                .eq(ConBomType::getCode, conBomType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBomType> nameList = conBomTypeMapper.selectList(new QueryWrapper<ConBomType>().lambda()
                .eq(ConBomType::getName, conBomType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conBomTypeMapper.insert(conBomType);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBomType.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改BOM类型
     *
     * @param conBomType BOM类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBomType(ConBomType conBomType) {
        ConBomType response = conBomTypeMapper.selectConBomTypeById(conBomType.getSid());
        int row = conBomTypeMapper.updateById(conBomType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBomType.getSid(), BusinessType.UPDATE.getValue(), response, conBomType, TITLE);
        }
        return row;
    }

    /**
     * 变更BOM类型
     *
     * @param conBomType BOM类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBomType(ConBomType conBomType) {
        List<ConBomType> nameList = conBomTypeMapper.selectList(new QueryWrapper<ConBomType>().lambda()
                .eq(ConBomType::getName, conBomType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conBomType.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBomType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBomType response = conBomTypeMapper.selectConBomTypeById(conBomType.getSid());
        int row = conBomTypeMapper.updateAllById(conBomType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBomType.getSid(), BusinessType.CHANGE.getValue(), response, conBomType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除BOM类型
     *
     * @param sids 需要删除的BOM类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBomTypeByIds(List<Long> sids) {
        return conBomTypeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBomType
     * @return
     */
    @Override
    public int changeStatus(ConBomType conBomType) {
        int row = 0;
        Long[] sids = conBomType.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBomType.setSid(id);
                row = conBomTypeMapper.updateById(conBomType);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBomType.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBomType.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBomType
     * @return
     */
    @Override
    public int check(ConBomType conBomType) {
        int row = 0;
        Long[] sids = conBomType.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBomType.setSid(id);
                row = conBomTypeMapper.updateById(conBomType);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conBomType.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
