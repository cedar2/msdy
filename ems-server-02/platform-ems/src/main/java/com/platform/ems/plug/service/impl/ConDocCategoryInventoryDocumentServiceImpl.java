package com.platform.ems.plug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConDocCategoryInventoryDocument;
import com.platform.ems.plug.mapper.ConDocCategoryInventoryDocumentMapper;
import com.platform.ems.plug.service.IConDocCategoryInventoryDocumentService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 单据类别_库存凭证Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocCategoryInventoryDocumentServiceImpl extends ServiceImpl<ConDocCategoryInventoryDocumentMapper,ConDocCategoryInventoryDocument>  implements IConDocCategoryInventoryDocumentService {
    @Autowired
    private ConDocCategoryInventoryDocumentMapper conDocCategoryInventoryDocumentMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类别_库存凭证";
    /**
     * 查询单据类别_库存凭证
     *
     * @param sid 单据类别_库存凭证ID
     * @return 单据类别_库存凭证
     */
    @Override
    public ConDocCategoryInventoryDocument selectConDocCategoryInventoryDocumentById(Long sid) {
        ConDocCategoryInventoryDocument conDocCategoryInventoryDocument = conDocCategoryInventoryDocumentMapper.selectConDocCategoryInventoryDocumentById(sid);
        MongodbUtil.find(conDocCategoryInventoryDocument);
        return  conDocCategoryInventoryDocument;
    }

    @Override
    public List<ConDocCategoryInventoryDocument> getList() {
        return conDocCategoryInventoryDocumentMapper.getList();
    }

    /**
     * 查询单据类别_库存凭证列表
     *
     * @param conDocCategoryInventoryDocument 单据类别_库存凭证
     * @return 单据类别_库存凭证
     */
    @Override
    public List<ConDocCategoryInventoryDocument> selectConDocCategoryInventoryDocumentList(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument) {
        return conDocCategoryInventoryDocumentMapper.selectConDocCategoryInventoryDocumentList(conDocCategoryInventoryDocument);
    }

    /**
     * 新增单据类别_库存凭证
     * 需要注意编码重复校验
     * @param conDocCategoryInventoryDocument 单据类别_库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocCategoryInventoryDocument(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument) {
        String name = conDocCategoryInventoryDocument.getName();
        String code = conDocCategoryInventoryDocument.getCode();
        List<ConDocCategoryInventoryDocument> list = conDocCategoryInventoryDocumentMapper.selectList(new QueryWrapper<ConDocCategoryInventoryDocument>().lambda()
                .or().eq(ConDocCategoryInventoryDocument::getName, name)
                .or().eq(ConDocCategoryInventoryDocument::getCode, code)
        );
        if(CollectionUtils.isNotEmpty(list)){
            throw new CustomException("配置档案已存在相同的名称或编码，不允许重复");
        }
        int row= conDocCategoryInventoryDocumentMapper.insert(conDocCategoryInventoryDocument);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocCategoryInventoryDocument.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类别_库存凭证
     *
     * @param conDocCategoryInventoryDocument 单据类别_库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocCategoryInventoryDocument(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument) {
        ConDocCategoryInventoryDocument response = conDocCategoryInventoryDocumentMapper.selectConDocCategoryInventoryDocumentById(conDocCategoryInventoryDocument.getSid());
        int row=conDocCategoryInventoryDocumentMapper.updateById(conDocCategoryInventoryDocument);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocCategoryInventoryDocument.getSid(), BusinessType.UPDATE.ordinal(), response,conDocCategoryInventoryDocument,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类别_库存凭证
     *
     * @param conDocCategoryInventoryDocument 单据类别_库存凭证
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocCategoryInventoryDocument(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument) {
        String name = conDocCategoryInventoryDocument.getName();
        ConDocCategoryInventoryDocument item = conDocCategoryInventoryDocumentMapper.selectOne(new QueryWrapper<ConDocCategoryInventoryDocument>().lambda()
                .eq(ConDocCategoryInventoryDocument::getName, name)
        );
        if (item != null && !item.getSid().equals(conDocCategoryInventoryDocument.getSid())) {
            throw new CustomException("配置档案已存在相同的名称，不允许重复");
        }
        ConDocCategoryInventoryDocument response = conDocCategoryInventoryDocumentMapper.selectConDocCategoryInventoryDocumentById(conDocCategoryInventoryDocument.getSid());
        int row = conDocCategoryInventoryDocumentMapper.updateAllById(conDocCategoryInventoryDocument);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocCategoryInventoryDocument.getSid(), BusinessType.CHANGE.ordinal(), response, conDocCategoryInventoryDocument, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类别_库存凭证
     *
     * @param sids 需要删除的单据类别_库存凭证ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocCategoryInventoryDocumentByIds(List<Long> sids) {
        return conDocCategoryInventoryDocumentMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocCategoryInventoryDocument
    * @return
    */
    @Override
    public int changeStatus(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument){
        int row=0;
        Long[] sids=conDocCategoryInventoryDocument.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocCategoryInventoryDocument.setSid(id);
                row=conDocCategoryInventoryDocumentMapper.updateById( conDocCategoryInventoryDocument);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocCategoryInventoryDocument.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocCategoryInventoryDocument.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocCategoryInventoryDocument
     * @return
     */
    @Override
    public int check(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument){
        int row=0;
        Long[] sids=conDocCategoryInventoryDocument.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocCategoryInventoryDocument.setSid(id);
                row=conDocCategoryInventoryDocumentMapper.updateById( conDocCategoryInventoryDocument);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocCategoryInventoryDocument.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
