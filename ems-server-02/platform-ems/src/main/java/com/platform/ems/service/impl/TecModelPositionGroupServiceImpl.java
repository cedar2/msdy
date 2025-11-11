package com.platform.ems.service.impl;

import java.util.*;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.TecModelPositionGroupItem;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.mapper.TecModelPositionGroupItemMapper;
import com.platform.ems.util.MongodbDeal;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.mapper.TecModelPositionGroupMapper;
import com.platform.ems.domain.TecModelPositionGroup;
import com.platform.ems.service.ITecModelPositionGroupService;

/**
 * 版型部位组档案Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-02
 */
@Service
@SuppressWarnings("all")
public class TecModelPositionGroupServiceImpl extends ServiceImpl<TecModelPositionGroupMapper,TecModelPositionGroup>  implements ITecModelPositionGroupService {
    @Autowired
    private TecModelPositionGroupMapper tecModelPositionGroupMapper;
    @Autowired
    private TecModelPositionGroupItemMapper itemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "版型部位组档案";
    /**
     * 查询版型部位组档案
     *
     * @param groupSid 版型部位组档案ID
     * @return 版型部位组档案
     */
    @Override
    public TecModelPositionGroup selectTecModelPositionGroupById(Long groupSid) {
        TecModelPositionGroup tecModelPositionGroup = tecModelPositionGroupMapper.selectTecModelPositionGroupById(groupSid);
        if(tecModelPositionGroup!=null){
            TecModelPositionGroupItem item=new TecModelPositionGroupItem();
            item.setGroupSid(tecModelPositionGroup.getGroupSid());
            List<TecModelPositionGroupItem> itemList= itemMapper.selectTecModelPositionGroupItemList(item);
            tecModelPositionGroup.setItemList(itemList);
        }
        MongodbUtil.find(tecModelPositionGroup);
        return tecModelPositionGroup;
    }
    /**
     * 查询版型部位组档案列表
     *
     * @param tecModelPositionGroup 版型部位组档案
     * @return 版型部位组档案
     */
    @Override
    public List<TecModelPositionGroup> selectTecModelPositionGroupList(TecModelPositionGroup tecModelPositionGroup) {
        return tecModelPositionGroupMapper.selectTecModelPositionGroupList(tecModelPositionGroup);
    }

    @Override
    public List<TecModelPositionGroup> getList(){
        List<TecModelPositionGroup> groupList=tecModelPositionGroupMapper.getList();
        return groupList;
    }

