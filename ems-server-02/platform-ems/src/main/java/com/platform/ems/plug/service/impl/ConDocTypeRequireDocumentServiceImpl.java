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
import com.platform.ems.plug.domain.ConDocTypeRequireDocument;
import com.platform.ems.plug.mapper.ConDocTypeRequireDocumentMapper;
import com.platform.ems.plug.service.IConDocTypeRequireDocumentService;
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
 * 单据类型_需求单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeRequireDocumentServiceImpl extends ServiceImpl<ConDocTypeRequireDocumentMapper,ConDocTypeRequireDocument>  implements IConDocTypeRequireDocumentService {
    @Autowired
    private ConDocTypeRequireDocumentMapper conDocTypeRequireDocumentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_需求单";
    /**
     * 查询单据类型_需求单
     *
     * @param sid 单据类型_需求单ID
     * @return 单据类型_需求单
     */
    @Override
    public ConDocTypeRequireDocument selectConDocTypeRequireDocumentById(Long sid) {
        ConDocTypeRequireDocument conDocTypeRequireDocument = conDocTypeRequireDocumentMapper.selectConDocTypeRequireDocumentById(sid);
        MongodbUtil.find(conDocTypeRequireDocument);
        return  conDocTypeRequireDocument;
    }

    /**
     * 查询单据类型_需求单列表
     *
     * @param conDocTypeRequireDocument 单据类型_需求单
     * @return 单据类型_需求单
     */
    @Override
    public List<ConDocTypeRequireDocument> selectConDocTypeRequireDocumentList(ConDocTypeRequireDocument conDocTypeRequireDocument) {
        return conDocTypeRequireDocumentMapper.selectConDocTypeRequireDocumentList(conDocTypeRequireDocument);
    }

    /**
     * 新增单据类型_需求单
     * 需要注意编码重复校验
     * @param conDocTypeRequireDocument 单据类型_需求单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeRequireDocument(ConDocTypeRequireDocument conDocTypeRequireDocument) {
        List<ConDocTypeRequireDocument> codeList = conDocTypeRequireDocumentMapper.selectList(new QueryWrapper<ConDocTypeRequireDocument>().lambda()
                .eq(ConDocTypeRequireDocument::getCode, conDocTypeRequireDocument.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeRequireDocument> nameList = conDocTypeRequireDocumentMapper.selectList(new QueryWrapper<ConDocTypeRequireDocument>().lambda()
                .eq(ConDocTypeRequireDocument::getName, conDocTypeRequireDocument.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeRequireDocumentMapper.insert(conDocTypeRequireDocument);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeRequireDocument.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_需求单
     *
     * @param conDocTypeRequireDocument 单据类型_需求单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeRequireDocument(ConDocTypeRequireDocument conDocTypeRequireDocument) {
        ConDocTypeRequireDocument response = conDocTypeRequireDocumentMapper.selectConDocTypeRequireDocumentById(conDocTypeRequireDocument.getSid());
        int row=conDocTypeRequireDocumentMapper.updateById(conDocTypeRequireDocument);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeRequireDocument.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeRequireDocument,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_需求单
     *
     * @param conDocTypeRequireDocument 单据类型_需求单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeRequireDocument(ConDocTypeRequireDocument conDocTypeRequireDocument) {
        List<ConDocTypeRequireDocument> nameList = conDocTypeRequireDocumentMapper.selectList(new QueryWrapper<ConDocTypeRequireDocument>().lambda()
                .eq(ConDocTypeRequireDocument::getName, conDocTypeRequireDocument.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeRequireDocument.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeRequireDocument.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeRequireDocument response = conDocTypeRequireDocumentMapper.selectConDocTypeRequireDocumentById(conDocTypeRequireDocument.getSid());
        int row = conDocTypeRequireDocumentMapper.updateAllById(conDocTypeRequireDocument);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeRequireDocument.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeRequireDocument, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_需求单
     *
     * @param sids 需要删除的单据类型_需求单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeRequireDocumentByIds(List<Long> sids) {
        return conDocTypeRequireDocumentMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeRequireDocument
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeRequireDocument conDocTypeRequireDocument){
        int row=0;
        Long[] sids=conDocTypeRequireDocument.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeRequireDocument.setSid(id);
                row=conDocTypeRequireDocumentMapper.updateById( conDocTypeRequireDocument);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeRequireDocument.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeRequireDocument.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeRequireDocument
     * @return
     */
    @Override
    public int check(ConDocTypeRequireDocument conDocTypeRequireDocument){
        int row=0;
        Long[] sids=conDocTypeRequireDocument.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeRequireDocument.setSid(id);
                row=conDocTypeRequireDocumentMapper.updateById( conDocTypeRequireDocument);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeRequireDocument.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
