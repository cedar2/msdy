package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConLaborType;
import com.platform.ems.plug.mapper.ConLaborTypeMapper;
import com.platform.ems.plug.service.IConLaborTypeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工价类型Service业务层处理
 *
 * @author chenkw
 * @date 2021-06-10
 */
@Service
@SuppressWarnings("all")
public class ConLaborTypeServiceImpl extends ServiceImpl<ConLaborTypeMapper, ConLaborType> implements IConLaborTypeService {
    @Autowired
    private ConLaborTypeMapper conLaborTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "工价类型";

    /**
     * 查询工价类型
     *
     * @param laborTypeSid 工价类型ID
     * @return 工价类型
     */
    @Override
    public ConLaborType selectConLaborTypeById(Long laborTypeSid) {
        ConLaborType conLaborType = conLaborTypeMapper.selectConLaborTypeById(laborTypeSid);
        MongodbUtil.find(conLaborType);
        return conLaborType;
    }

    /**
     * 查询工价类型列表
     *
     * @param conLaborType 工价类型
     * @return 工价类型
     */
    @Override
    public List<ConLaborType> selectConLaborTypeList(ConLaborType conLaborType) {
        return conLaborTypeMapper.selectConLaborTypeList(conLaborType);
    }

    /**
     * 新增工价类型
     * 需要注意编码重复校验
     *
     * @param conLaborType 工价类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConLaborType(ConLaborType conLaborType) {

        List<ConLaborType> codeList = conLaborTypeMapper.selectList(new QueryWrapper<ConLaborType>().lambda()
                .eq(ConLaborType::getLaborTypeCode, conLaborType.getLaborTypeCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConLaborType> nameList = conLaborTypeMapper.selectList(new QueryWrapper<ConLaborType>().lambda()
                .eq(ConLaborType::getLaborTypeName, conLaborType.getLaborTypeName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        if (ConstantsEms.CHECK_STATUS.equals(conLaborType.getHandleStatus())) {
            conLaborType.setConfirmDate(new Date());
            conLaborType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = conLaborTypeMapper.insert(conLaborType);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conLaborType.getLaborTypeSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改工价类型
     *
     * @param conLaborType 工价类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConLaborType(ConLaborType conLaborType) {
        ConLaborType laborType = conLaborTypeMapper.selectOne(new QueryWrapper<ConLaborType>().lambda()
                .eq(ConLaborType::getLaborTypeName, conLaborType.getLaborTypeName()));
        if (!conLaborType.getLaborTypeSid().equals(laborType.getLaborTypeSid())) {
            throw new CheckedException("工价类型名称已存在！");
        }
        ConLaborType response = conLaborTypeMapper.selectConLaborTypeById(conLaborType.getLaborTypeSid());
        int row = conLaborTypeMapper.updateById(conLaborType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conLaborType.getLaborTypeSid(), BusinessType.UPDATE.getValue(), response, conLaborType, TITLE);
        }
        return row;
    }

    /**
     * 变更工价类型
     *
     * @param conLaborType 工价类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConLaborType(ConLaborType conLaborType) {
        List<ConLaborType> nameList = conLaborTypeMapper.selectList(new QueryWrapper<ConLaborType>().lambda()
                .eq(ConLaborType::getLaborTypeName, conLaborType.getLaborTypeName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getLaborTypeSid().equals(conLaborType.getLaborTypeSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conLaborType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConLaborType response = conLaborTypeMapper.selectConLaborTypeById(conLaborType.getLaborTypeSid());
        int row = conLaborTypeMapper.updateAllById(conLaborType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conLaborType.getLaborTypeSid(), BusinessType.CHANGE.getValue(), response, conLaborType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除工价类型
     *
     * @param laborTypeSids 需要删除的工价类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConLaborTypeByIds(List<Long> laborTypeSids) {
        List<ConLaborType> conLaborTypeList = conLaborTypeMapper.selectList(new QueryWrapper<ConLaborType>().lambda().in(ConLaborType::getLaborTypeSid, laborTypeSids)
                .eq(ConLaborType::getHandleStatus, ConstantsEms.CHECK_STATUS));
        if (conLaborTypeList.size() > 0) {
            throw new BaseException("已确认的数据不可删除");
        }
        return conLaborTypeMapper.deleteBatchIds(laborTypeSids);
    }

    /**
     * 启用/停用
     *
     * @param conLaborType
     * @return
     */
    @Override
    public int changeStatus(ConLaborType conLaborType) {
        int row = 0;
        Long[] sids = conLaborType.getLaborTypeSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conLaborType.setLaborTypeSid(id);
                row = conLaborTypeMapper.updateById(conLaborType);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conLaborType.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conLaborType.getLaborTypeSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conLaborType
     * @return
     */
    @Override
    public int check(ConLaborType conLaborType) {
        int row = 0;
        Long[] sids = conLaborType.getLaborTypeSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conLaborType.setLaborTypeSid(id);
                row = conLaborTypeMapper.updateById(conLaborType);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conLaborType.getLaborTypeSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConLaborType> getConLaborTypeList() {
        return conLaborTypeMapper.getConLaborTypeList();
    }

    @Override
    public String selectConLaborTypeCodeBySid(Long laborTypeSid){
        if (laborTypeSid == null){ return null; }
        return conLaborTypeMapper.selectConLaborTypeCodeBySid(laborTypeSid);
    }

}
