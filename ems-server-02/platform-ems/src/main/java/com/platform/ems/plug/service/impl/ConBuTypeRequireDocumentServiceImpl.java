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
import com.platform.ems.plug.domain.ConBuTypeRequireDocument;
import com.platform.ems.plug.mapper.ConBuTypeRequireDocumentMapper;
import com.platform.ems.plug.service.IConBuTypeRequireDocumentService;
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
 * 业务类型_需求单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeRequireDocumentServiceImpl extends ServiceImpl<ConBuTypeRequireDocumentMapper,ConBuTypeRequireDocument>  implements IConBuTypeRequireDocumentService {
    @Autowired
    private ConBuTypeRequireDocumentMapper conBuTypeRequireDocumentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_需求单";
    /**
     * 查询业务类型_需求单
     *
     * @param sid 业务类型_需求单ID
     * @return 业务类型_需求单
     */
    @Override
    public ConBuTypeRequireDocument selectConBuTypeRequireDocumentById(Long sid) {
        ConBuTypeRequireDocument conBuTypeRequireDocument = conBuTypeRequireDocumentMapper.selectConBuTypeRequireDocumentById(sid);
        MongodbUtil.find(conBuTypeRequireDocument);
        return  conBuTypeRequireDocument;
    }

    /**
     * 查询业务类型_需求单列表
     *
     * @param conBuTypeRequireDocument 业务类型_需求单
     * @return 业务类型_需求单
     */
    @Override
    public List<ConBuTypeRequireDocument> selectConBuTypeRequireDocumentList(ConBuTypeRequireDocument conBuTypeRequireDocument) {
        return conBuTypeRequireDocumentMapper.selectConBuTypeRequireDocumentList(conBuTypeRequireDocument);
    }

    /**
     * 新增业务类型_需求单
     * 需要注意编码重复校验
     * @param conBuTypeRequireDocument 业务类型_需求单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeRequireDocument(ConBuTypeRequireDocument conBuTypeRequireDocument) {
        List<ConBuTypeRequireDocument> codeList = conBuTypeRequireDocumentMapper.selectList(new QueryWrapper<ConBuTypeRequireDocument>().lambda()
                .eq(ConBuTypeRequireDocument::getCode, conBuTypeRequireDocument.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeRequireDocument> nameList = conBuTypeRequireDocumentMapper.selectList(new QueryWrapper<ConBuTypeRequireDocument>().lambda()
                .eq(ConBuTypeRequireDocument::getName, conBuTypeRequireDocument.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeRequireDocumentMapper.insert(conBuTypeRequireDocument);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeRequireDocument.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_需求单
     *
     * @param conBuTypeRequireDocument 业务类型_需求单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeRequireDocument(ConBuTypeRequireDocument conBuTypeRequireDocument) {
        ConBuTypeRequireDocument response = conBuTypeRequireDocumentMapper.selectConBuTypeRequireDocumentById(conBuTypeRequireDocument.getSid());
        int row=conBuTypeRequireDocumentMapper.updateById(conBuTypeRequireDocument);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeRequireDocument.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeRequireDocument,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_需求单
     *
     * @param conBuTypeRequireDocument 业务类型_需求单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeRequireDocument(ConBuTypeRequireDocument conBuTypeRequireDocument) {
        List<ConBuTypeRequireDocument> nameList = conBuTypeRequireDocumentMapper.selectList(new QueryWrapper<ConBuTypeRequireDocument>().lambda()
                .eq(ConBuTypeRequireDocument::getName, conBuTypeRequireDocument.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeRequireDocument.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeRequireDocument.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeRequireDocument response = conBuTypeRequireDocumentMapper.selectConBuTypeRequireDocumentById(conBuTypeRequireDocument.getSid());
        int row = conBuTypeRequireDocumentMapper.updateAllById(conBuTypeRequireDocument);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeRequireDocument.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeRequireDocument, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_需求单
     *
     * @param sids 需要删除的业务类型_需求单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeRequireDocumentByIds(List<Long> sids) {
        return conBuTypeRequireDocumentMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeRequireDocument
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeRequireDocument conBuTypeRequireDocument){
        int row=0;
        Long[] sids=conBuTypeRequireDocument.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeRequireDocument.setSid(id);
                row=conBuTypeRequireDocumentMapper.updateById( conBuTypeRequireDocument);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeRequireDocument.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeRequireDocument.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeRequireDocument
     * @return
     */
    @Override
    public int check(ConBuTypeRequireDocument conBuTypeRequireDocument){
        int row=0;
        Long[] sids=conBuTypeRequireDocument.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeRequireDocument.setSid(id);
                row=conBuTypeRequireDocumentMapper.updateById( conBuTypeRequireDocument);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeRequireDocument.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
