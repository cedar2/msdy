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
import com.platform.ems.plug.domain.ConMaterialType;
import com.platform.ems.plug.mapper.ConMaterialTypeMapper;
import com.platform.ems.plug.service.IConMaterialTypeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 物料类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConMaterialTypeServiceImpl extends ServiceImpl<ConMaterialTypeMapper, ConMaterialType> implements IConMaterialTypeService {
    @Autowired
    private ConMaterialTypeMapper conMaterialTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "物料类型";

    /**
     * 查询物料类型
     *
     * @param sid 物料类型ID
     * @return 物料类型
     */
    @Override
    public ConMaterialType selectConMaterialTypeById(Long sid) {
        ConMaterialType conMaterialType = conMaterialTypeMapper.selectConMaterialTypeById(sid);
        MongodbUtil.find(conMaterialType);
        return conMaterialType;
    }

    /**
     * 查询物料类型列表
     *
     * @param conMaterialType 物料类型
     * @return 物料类型
     */
    @Override
    public List<ConMaterialType> selectConMaterialTypeList(ConMaterialType conMaterialType) {
        return conMaterialTypeMapper.selectConMaterialTypeList(conMaterialType);
    }

    /**
     * 新增物料类型
     * 需要注意编码重复校验
     *
     * @param conMaterialType 物料类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConMaterialType(ConMaterialType conMaterialType) {
        List<ConMaterialType> codeList = conMaterialTypeMapper.selectList(new QueryWrapper<ConMaterialType>().lambda()
                .eq(ConMaterialType::getCode, conMaterialType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConMaterialType> nameList = conMaterialTypeMapper.selectList(new QueryWrapper<ConMaterialType>().lambda()
                .eq(ConMaterialType::getName, conMaterialType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conMaterialTypeMapper.insert(conMaterialType);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conMaterialType.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改物料类型
     *
     * @param conMaterialType 物料类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConMaterialType(ConMaterialType conMaterialType) {
        ConMaterialType response = conMaterialTypeMapper.selectConMaterialTypeById(conMaterialType.getSid());
        int row = conMaterialTypeMapper.updateById(conMaterialType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conMaterialType.getSid(), BusinessType.UPDATE.getValue(), response, conMaterialType, TITLE);
        }
        return row;
    }

    /**
     * 变更物料类型
     *
     * @param conMaterialType 物料类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConMaterialType(ConMaterialType conMaterialType) {
        List<ConMaterialType> nameList = conMaterialTypeMapper.selectList(new QueryWrapper<ConMaterialType>().lambda()
                .eq(ConMaterialType::getName, conMaterialType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conMaterialType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conMaterialType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConMaterialType response = conMaterialTypeMapper.selectConMaterialTypeById(conMaterialType.getSid());
        int row = conMaterialTypeMapper.updateAllById(conMaterialType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conMaterialType.getSid(), BusinessType.CHANGE.getValue(), response, conMaterialType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除物料类型
     *
     * @param sids 需要删除的物料类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConMaterialTypeByIds(List<Long> sids) {
        return conMaterialTypeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conMaterialType
     * @return
     */
    @Override
    public int changeStatus(ConMaterialType conMaterialType) {
        int row = 0;
        Long[] sids = conMaterialType.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conMaterialType.setSid(id);
                row = conMaterialTypeMapper.updateById(conMaterialType);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conMaterialType.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conMaterialType.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conMaterialType
     * @return
     */
    @Override
    public int check(ConMaterialType conMaterialType) {
        int row = 0;
        Long[] sids = conMaterialType.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conMaterialType.setSid(id);
                row = conMaterialTypeMapper.updateById(conMaterialType);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conMaterialType.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConMaterialType> getConMaterialTypeList() {
        ConMaterialType materialType = new ConMaterialType();
        materialType.setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return conMaterialTypeMapper.getList(materialType);
    }

    //获取下拉框
    @Override
    public List<ConMaterialType> getList(ConMaterialType conMaterialType) {
        return conMaterialTypeMapper.getList(conMaterialType);
    }
}
