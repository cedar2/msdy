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
import com.platform.ems.plug.domain.ConDocTypeInventoryDocument;
import com.platform.ems.plug.mapper.ConDocTypeInventoryDocumentMapper;
import com.platform.ems.plug.service.IConDocTypeInventoryDocumentService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_库存凭证Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-17
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeInventoryDocumentServiceImpl extends ServiceImpl<ConDocTypeInventoryDocumentMapper, ConDocTypeInventoryDocument> implements IConDocTypeInventoryDocumentService {
    @Autowired
    private ConDocTypeInventoryDocumentMapper conDocTypeInventoryDocumentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_库存凭证";

    /**
     * 查询单据类型_库存凭证
     *
     * @param sid 单据类型_库存凭证ID
     * @return 单据类型_库存凭证
     */
    @Override
    public ConDocTypeInventoryDocument selectConDocTypeInventoryDocumentById(Long sid) {
        ConDocTypeInventoryDocument conDocTypeInventoryDocument = conDocTypeInventoryDocumentMapper.selectConDocTypeInventoryDocumentById(sid);
        MongodbUtil.find(conDocTypeInventoryDocument);
        return conDocTypeInventoryDocument;
    }

    /**
     * 查询单据类型_库存凭证列表
     *
     * @param conDocTypeInventoryDocument 单据类型_库存凭证
     * @return 单据类型_库存凭证
     */
    @Override
    public List<ConDocTypeInventoryDocument> selectConDocTypeInventoryDocumentList(ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        return conDocTypeInventoryDocumentMapper.selectConDocTypeInventoryDocumentList(conDocTypeInventoryDocument);
    }

    /**
     * 新增单据类型_库存凭证
     * 需要注意编码重复校验
     *
     * @param conDocTypeInventoryDocument 单据类型_库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeInventoryDocument(ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        List<ConDocTypeInventoryDocument> codeList = conDocTypeInventoryDocumentMapper.selectList(new QueryWrapper<ConDocTypeInventoryDocument>().lambda()
                .eq(ConDocTypeInventoryDocument::getCode, conDocTypeInventoryDocument.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeInventoryDocument> nameList = conDocTypeInventoryDocumentMapper.selectList(new QueryWrapper<ConDocTypeInventoryDocument>().lambda()
                .eq(ConDocTypeInventoryDocument::getName, conDocTypeInventoryDocument.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conDocTypeInventoryDocument);
        int row = conDocTypeInventoryDocumentMapper.insert(conDocTypeInventoryDocument);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeInventoryDocument.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConDocTypeInventoryDocument o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改单据类型_库存凭证
     *
     * @param conDocTypeInventoryDocument 单据类型_库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeInventoryDocument(ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        ConDocTypeInventoryDocument response = conDocTypeInventoryDocumentMapper.selectConDocTypeInventoryDocumentById(conDocTypeInventoryDocument.getSid());
        int row = conDocTypeInventoryDocumentMapper.updateById(conDocTypeInventoryDocument);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeInventoryDocument.getSid(), BusinessType.UPDATE.getValue(), response, conDocTypeInventoryDocument, TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_库存凭证
     *
     * @param conDocTypeInventoryDocument 单据类型_库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeInventoryDocument(ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        List<ConDocTypeInventoryDocument> nameList = conDocTypeInventoryDocumentMapper.selectList(new QueryWrapper<ConDocTypeInventoryDocument>().lambda()
                .eq(ConDocTypeInventoryDocument::getName, conDocTypeInventoryDocument.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeInventoryDocument.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeInventoryDocument.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        setConfirmInfo(conDocTypeInventoryDocument);
        ConDocTypeInventoryDocument response = conDocTypeInventoryDocumentMapper.selectConDocTypeInventoryDocumentById(conDocTypeInventoryDocument.getSid());
        int row = conDocTypeInventoryDocumentMapper.updateAllById(conDocTypeInventoryDocument);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeInventoryDocument.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeInventoryDocument, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_库存凭证
     *
     * @param sids 需要删除的单据类型_库存凭证ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeInventoryDocumentByIds(List<Long> sids) {
        return conDocTypeInventoryDocumentMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDocTypeInventoryDocument
     * @return
     */
    @Override
    public int changeStatus(ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        int row = 0;
        Long[] sids = conDocTypeInventoryDocument.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeInventoryDocumentMapper.update(null, new UpdateWrapper<ConDocTypeInventoryDocument>().lambda().set(ConDocTypeInventoryDocument::getStatus, conDocTypeInventoryDocument.getStatus())
                    .in(ConDocTypeInventoryDocument::getSid, sids));
            for (Long id : sids) {
                conDocTypeInventoryDocument.setSid(id);
                row = conDocTypeInventoryDocumentMapper.updateById(conDocTypeInventoryDocument);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDocTypeInventoryDocument.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDocTypeInventoryDocument.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDocTypeInventoryDocument
     * @return
     */
    @Override
    public int check(ConDocTypeInventoryDocument conDocTypeInventoryDocument) {
        int row = 0;
        Long[] sids = conDocTypeInventoryDocument.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeInventoryDocumentMapper.update(null, new UpdateWrapper<ConDocTypeInventoryDocument>().lambda().set(ConDocTypeInventoryDocument::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConDocTypeInventoryDocument::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
