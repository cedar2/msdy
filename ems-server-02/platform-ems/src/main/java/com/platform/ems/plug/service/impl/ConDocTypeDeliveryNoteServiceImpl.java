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
import com.platform.ems.plug.domain.ConDocTypeDeliveryNote;
import com.platform.ems.plug.mapper.ConDocTypeDeliveryNoteMapper;
import com.platform.ems.plug.service.IConDocTypeDeliveryNoteService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_采购交货单/销售发货单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeDeliveryNoteServiceImpl extends ServiceImpl<ConDocTypeDeliveryNoteMapper, ConDocTypeDeliveryNote> implements IConDocTypeDeliveryNoteService {
    @Autowired
    private ConDocTypeDeliveryNoteMapper conDocTypeDeliveryNoteMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_采购交货单/销售发货单";

    /**
     * 查询单据类型_采购交货单/销售发货单
     *
     * @param sid 单据类型_采购交货单/销售发货单ID
     * @return 单据类型_采购交货单/销售发货单
     */
    @Override
    public ConDocTypeDeliveryNote selectConDocTypeDeliveryNoteById(Long sid) {
        ConDocTypeDeliveryNote conDocTypeDeliveryNote = conDocTypeDeliveryNoteMapper.selectConDocTypeDeliveryNoteById(sid);
        MongodbUtil.find(conDocTypeDeliveryNote);
        return conDocTypeDeliveryNote;
    }

    /**
     * 查询单据类型_采购交货单/销售发货单列表
     *
     * @param conDocTypeDeliveryNote 单据类型_采购交货单/销售发货单
     * @return 单据类型_采购交货单/销售发货单
     */
    @Override
    public List<ConDocTypeDeliveryNote> selectConDocTypeDeliveryNoteList(ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        return conDocTypeDeliveryNoteMapper.selectConDocTypeDeliveryNoteList(conDocTypeDeliveryNote);
    }

    /**
     * 新增单据类型_采购交货单/销售发货单
     * 需要注意编码重复校验
     *
     * @param conDocTypeDeliveryNote 单据类型_采购交货单/销售发货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeDeliveryNote(ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        List<ConDocTypeDeliveryNote> codeList = conDocTypeDeliveryNoteMapper.selectList(new QueryWrapper<ConDocTypeDeliveryNote>().lambda()
                .eq(ConDocTypeDeliveryNote::getCode, conDocTypeDeliveryNote.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeDeliveryNote> nameList = conDocTypeDeliveryNoteMapper.selectList(new QueryWrapper<ConDocTypeDeliveryNote>().lambda()
                .eq(ConDocTypeDeliveryNote::getName, conDocTypeDeliveryNote.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conDocTypeDeliveryNoteMapper.insert(conDocTypeDeliveryNote);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeDeliveryNote.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_采购交货单/销售发货单
     *
     * @param conDocTypeDeliveryNote 单据类型_采购交货单/销售发货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeDeliveryNote(ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        ConDocTypeDeliveryNote response = conDocTypeDeliveryNoteMapper.selectConDocTypeDeliveryNoteById(conDocTypeDeliveryNote.getSid());
        int row = conDocTypeDeliveryNoteMapper.updateById(conDocTypeDeliveryNote);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeDeliveryNote.getSid(), BusinessType.UPDATE.getValue(), response, conDocTypeDeliveryNote, TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_采购交货单/销售发货单
     *
     * @param conDocTypeDeliveryNote 单据类型_采购交货单/销售发货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeDeliveryNote(ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        List<ConDocTypeDeliveryNote> nameList = conDocTypeDeliveryNoteMapper.selectList(new QueryWrapper<ConDocTypeDeliveryNote>().lambda()
                .eq(ConDocTypeDeliveryNote::getName, conDocTypeDeliveryNote.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeDeliveryNote.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeDeliveryNote.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeDeliveryNote response = conDocTypeDeliveryNoteMapper.selectConDocTypeDeliveryNoteById(conDocTypeDeliveryNote.getSid());
        int row = conDocTypeDeliveryNoteMapper.updateAllById(conDocTypeDeliveryNote);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeDeliveryNote.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeDeliveryNote, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_采购交货单/销售发货单
     *
     * @param sids 需要删除的单据类型_采购交货单/销售发货单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeDeliveryNoteByIds(List<Long> sids) {
        return conDocTypeDeliveryNoteMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDocTypeDeliveryNote
     * @return
     */
    @Override
    public int changeStatus(ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        int row = 0;
        Long[] sids = conDocTypeDeliveryNote.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conDocTypeDeliveryNote.setSid(id);
                row = conDocTypeDeliveryNoteMapper.updateById(conDocTypeDeliveryNote);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDocTypeDeliveryNote.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDocTypeDeliveryNote.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDocTypeDeliveryNote
     * @return
     */
    @Override
    public int check(ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        int row = 0;
        Long[] sids = conDocTypeDeliveryNote.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conDocTypeDeliveryNote.setSid(id);
                row = conDocTypeDeliveryNoteMapper.updateById(conDocTypeDeliveryNote);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeDeliveryNote.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 单据类型_采购交货单/销售发货单下拉列表
     */
    @Override
    public List<ConDocTypeDeliveryNote> getList(ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        return conDocTypeDeliveryNoteMapper.getList(conDocTypeDeliveryNote);
    }
}
