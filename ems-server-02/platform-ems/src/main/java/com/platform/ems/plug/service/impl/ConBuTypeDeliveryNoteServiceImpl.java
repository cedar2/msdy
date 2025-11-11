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
import com.platform.ems.plug.domain.ConBuTypeDeliveryNote;
import com.platform.ems.plug.mapper.ConBuTypeDeliveryNoteMapper;
import com.platform.ems.plug.service.IConBuTypeDeliveryNoteService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务类型_采购交货单/销售发货单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeDeliveryNoteServiceImpl extends ServiceImpl<ConBuTypeDeliveryNoteMapper, ConBuTypeDeliveryNote> implements IConBuTypeDeliveryNoteService {
    @Autowired
    private ConBuTypeDeliveryNoteMapper conBuTypeDeliveryNoteMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_采购交货单/销售发货单";

    /**
     * 查询业务类型_采购交货单/销售发货单
     *
     * @param sid 业务类型_采购交货单/销售发货单ID
     * @return 业务类型_采购交货单/销售发货单
     */
    @Override
    public ConBuTypeDeliveryNote selectConBuTypeDeliveryNoteById(Long sid) {
        ConBuTypeDeliveryNote conBuTypeDeliveryNote = conBuTypeDeliveryNoteMapper.selectConBuTypeDeliveryNoteById(sid);
        MongodbUtil.find(conBuTypeDeliveryNote);
        return conBuTypeDeliveryNote;
    }

    /**
     * 查询业务类型_采购交货单/销售发货单列表
     *
     * @param conBuTypeDeliveryNote 业务类型_采购交货单/销售发货单
     * @return 业务类型_采购交货单/销售发货单
     */
    @Override
    public List<ConBuTypeDeliveryNote> selectConBuTypeDeliveryNoteList(ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        return conBuTypeDeliveryNoteMapper.selectConBuTypeDeliveryNoteList(conBuTypeDeliveryNote);
    }

    /**
     * 新增业务类型_采购交货单/销售发货单
     * 需要注意编码重复校验
     *
     * @param conBuTypeDeliveryNote 业务类型_采购交货单/销售发货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeDeliveryNote(ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        List<ConBuTypeDeliveryNote> codeList = conBuTypeDeliveryNoteMapper.selectList(new QueryWrapper<ConBuTypeDeliveryNote>().lambda()
                .eq(ConBuTypeDeliveryNote::getCode, conBuTypeDeliveryNote.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeDeliveryNote> nameList = conBuTypeDeliveryNoteMapper.selectList(new QueryWrapper<ConBuTypeDeliveryNote>().lambda()
                .eq(ConBuTypeDeliveryNote::getName, conBuTypeDeliveryNote.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conBuTypeDeliveryNoteMapper.insert(conBuTypeDeliveryNote);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeDeliveryNote.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_采购交货单/销售发货单
     *
     * @param conBuTypeDeliveryNote 业务类型_采购交货单/销售发货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeDeliveryNote(ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        ConBuTypeDeliveryNote response = conBuTypeDeliveryNoteMapper.selectConBuTypeDeliveryNoteById(conBuTypeDeliveryNote.getSid());
        int row = conBuTypeDeliveryNoteMapper.updateById(conBuTypeDeliveryNote);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeDeliveryNote.getSid(), BusinessType.UPDATE.getValue(), response, conBuTypeDeliveryNote, TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_采购交货单/销售发货单
     *
     * @param conBuTypeDeliveryNote 业务类型_采购交货单/销售发货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeDeliveryNote(ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        List<ConBuTypeDeliveryNote> nameList = conBuTypeDeliveryNoteMapper.selectList(new QueryWrapper<ConBuTypeDeliveryNote>().lambda()
                .eq(ConBuTypeDeliveryNote::getName, conBuTypeDeliveryNote.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conBuTypeDeliveryNote.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeDeliveryNote.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeDeliveryNote response = conBuTypeDeliveryNoteMapper.selectConBuTypeDeliveryNoteById(conBuTypeDeliveryNote.getSid());
        int row = conBuTypeDeliveryNoteMapper.updateAllById(conBuTypeDeliveryNote);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeDeliveryNote.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeDeliveryNote, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_采购交货单/销售发货单
     *
     * @param sids 需要删除的业务类型_采购交货单/销售发货单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeDeliveryNoteByIds(List<Long> sids) {
        return conBuTypeDeliveryNoteMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBuTypeDeliveryNote
     * @return
     */
    @Override
    public int changeStatus(ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        int row = 0;
        Long[] sids = conBuTypeDeliveryNote.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBuTypeDeliveryNote.setSid(id);
                row = conBuTypeDeliveryNoteMapper.updateById(conBuTypeDeliveryNote);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBuTypeDeliveryNote.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBuTypeDeliveryNote.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBuTypeDeliveryNote
     * @return
     */
    @Override
    public int check(ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        int row = 0;
        Long[] sids = conBuTypeDeliveryNote.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBuTypeDeliveryNote.setSid(id);
                row = conBuTypeDeliveryNoteMapper.updateById(conBuTypeDeliveryNote);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeDeliveryNote.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 业务类型_采购交货单/销售发货单下拉列表
     */
    @Override
    public List<ConBuTypeDeliveryNote> getList(ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        return conBuTypeDeliveryNoteMapper.getList(conBuTypeDeliveryNote);
    }
}
