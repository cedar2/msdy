package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConDocTypeInventorySheetMapper;
import com.platform.ems.plug.domain.ConDocTypeInventorySheet;
import com.platform.ems.plug.service.IConDocTypeInventorySheetService;

/**
 * 单据类型(盘点单)Service业务层处理
 *
 * @author chenkw
 * @date 2021-08-11
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeInventorySheetServiceImpl extends ServiceImpl<ConDocTypeInventorySheetMapper,ConDocTypeInventorySheet>  implements IConDocTypeInventorySheetService {
    @Autowired
    private ConDocTypeInventorySheetMapper conDocTypeInventorySheetMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型(盘点单)";
    /**
     * 查询单据类型(盘点单)
     *
     * @param sid 单据类型(盘点单)ID
     * @return 单据类型(盘点单)
     */
    @Override
    public ConDocTypeInventorySheet selectConDocTypeInventorySheetById(Long sid) {
        ConDocTypeInventorySheet conDocTypeInventorySheet = conDocTypeInventorySheetMapper.selectConDocTypeInventorySheetById(sid);
        MongodbUtil.find(conDocTypeInventorySheet);
        return  conDocTypeInventorySheet;
    }

    /**
     * 查询单据类型(盘点单)列表
     *
     * @param conDocTypeInventorySheet 单据类型(盘点单)
     * @return 单据类型(盘点单)
     */
    @Override
    public List<ConDocTypeInventorySheet> selectConDocTypeInventorySheetList(ConDocTypeInventorySheet conDocTypeInventorySheet) {
        return conDocTypeInventorySheetMapper.selectConDocTypeInventorySheetList(conDocTypeInventorySheet);
    }

    /**
     * 新增单据类型(盘点单)
     * 需要注意编码重复校验
     * @param conDocTypeInventorySheet 单据类型(盘点单)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeInventorySheet(ConDocTypeInventorySheet conDocTypeInventorySheet) {
        List<ConDocTypeInventorySheet> codeList = conDocTypeInventorySheetMapper.selectList(new QueryWrapper<ConDocTypeInventorySheet>().lambda()
                .eq(ConDocTypeInventorySheet::getCode, conDocTypeInventorySheet.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeInventorySheet> nameList = conDocTypeInventorySheetMapper.selectList(new QueryWrapper<ConDocTypeInventorySheet>().lambda()
                .eq(ConDocTypeInventorySheet::getName, conDocTypeInventorySheet.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeInventorySheetMapper.insert(conDocTypeInventorySheet);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeInventorySheet.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型(盘点单)
     *
     * @param conDocTypeInventorySheet 单据类型(盘点单)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeInventorySheet(ConDocTypeInventorySheet conDocTypeInventorySheet) {
        List<ConDocTypeInventorySheet> nameList = conDocTypeInventorySheetMapper.selectList(new QueryWrapper<ConDocTypeInventorySheet>().lambda()
                .eq(ConDocTypeInventorySheet::getName, conDocTypeInventorySheet.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeInventorySheet.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        ConDocTypeInventorySheet response = conDocTypeInventorySheetMapper.selectConDocTypeInventorySheetById(conDocTypeInventorySheet.getSid());
        int row=conDocTypeInventorySheetMapper.updateById(conDocTypeInventorySheet);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeInventorySheet.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeInventorySheet,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型(盘点单)
     *
     * @param conDocTypeInventorySheet 单据类型(盘点单)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeInventorySheet(ConDocTypeInventorySheet conDocTypeInventorySheet) {
        List<ConDocTypeInventorySheet> nameList = conDocTypeInventorySheetMapper.selectList(new QueryWrapper<ConDocTypeInventorySheet>().lambda()
                .eq(ConDocTypeInventorySheet::getName, conDocTypeInventorySheet.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeInventorySheet.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeInventorySheet.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeInventorySheet response = conDocTypeInventorySheetMapper.selectConDocTypeInventorySheetById(conDocTypeInventorySheet.getSid());
        int row=conDocTypeInventorySheetMapper.updateAllById(conDocTypeInventorySheet);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeInventorySheet.getSid(), BusinessType.CHANGE.getValue(), response,conDocTypeInventorySheet,TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型(盘点单)
     *
     * @param sids 需要删除的单据类型(盘点单)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeInventorySheetByIds(List<Long> sids) {
        return conDocTypeInventorySheetMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     * @param conDocTypeInventorySheet
     * @return
     */
    @Override
    public int changeStatus(ConDocTypeInventorySheet conDocTypeInventorySheet){
        int row=0;
        Long[] sids=conDocTypeInventorySheet.getSidList();
        if(sids!=null&&sids.length>0){
            row=conDocTypeInventorySheetMapper.update(null, new UpdateWrapper<ConDocTypeInventorySheet>().lambda().set(ConDocTypeInventorySheet::getStatus ,conDocTypeInventorySheet.getStatus() )
                    .in(ConDocTypeInventorySheet::getSid,sids));
            for(Long id:sids){
                conDocTypeInventorySheet.setSid(id);
                row=conDocTypeInventorySheetMapper.updateById( conDocTypeInventorySheet);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeInventorySheet.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeInventorySheet.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeInventorySheet
     * @return
     */
    @Override
    public int check(ConDocTypeInventorySheet conDocTypeInventorySheet){
        int row=0;
        Long[] sids=conDocTypeInventorySheet.getSidList();
        if(sids!=null&&sids.length>0){
            row=conDocTypeInventorySheetMapper.update(null,new UpdateWrapper<ConDocTypeInventorySheet>().lambda().set(ConDocTypeInventorySheet::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConDocTypeInventorySheet::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
