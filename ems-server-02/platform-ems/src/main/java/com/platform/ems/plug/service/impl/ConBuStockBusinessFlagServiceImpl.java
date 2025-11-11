package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBuStockBusinessFlag;
import com.platform.ems.plug.mapper.ConBuStockBusinessFlagMapper;
import com.platform.ems.plug.service.IConBuStockBusinessFlagService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务标识_其它出入库Service业务层处理
 *
 * @author wangp
 * @date 2022-10-09
 */
@Service
@SuppressWarnings("all")
public class ConBuStockBusinessFlagServiceImpl extends ServiceImpl<ConBuStockBusinessFlagMapper, ConBuStockBusinessFlag> implements IConBuStockBusinessFlagService {
    @Autowired
    private ConBuStockBusinessFlagMapper conBuStockBusinessFlagMapper;

    private static final String TITLE = "业务标识_其它出入库";

    /**
     * 查询业务标识_其它出入库
     *
     * @param sid 业务标识_其它出入库ID
     * @return 业务标识_其它出入库
     */
    @Override
    public ConBuStockBusinessFlag selectConBuStockBusinessFlagById(Long sid) {
        ConBuStockBusinessFlag conBuStockBusinessFlag = conBuStockBusinessFlagMapper.selectConBuStockBusinessFlagById(sid);
        MongodbUtil.find(conBuStockBusinessFlag);
        return conBuStockBusinessFlag;
    }

    /**
     * 查询业务标识_其它出入库列表
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 业务标识_其它出入库
     */
    @Override
    public List<ConBuStockBusinessFlag> selectConBuStockBusinessFlagList(ConBuStockBusinessFlag conBuStockBusinessFlag) {
        return conBuStockBusinessFlagMapper.selectConBuStockBusinessFlagList(conBuStockBusinessFlag);
    }

    /**
     * 业务标识_其它出入库列表下拉框
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 业务标识_其它出入库集合
     */
    public List<ConBuStockBusinessFlag> getConBuStockBusinessFlagList(ConBuStockBusinessFlag conBuStockBusinessFlag) {
        return conBuStockBusinessFlagMapper.getConBuStockBusinessFlagList(conBuStockBusinessFlag);
    }

