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
import com.platform.ems.plug.domain.ConDocTypeVendorAccountAdjust;
import com.platform.ems.plug.mapper.ConDocTypeVendorAccountAdjustMapper;
import com.platform.ems.plug.service.IConDocTypeVendorAccountAdjustService;
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
 * 单据类型_供应商调账单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeVendorAccountAdjustServiceImpl extends ServiceImpl<ConDocTypeVendorAccountAdjustMapper,ConDocTypeVendorAccountAdjust>  implements IConDocTypeVendorAccountAdjustService {
    @Autowired
    private ConDocTypeVendorAccountAdjustMapper conDocTypeVendorAccountAdjustMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_供应商调账单";
    /**
     * 查询单据类型_供应商调账单
     *
     * @param sid 单据类型_供应商调账单ID
     * @return 单据类型_供应商调账单
     */
    @Override
    public ConDocTypeVendorAccountAdjust selectConDocTypeVendorAccountAdjustById(Long sid) {
        ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust = conDocTypeVendorAccountAdjustMapper.selectConDocTypeVendorAccountAdjustById(sid);
        MongodbUtil.find(conDocTypeVendorAccountAdjust);
        return  conDocTypeVendorAccountAdjust;
    }

    /**
     * 查询单据类型_供应商调账单列表
     *
     * @param conDocTypeVendorAccountAdjust 单据类型_供应商调账单
     * @return 单据类型_供应商调账单
     */
    @Override
    public List<ConDocTypeVendorAccountAdjust> selectConDocTypeVendorAccountAdjustList(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust) {
        return conDocTypeVendorAccountAdjustMapper.selectConDocTypeVendorAccountAdjustList(conDocTypeVendorAccountAdjust);
    }

    /**
     * 新增单据类型_供应商调账单
     * 需要注意编码重复校验
     * @param conDocTypeVendorAccountAdjust 单据类型_供应商调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeVendorAccountAdjust(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust) {
        List<ConDocTypeVendorAccountAdjust> codeList = conDocTypeVendorAccountAdjustMapper.selectList(new QueryWrapper<ConDocTypeVendorAccountAdjust>().lambda()
                .eq(ConDocTypeVendorAccountAdjust::getCode, conDocTypeVendorAccountAdjust.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeVendorAccountAdjust> nameList = conDocTypeVendorAccountAdjustMapper.selectList(new QueryWrapper<ConDocTypeVendorAccountAdjust>().lambda()
                .eq(ConDocTypeVendorAccountAdjust::getName, conDocTypeVendorAccountAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeVendorAccountAdjustMapper.insert(conDocTypeVendorAccountAdjust);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeVendorAccountAdjust.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_供应商调账单
     *
     * @param conDocTypeVendorAccountAdjust 单据类型_供应商调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeVendorAccountAdjust(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust) {
        ConDocTypeVendorAccountAdjust response = conDocTypeVendorAccountAdjustMapper.selectConDocTypeVendorAccountAdjustById(conDocTypeVendorAccountAdjust.getSid());
        int row=conDocTypeVendorAccountAdjustMapper.updateById(conDocTypeVendorAccountAdjust);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeVendorAccountAdjust.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeVendorAccountAdjust,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_供应商调账单
     *
     * @param conDocTypeVendorAccountAdjust 单据类型_供应商调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeVendorAccountAdjust(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust) {
        List<ConDocTypeVendorAccountAdjust> nameList = conDocTypeVendorAccountAdjustMapper.selectList(new QueryWrapper<ConDocTypeVendorAccountAdjust>().lambda()
                .eq(ConDocTypeVendorAccountAdjust::getName, conDocTypeVendorAccountAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeVendorAccountAdjust.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeVendorAccountAdjust.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeVendorAccountAdjust response = conDocTypeVendorAccountAdjustMapper.selectConDocTypeVendorAccountAdjustById(conDocTypeVendorAccountAdjust.getSid());
        int row = conDocTypeVendorAccountAdjustMapper.updateAllById(conDocTypeVendorAccountAdjust);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeVendorAccountAdjust.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeVendorAccountAdjust, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_供应商调账单
     *
     * @param sids 需要删除的单据类型_供应商调账单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeVendorAccountAdjustByIds(List<Long> sids) {
        return conDocTypeVendorAccountAdjustMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeVendorAccountAdjust
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust){
        int row=0;
        Long[] sids=conDocTypeVendorAccountAdjust.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeVendorAccountAdjust.setSid(id);
                row=conDocTypeVendorAccountAdjustMapper.updateById( conDocTypeVendorAccountAdjust);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeVendorAccountAdjust.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeVendorAccountAdjust.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeVendorAccountAdjust
     * @return
     */
    @Override
    public int check(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust){
        int row=0;
        Long[] sids=conDocTypeVendorAccountAdjust.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeVendorAccountAdjust.setSid(id);
                row=conDocTypeVendorAccountAdjustMapper.updateById( conDocTypeVendorAccountAdjust);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeVendorAccountAdjust.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
