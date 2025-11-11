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
import com.platform.ems.plug.domain.ConPurchaseOrg;
import com.platform.ems.plug.mapper.ConPurchaseOrgMapper;
import com.platform.ems.plug.service.IConPurchaseOrgService;
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
 * 采购组织Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConPurchaseOrgServiceImpl extends ServiceImpl<ConPurchaseOrgMapper,ConPurchaseOrg>  implements IConPurchaseOrgService {
    @Autowired
    private ConPurchaseOrgMapper conPurchaseOrgMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "采购组织";
    /**
     * 查询采购组织
     *
     * @param sid 采购组织ID
     * @return 采购组织
     */
    @Override
    public ConPurchaseOrg selectConPurchaseOrgById(Long sid) {
        ConPurchaseOrg conPurchaseOrg = conPurchaseOrgMapper.selectConPurchaseOrgById(sid);
        MongodbUtil.find(conPurchaseOrg);
        return  conPurchaseOrg;
    }

    /**
     * 查询采购组织列表
     *
     * @param conPurchaseOrg 采购组织
     * @return 采购组织
     */
    @Override
    public List<ConPurchaseOrg> selectConPurchaseOrgList(ConPurchaseOrg conPurchaseOrg) {
        return conPurchaseOrgMapper.selectConPurchaseOrgList(conPurchaseOrg);
    }

    /**
     * 新增采购组织
     * 需要注意编码重复校验
     * @param conPurchaseOrg 采购组织
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConPurchaseOrg(ConPurchaseOrg conPurchaseOrg) {
        List<ConPurchaseOrg> codeList = conPurchaseOrgMapper.selectList(new QueryWrapper<ConPurchaseOrg>().lambda()
                .eq(ConPurchaseOrg::getCode, conPurchaseOrg.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConPurchaseOrg> nameList = conPurchaseOrgMapper.selectList(new QueryWrapper<ConPurchaseOrg>().lambda()
                .eq(ConPurchaseOrg::getName, conPurchaseOrg.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conPurchaseOrgMapper.insert(conPurchaseOrg);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conPurchaseOrg.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改采购组织
     *
     * @param conPurchaseOrg 采购组织
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConPurchaseOrg(ConPurchaseOrg conPurchaseOrg) {
        ConPurchaseOrg response = conPurchaseOrgMapper.selectConPurchaseOrgById(conPurchaseOrg.getSid());
        int row=conPurchaseOrgMapper.updateById(conPurchaseOrg);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conPurchaseOrg.getSid(), BusinessType.UPDATE.getValue(), response,conPurchaseOrg,TITLE);
        }
        return row;
    }

    /**
     * 变更采购组织
     *
     * @param conPurchaseOrg 采购组织
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConPurchaseOrg(ConPurchaseOrg conPurchaseOrg) {
        List<ConPurchaseOrg> nameList = conPurchaseOrgMapper.selectList(new QueryWrapper<ConPurchaseOrg>().lambda()
                .eq(ConPurchaseOrg::getName, conPurchaseOrg.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conPurchaseOrg.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conPurchaseOrg.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConPurchaseOrg response = conPurchaseOrgMapper.selectConPurchaseOrgById(conPurchaseOrg.getSid());
        int row = conPurchaseOrgMapper.updateAllById(conPurchaseOrg);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPurchaseOrg.getSid(), BusinessType.CHANGE.getValue(), response, conPurchaseOrg, TITLE);
        }
        return row;
    }

    /**
     * 批量删除采购组织
     *
     * @param sids 需要删除的采购组织ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConPurchaseOrgByIds(List<Long> sids) {
        return conPurchaseOrgMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conPurchaseOrg
    * @return
    */
    @Override
    public int changeStatus(ConPurchaseOrg conPurchaseOrg){
        int row=0;
        Long[] sids=conPurchaseOrg.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPurchaseOrg.setSid(id);
                row=conPurchaseOrgMapper.updateById( conPurchaseOrg);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conPurchaseOrg.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conPurchaseOrg.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conPurchaseOrg
     * @return
     */
    @Override
    public int check(ConPurchaseOrg conPurchaseOrg){
        int row=0;
        Long[] sids=conPurchaseOrg.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPurchaseOrg.setSid(id);
                row=conPurchaseOrgMapper.updateById( conPurchaseOrg);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conPurchaseOrg.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConPurchaseOrg> getConPurchaseOrgList() {
        return conPurchaseOrgMapper.getConPurchaseOrgList();
    }

}