    /**
     * 新增业务标识_其它出入库
     * 需要注意编码重复校验
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuStockBusinessFlag(ConBuStockBusinessFlag conBuStockBusinessFlag) {

        List<ConBuStockBusinessFlag> checkCode = conBuStockBusinessFlagMapper.selectList(new QueryWrapper<ConBuStockBusinessFlag>().lambda()
                .eq(ConBuStockBusinessFlag::getCode, conBuStockBusinessFlag.getCode()));
        if (CollectionUtil.isNotEmpty(checkCode)) {
            throw new CustomException("业务标识编码已存在，请核实！");
        }

        List<ConBuStockBusinessFlag> checkName = conBuStockBusinessFlagMapper.selectList(new QueryWrapper<ConBuStockBusinessFlag>().lambda()
                .eq(ConBuStockBusinessFlag::getName, conBuStockBusinessFlag.getName()));
        if (CollectionUtil.isNotEmpty(checkName)) {
            throw new CustomException("业务标识名称已存在，请核实！");
        }

        //新增多个 | 现在用不到

//        StringBuilder sBuilder = new StringBuilder();
//
//        for (String docCat : conBuStockBusinessFlag.getDocumentCategoryList()) {
//            sBuilder.append(docCat).append(";");
//        }
//
//        conBuStockBusinessFlag.setDocumentCategory(sBuilder.toString());

        int row = conBuStockBusinessFlagMapper.insert(conBuStockBusinessFlag);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ConBuStockBusinessFlag(), conBuStockBusinessFlag);
            MongodbDeal.insert(conBuStockBusinessFlag.getSid(), conBuStockBusinessFlag.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改业务标识_其它出入库
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuStockBusinessFlag(ConBuStockBusinessFlag conBuStockBusinessFlag) {
        ConBuStockBusinessFlag original = conBuStockBusinessFlagMapper.selectConBuStockBusinessFlagById(conBuStockBusinessFlag.getSid());
        int row = conBuStockBusinessFlagMapper.updateById(conBuStockBusinessFlag);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, conBuStockBusinessFlag);
            MongodbDeal.update(conBuStockBusinessFlag.getSid(), original.getHandleStatus(), conBuStockBusinessFlag.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更业务标识_其它出入库
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuStockBusinessFlag(ConBuStockBusinessFlag conBuStockBusinessFlag) {

        List<ConBuStockBusinessFlag> checkName = conBuStockBusinessFlagMapper.selectList(new QueryWrapper<ConBuStockBusinessFlag>().lambda()
            .eq(ConBuStockBusinessFlag::getName, conBuStockBusinessFlag.getName()));
        if (CollectionUtil.isNotEmpty(checkName) && !checkName.get(0).getSid().equals(conBuStockBusinessFlag.getSid())) {
            throw new CustomException("业务标识名称已存在，请核实！");
        }
        else{
            conBuStockBusinessFlag.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());

            ConBuStockBusinessFlag response = conBuStockBusinessFlagMapper.selectConBuStockBusinessFlagById(conBuStockBusinessFlag.getSid());
            int row = conBuStockBusinessFlagMapper.updateAllById(conBuStockBusinessFlag);
            if (row > 0) {
                //插入日志
                MongodbUtil.insertUserLog(conBuStockBusinessFlag.getSid(), BusinessType.CHANGE.getValue(), response, conBuStockBusinessFlag, TITLE);
            }
            return row;
        }
    }

    /**
     * 批量删除业务标识_其它出入库
     *
     * @param sids 需要删除的业务标识_其它出入库ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuStockBusinessFlagByIds(List<Long> sids) {
        List<ConBuStockBusinessFlag> list = conBuStockBusinessFlagMapper.selectList(new QueryWrapper<ConBuStockBusinessFlag>()
                .lambda().in(ConBuStockBusinessFlag::getSid, sids));
        int row = conBuStockBusinessFlagMapper.deleteBatchIds(sids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ConBuStockBusinessFlag());
                MongodbUtil.insertUserLog(o.getSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param conBuStockBusinessFlag
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConBuStockBusinessFlag conBuStockBusinessFlag) {
        int row = 0;
        Long[] sids = conBuStockBusinessFlag.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuStockBusinessFlagMapper.update(null, new UpdateWrapper<ConBuStockBusinessFlag>().lambda().set(ConBuStockBusinessFlag::getStatus, conBuStockBusinessFlag.getStatus())
                    .in(ConBuStockBusinessFlag::getSid, sids));
            if (row == 0) {
                throw new CustomException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                //插入日志
                String remark = conBuStockBusinessFlag.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbDeal.status(id, conBuStockBusinessFlag.getStatus(), null, TITLE, remark);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param conBuStockBusinessFlag
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConBuStockBusinessFlag conBuStockBusinessFlag) {
        int row = 0;
        Long[] sids = conBuStockBusinessFlag.getSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ConBuStockBusinessFlag> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ConBuStockBusinessFlag::getSid, sids);
            updateWrapper.set(ConBuStockBusinessFlag::getHandleStatus, conBuStockBusinessFlag.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(conBuStockBusinessFlag.getHandleStatus())) {
                updateWrapper.set(ConBuStockBusinessFlag::getConfirmDate, new Date());
                updateWrapper.set(ConBuStockBusinessFlag::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = conBuStockBusinessFlagMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, conBuStockBusinessFlag.getHandleStatus(), null, TITLE, null);
                }
            } else {
                throw new CustomException("确认失败,请联系管理员");
            }
        }
        return row;
    }

}
