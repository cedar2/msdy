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
import com.platform.ems.plug.domain.ConBuTypeInventoryDocument;
import com.platform.ems.plug.mapper.ConBuTypeInventoryDocumentMapper;
import com.platform.ems.plug.service.IConBuTypeInventoryDocumentService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务类型_库存凭证Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeInventoryDocumentServiceImpl extends ServiceImpl<ConBuTypeInventoryDocumentMapper,ConBuTypeInventoryDocument>  implements IConBuTypeInventoryDocumentService {
    @Autowired
    private ConBuTypeInventoryDocumentMapper conBuTypeInventoryDocumentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_库存凭证";
    /**
     * 查询业务类型_库存凭证
     *
     * @param sid 业务类型_库存凭证ID
     * @return 业务类型_库存凭证
     */
    @Override
    public ConBuTypeInventoryDocument selectConBuTypeInventoryDocumentById(Long sid) {
        ConBuTypeInventoryDocument conBuTypeInventoryDocument = conBuTypeInventoryDocumentMapper.selectConBuTypeInventoryDocumentById(sid);
        MongodbUtil.find(conBuTypeInventoryDocument);
        return  conBuTypeInventoryDocument;
    }

    /**
     * 查询业务类型_库存凭证列表
     *
     * @param conBuTypeInventoryDocument 业务类型_库存凭证
     * @return 业务类型_库存凭证
     */
    @Override
    public List<ConBuTypeInventoryDocument> selectConBuTypeInventoryDocumentList(ConBuTypeInventoryDocument conBuTypeInventoryDocument) {
        return conBuTypeInventoryDocumentMapper.selectConBuTypeInventoryDocumentList(conBuTypeInventoryDocument);
    }

    /**
     * 新增业务类型_库存凭证
     * 需要注意编码重复校验
     * @param conBuTypeInventoryDocument 业务类型_库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeInventoryDocument(ConBuTypeInventoryDocument conBuTypeInventoryDocument) {
        List<ConBuTypeInventoryDocument> codeList = conBuTypeInventoryDocumentMapper.selectList(new QueryWrapper<ConBuTypeInventoryDocument>().lambda()
                .eq(ConBuTypeInventoryDocument::getCode, conBuTypeInventoryDocument.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeInventoryDocument> nameList = conBuTypeInventoryDocumentMapper.selectList(new QueryWrapper<ConBuTypeInventoryDocument>().lambda()
                .eq(ConBuTypeInventoryDocument::getName, conBuTypeInventoryDocument.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeInventoryDocumentMapper.insert(conBuTypeInventoryDocument);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeInventoryDocument.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_库存凭证
     *
     * @param conBuTypeInventoryDocument 业务类型_库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeInventoryDocument(ConBuTypeInventoryDocument conBuTypeInventoryDocument) {
        ConBuTypeInventoryDocument response = conBuTypeInventoryDocumentMapper.selectConBuTypeInventoryDocumentById(conBuTypeInventoryDocument.getSid());
        int row=conBuTypeInventoryDocumentMapper.updateById(conBuTypeInventoryDocument);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeInventoryDocument.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeInventoryDocument,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_库存凭证
     *
     * @param conBuTypeInventoryDocument 业务类型_库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeInventoryDocument(ConBuTypeInventoryDocument conBuTypeInventoryDocument) {
        List<ConBuTypeInventoryDocument> nameList = conBuTypeInventoryDocumentMapper.selectList(new QueryWrapper<ConBuTypeInventoryDocument>().lambda()
                .eq(ConBuTypeInventoryDocument::getName, conBuTypeInventoryDocument.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeInventoryDocument.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeInventoryDocument.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeInventoryDocument response = conBuTypeInventoryDocumentMapper.selectConBuTypeInventoryDocumentById(conBuTypeInventoryDocument.getSid());
        int row = conBuTypeInventoryDocumentMapper.updateAllById(conBuTypeInventoryDocument);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeInventoryDocument.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeInventoryDocument, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_库存凭证
     *
     * @param sids 需要删除的业务类型_库存凭证ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeInventoryDocumentByIds(List<Long> sids) {
        return conBuTypeInventoryDocumentMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeInventoryDocument
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeInventoryDocument conBuTypeInventoryDocument){
        int row=0;
        Long[] sids=conBuTypeInventoryDocument.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeInventoryDocument.setSid(id);
                row=conBuTypeInventoryDocumentMapper.updateById( conBuTypeInventoryDocument);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeInventoryDocument.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeInventoryDocument.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeInventoryDocument
     * @return
     */
    @Override
    public int check(ConBuTypeInventoryDocument conBuTypeInventoryDocument){
        int row=0;
        Long[] sids=conBuTypeInventoryDocument.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeInventoryDocument.setSid(id);
                row=conBuTypeInventoryDocumentMapper.updateById( conBuTypeInventoryDocument);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeInventoryDocument.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
