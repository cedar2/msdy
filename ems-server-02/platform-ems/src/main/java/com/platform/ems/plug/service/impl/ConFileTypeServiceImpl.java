package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConDataobjectCategory;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConDataobjectCategoryMapper;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import com.platform.ems.plug.service.IConFileTypeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 附件类型Service业务层处理
 *
 * @author chenkw
 * @date 2021-07-05
 */
@Service
@SuppressWarnings("all")
public class ConFileTypeServiceImpl extends ServiceImpl<ConFileTypeMapper, ConFileType> implements IConFileTypeService {
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;
    @Autowired
    private ConDataobjectCategoryMapper conDataobjectCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "附件类型";

    /**
     * 查询附件类型
     *
     * @param sid 附件类型ID
     * @return 附件类型
     */
    @Override
    public ConFileType selectConFileTypeById(Long sid) {
        ConFileType conFileType = conFileTypeMapper.selectConFileTypeById(sid);
        MongodbUtil.find(conFileType);
        return conFileType;
    }

    /**
     * 查询附件类型列表
     *
     * @param conFileType 附件类型
     * @return 附件类型
     */
    @Override
    public List<ConFileType> selectConFileTypeList(ConFileType conFileType) {
        return conFileTypeMapper.selectConFileTypeList(conFileType);
    }

    /**
     * 新增附件类型
     * 需要注意编码重复校验
     *
     * @param conFileType 附件类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConFileType(ConFileType conFileType) {
        List<ConFileType> codeList = conFileTypeMapper.selectList(new QueryWrapper<ConFileType>().lambda()
                .eq(ConFileType::getCode, conFileType.getCode())
                .eq(ConFileType::getDataobjectCategorySid, conFileType.getDataobjectCategorySid()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException("该数据对象类别下已存在相同的附件类型编码");
        }
        List<ConFileType> nameList = conFileTypeMapper.selectList(new QueryWrapper<ConFileType>().lambda()
                .eq(ConFileType::getName, conFileType.getName())
                .eq(ConFileType::getDataobjectCategorySid, conFileType.getDataobjectCategorySid()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException("该数据对象类别下已存在相同的附件类型名称");
        }
        ConDataobjectCategory conDataobjectCategory = conDataobjectCategoryMapper.selectConDataobjectCategoryById(conFileType.getDataobjectCategorySid());
        conFileType.setDataobjectCategoryCode(conDataobjectCategory.getCode());
        setConfirmInfo(conFileType);
        int row = conFileTypeMapper.insert(conFileType);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conFileType.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConFileType o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改附件类型
     *
     * @param conFileType 附件类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConFileType(ConFileType conFileType) {
        List<ConFileType> nameList = conFileTypeMapper.selectList(new QueryWrapper<ConFileType>().lambda()
                .eq(ConFileType::getName, conFileType.getName())
                .eq(ConFileType::getDataobjectCategorySid, conFileType.getDataobjectCategorySid()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!conFileType.getSid().equals(o.getSid())) {
                    throw new BaseException("该数据对象类别下已存在相同的附件类型名称");
                }
            });
        }
        ConDataobjectCategory conDataobjectCategory = conDataobjectCategoryMapper.selectConDataobjectCategoryById(conFileType.getDataobjectCategorySid());
        conFileType.setDataobjectCategoryCode(conDataobjectCategory.getCode());
        setConfirmInfo(conFileType);
        ConFileType response = conFileTypeMapper.selectConFileTypeById(conFileType.getSid());
        int row = conFileTypeMapper.updateById(conFileType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conFileType.getSid(), BusinessType.UPDATE.getValue(), response, conFileType, TITLE);
        }
        return row;
    }

    /**
     * 变更附件类型
     *
     * @param conFileType 附件类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConFileType(ConFileType conFileType) {
        List<ConFileType> nameList = conFileTypeMapper.selectList(new QueryWrapper<ConFileType>().lambda()
                .eq(ConFileType::getName, conFileType.getName())
                .eq(ConFileType::getDataobjectCategorySid, conFileType.getDataobjectCategorySid()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!conFileType.getSid().equals(o.getSid())) {
                    throw new BaseException("该数据对象类别下已存在相同的附件类型名称");
                }
            });
        }
        ConDataobjectCategory conDataobjectCategory = conDataobjectCategoryMapper.selectConDataobjectCategoryById(conFileType.getDataobjectCategorySid());
        conFileType.setDataobjectCategoryCode(conDataobjectCategory.getCode());
        setConfirmInfo(conFileType);
        ConFileType response = conFileTypeMapper.selectConFileTypeById(conFileType.getSid());
        int row = conFileTypeMapper.updateAllById(conFileType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conFileType.getSid(), BusinessType.CHANGE.getValue(), response, conFileType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除附件类型
     *
     * @param sids 需要删除的附件类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConFileTypeByIds(List<Long> sids) {
        return conFileTypeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conFileType
     * @return
     */
    @Override
    public int changeStatus(ConFileType conFileType) {
        int row = 0;
        Long[] sids = conFileType.getSidList();
        if (sids != null && sids.length > 0) {
            row = conFileTypeMapper.update(null, new UpdateWrapper<ConFileType>().lambda().set(ConFileType::getStatus, conFileType.getStatus())
                    .in(ConFileType::getSid, sids));
            for (Long id : sids) {
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conFileType.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conFileType.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conFileType
     * @return
     */
    @Override
    public int check(ConFileType conFileType) {
        int row = 0;
        Long[] sids = conFileType.getSidList();
        if (sids != null && sids.length > 0) {
            row = conFileTypeMapper.update(null, new UpdateWrapper<ConFileType>().lambda().set(ConFileType::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConFileType::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 下拉框列表
     */
    @Override
    public List<ConFileType> getConFileTypeList() {
        return conFileTypeMapper.getConFileTypeList();
    }
}