    /**
     * 新增版型部位组档案
     * 需要注意编码重复校验
     * @param tecModelPositionGroup 版型部位组档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecModelPositionGroup(TecModelPositionGroup tecModelPositionGroup) {
        Map<String,Object> params=new HashMap<>();
        params.put("group_name", tecModelPositionGroup.getGroupName());
        List<TecModelPositionGroup> query=tecModelPositionGroupMapper.selectByMap(params);
        if(query.size()>0){
            throw new CustomException("版型部位组名重复，请查看");
        }
        params.clear();
        params.put("group_code", tecModelPositionGroup.getGroupCode());
        List<TecModelPositionGroup> query2=tecModelPositionGroupMapper.selectByMap(params);
        if(query2.size()>0){
            throw new CustomException("版型部位组编码重复，请查看");
        }
        if(ConstantsEms.CHECK_STATUS.equals(tecModelPositionGroup.getHandleStatus())){
            tecModelPositionGroup.setConfirmDate(new Date());
            tecModelPositionGroup.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row= tecModelPositionGroupMapper.insert(tecModelPositionGroup);
        if(row>0){
            List<TecModelPositionGroupItem> itemList=tecModelPositionGroup.getItemList();
            if(itemList!=null&&itemList.size()>0){
                for(TecModelPositionGroupItem item:itemList){
                    item.setGroupSid(tecModelPositionGroup.getGroupSid());
                    item.setCreateDate(new Date());
                    item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    itemMapper.insert(item);
                }
            }
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(tecModelPositionGroup.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_tec_model_position_group")
                        .setDocumentSid(tecModelPositionGroup.getGroupSid());
                sysTodoTask.setTitle("版型部位组档案: " + tecModelPositionGroup.getGroupCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(tecModelPositionGroup.getGroupCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbDeal.insert(tecModelPositionGroup.getGroupSid(), tecModelPositionGroup.getHandleStatus(), null, TITLE,null);
        }
        return row;
    }

    /**
     * 修改版型部位组档案
     *
     * @param tecModelPositionGroup 版型部位组档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecModelPositionGroup(TecModelPositionGroup tecModelPositionGroup) {
        Map<String,Object> queryParams=new HashMap<>();
        queryParams.put("group_name", tecModelPositionGroup.getGroupName());
        List<TecModelPositionGroup> queryResult=tecModelPositionGroupMapper.selectByMap(queryParams);
        if(queryResult.size()>0){
            for(TecModelPositionGroup group:queryResult){
                if(group.getGroupName().equals(tecModelPositionGroup.getGroupName())&&!group.getGroupSid().equals(tecModelPositionGroup.getGroupSid())){
                    throw new CustomException("名称重复,请查看");
                }
            }
        }
        queryParams.clear();
        queryParams.put("group_code", tecModelPositionGroup.getGroupCode());
        List<TecModelPositionGroup> queryResult2=tecModelPositionGroupMapper.selectByMap(queryParams);
        if(queryResult2.size()>0){
            for(TecModelPositionGroup group:queryResult2){
                if(group.getGroupCode().equals(tecModelPositionGroup.getGroupCode())&&!group.getGroupSid().equals(tecModelPositionGroup.getGroupSid())){
                    throw new CustomException("编码重复,请查看");
                }
            }
        }
        tecModelPositionGroup.setUpdateDate(new Date());
        tecModelPositionGroup.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        if(ConstantsEms.CHECK_STATUS.equals(tecModelPositionGroup.getHandleStatus())){
            tecModelPositionGroup.setConfirmDate(new Date());
            tecModelPositionGroup.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        TecModelPositionGroup old = tecModelPositionGroupMapper.selectById(tecModelPositionGroup.getGroupSid());
        int row=tecModelPositionGroupMapper.updateAllById(tecModelPositionGroup);
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(tecModelPositionGroup.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, tecModelPositionGroup.getGroupSid()));
        }
        List<TecModelPositionGroupItem> itemList=tecModelPositionGroup.getItemList();
        //删除旧数据
        Map<String,Object> params=new HashMap<>();
        params.put("group_sid", tecModelPositionGroup.getGroupSid());
        itemMapper.deleteByMap(params);
        if(itemList!=null&&itemList.size()>0){
            for(TecModelPositionGroupItem item:itemList){
                item.setGroupSid(tecModelPositionGroup.getGroupSid());
                item.setUpdateDate(new Date());
                item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                itemMapper.insert(item);
            }
        }
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(old, tecModelPositionGroup);
        String remark = null;
        MongodbDeal.update(tecModelPositionGroup.getGroupSid(), old.getHandleStatus(), tecModelPositionGroup.getHandleStatus(), msgList, TITLE, remark);
        return row;
    }

    /**
     * 变更版型部位组档案
     *
     * @param tecModelPositionGroup 版型部位组档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecModelPositionGroup(TecModelPositionGroup tecModelPositionGroup) {
        TecModelPositionGroup response = tecModelPositionGroupMapper.selectTecModelPositionGroupById(tecModelPositionGroup.getGroupSid());
        int row=tecModelPositionGroupMapper.updateAllById(tecModelPositionGroup);
        if(row>0){
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, tecModelPositionGroup);
            String remark = null;
            MongodbDeal.update(tecModelPositionGroup.getGroupSid(), response.getHandleStatus(), tecModelPositionGroup.getHandleStatus(), msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 批量删除版型部位组档案
     *
     * @param groupSids 需要删除的版型部位组档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecModelPositionGroupByIds(List<String> groupSids) {
        int row=tecModelPositionGroupMapper.deleteBatchIds(groupSids);
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, groupSids));
        for(String sid:groupSids){
            Map<String,Object> params=new HashMap<>();
            params.put("group_sid", sid);
            itemMapper.deleteByMap(params);
        }
        return row;
    }

    /**
    * 启用/停用
    * @param tecModelPositionGroup
    * @return
    */
    @Override
    public int changeStatus(TecModelPositionGroup tecModelPositionGroup){
        int row=0;
        Long[] sids=tecModelPositionGroup.getGroupSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                tecModelPositionGroup.setGroupSid(id);
                row=tecModelPositionGroupMapper.updateById( tecModelPositionGroup);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                String remark = StrUtil.isEmpty(tecModelPositionGroup.getDisableRemark()) ? null : tecModelPositionGroup.getDisableRemark();
                MongodbDeal.status(Long.valueOf(id), tecModelPositionGroup.getStatus(), null, TITLE, remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param tecModelPositionGroup
     * @return
     */
    @Override
    public int check(TecModelPositionGroup tecModelPositionGroup){
        int row=0;
        Long[] sids=tecModelPositionGroup.getGroupSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                tecModelPositionGroup.setGroupSid(id);
                TecModelPositionGroup group = selectTecModelPositionGroupById(id);
                List<TecModelPositionGroupItem> itemList = group.getItemList();
                if(CollectionUtils.isEmpty(itemList)){
                    throw new CustomException(group.getGroupName()+"明细为空不允许确认");
                }
                row=tecModelPositionGroupMapper.updateById( tecModelPositionGroup);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                MongodbDeal.check(id, tecModelPositionGroup.getHandleStatus(), null,TITLE, null);
            }
        }
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(tecModelPositionGroup.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, sids));
        }
        return row;
    }


}
