package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ConCheckItem;
import com.platform.ems.domain.ConCheckStandard;
import com.platform.ems.mapper.ConCheckItemMapper;
import com.platform.ems.service.IConCheckItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测项目Service业务层处理
 *
 * @author qhq
 * @date 2021-11-01
 */
@Service
@SuppressWarnings("all")
public class ConCheckItemServiceImpl extends ServiceImpl<ConCheckItemMapper,ConCheckItem>  implements IConCheckItemService {
    @Autowired
    private ConCheckItemMapper conCheckItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "检测项目";
    /**
     * 查询检测项目
     *
     * @param sid 检测项目ID
     * @return 检测项目
     */
    @Override
    public ConCheckItem selectConCheckItemById(Long sid) {
        ConCheckItem conCheckItem = conCheckItemMapper.selectConCheckItemById(sid);
        MongodbUtil.find(conCheckItem);
        return  conCheckItem;
    }

    /**
     * 查询检测项目列表
     *
     * @param conCheckItem 检测项目
     * @return 检测项目
     */
    @Override
    public List<ConCheckItem> selectConCheckItemList(ConCheckItem conCheckItem) {
        return conCheckItemMapper.selectConCheckItemList(conCheckItem);
    }

    /**
     * 新增检测项目
     * 需要注意编码重复校验
     * @param conCheckItem 检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConCheckItem(ConCheckItem conCheckItem) {
        matchingCode(conCheckItem);
        matchingName(conCheckItem);
        int row = conCheckItemMapper.insert(conCheckItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conCheckItem.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改检测项目
     *
     * @param conCheckItem 检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConCheckItem(ConCheckItem conCheckItem) {
        ConCheckItem response = conCheckItemMapper.selectConCheckItemById(conCheckItem.getSid());
        if(!conCheckItem.getCode().equals(response.getCode())){
            matchingCode(conCheckItem);
        }
        if(!conCheckItem.getName().equals(response.getName())){
            matchingName(conCheckItem);
        }
        int row = conCheckItemMapper.updateById(conCheckItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCheckItem.getSid(), BusinessType.UPDATE.ordinal(), response,conCheckItem,TITLE);
        }
        return row;
    }

    /**
     * 变更检测项目
     *
     * @param conCheckItem 检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConCheckItem(ConCheckItem conCheckItem) {
        ConCheckItem response = conCheckItemMapper.selectConCheckItemById(conCheckItem.getSid());
        if(!conCheckItem.getCode().equals(response.getCode())){
            matchingCode(conCheckItem);
        }
        if(!conCheckItem.getName().equals(response.getName())){
            matchingName(conCheckItem);
        }
        int row=conCheckItemMapper.updateAllById(conCheckItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCheckItem.getSid(), BusinessType.CHANGE.ordinal(), response,conCheckItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除检测项目
     *
     * @param sids 需要删除的检测项目ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConCheckItemByIds(List<Long> sids) {
        return conCheckItemMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     * @param conCheckItem
     * @return
     */
    @Override
    public int changeStatus(ConCheckItem conCheckItem){
        int row=0;
        Long[] sids=conCheckItem.getSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckItemMapper.update(null, new UpdateWrapper<ConCheckItem>().lambda()
                    .set(ConCheckItem::getStatus ,conCheckItem.getStatus())
                    .in(ConCheckItem::getSid,sids));
            for(Long id:sids){
                conCheckItem.setSid(id);
                row=conCheckItemMapper.updateById( conCheckItem);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conCheckItem.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conCheckItem.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conCheckItem
     * @return
     */
    @Override
    public int check(ConCheckItem conCheckItem){
        int row=0;
        Long[] sids=conCheckItem.getSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckItemMapper.update(null,new UpdateWrapper<ConCheckItem>().lambda()
                    .set(ConCheckItem::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConCheckItem::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 验证编码重复
     * @param conCheckItem
     */
    public void matchingCode(ConCheckItem conCheckItem){
        QueryWrapper<ConCheckItem> wrapper = new QueryWrapper<>();
        wrapper.eq("code",conCheckItem.getCode());
        int codeMatching = conCheckItemMapper.selectList(wrapper).size();
        if(codeMatching>0){
            throw new CustomException("已存在相同编码，请重新输入！");
        }
    }

    /**
     * 验证名称重复
     * @param conCheckItem
     */
    public void matchingName(ConCheckItem conCheckItem){
        QueryWrapper<ConCheckItem> wrapper = new QueryWrapper<>();
        wrapper.eq("name",conCheckItem.getName());
        int codeMatching = conCheckItemMapper.selectList(wrapper).size();
        if(codeMatching>0){
            throw new CustomException("已存在相同名称，请重新输入！");
        }
    }

}
