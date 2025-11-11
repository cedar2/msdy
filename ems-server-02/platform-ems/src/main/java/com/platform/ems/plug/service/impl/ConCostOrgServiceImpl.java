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
import com.platform.ems.plug.domain.ConCostOrg;
import com.platform.ems.plug.mapper.ConCostOrgMapper;
import com.platform.ems.plug.service.IConCostOrgService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 成本组织Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConCostOrgServiceImpl extends ServiceImpl<ConCostOrgMapper,ConCostOrg>  implements IConCostOrgService {
    @Autowired
    private ConCostOrgMapper conCostOrgMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "成本组织";
    /**
     * 查询成本组织
     *
     * @param sid 成本组织ID
     * @return 成本组织
     */
    @Override
    public ConCostOrg selectConCostOrgById(Long sid) {
        ConCostOrg conCostOrg = conCostOrgMapper.selectConCostOrgById(sid);
        MongodbUtil.find(conCostOrg);
        return  conCostOrg;
    }

    /**
     * 查询成本组织列表
     *
     * @param conCostOrg 成本组织
     * @return 成本组织
     */
    @Override
    public List<ConCostOrg> selectConCostOrgList(ConCostOrg conCostOrg) {
        return conCostOrgMapper.selectConCostOrgList(conCostOrg);
    }

    /**
     * 查询成本组织列表-下拉框
     *
     * @param conCostOrg 成本组织
     * @return 成本组织
     */
    @Override
    public List<ConCostOrg> getCostOrgList(ConCostOrg conCostOrg) {
        return conCostOrgMapper.getCostOrgList(conCostOrg);
    }

    /**
     * 新增成本组织
     * 需要注意编码重复校验
     * @param conCostOrg 成本组织
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConCostOrg(ConCostOrg conCostOrg) {
        List<ConCostOrg> codeList = conCostOrgMapper.selectList(new QueryWrapper<ConCostOrg>().lambda()
                .eq(ConCostOrg::getCode, conCostOrg.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConCostOrg> nameList = conCostOrgMapper.selectList(new QueryWrapper<ConCostOrg>().lambda()
                .eq(ConCostOrg::getName, conCostOrg.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conCostOrgMapper.insert(conCostOrg);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conCostOrg.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改成本组织
     *
     * @param conCostOrg 成本组织
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConCostOrg(ConCostOrg conCostOrg) {
        ConCostOrg response = conCostOrgMapper.selectConCostOrgById(conCostOrg.getSid());
        int row=conCostOrgMapper.updateById(conCostOrg);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCostOrg.getSid(), BusinessType.UPDATE.getValue(), response,conCostOrg,TITLE);
        }
        return row;
    }

    /**
     * 变更成本组织
     *
     * @param conCostOrg 成本组织
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConCostOrg(ConCostOrg conCostOrg) {
        List<ConCostOrg> nameList = conCostOrgMapper.selectList(new QueryWrapper<ConCostOrg>().lambda()
                .eq(ConCostOrg::getName, conCostOrg.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conCostOrg.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conCostOrg.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConCostOrg response = conCostOrgMapper.selectConCostOrgById(conCostOrg.getSid());
        int row = conCostOrgMapper.updateAllById(conCostOrg);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conCostOrg.getSid(), BusinessType.CHANGE.getValue(), response, conCostOrg, TITLE);
        }
        return row;
    }

    /**
     * 批量删除成本组织
     *
     * @param sids 需要删除的成本组织ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConCostOrgByIds(List<Long> sids) {
        return conCostOrgMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conCostOrg
    * @return
    */
    @Override
    public int changeStatus(ConCostOrg conCostOrg){
        int row=0;
        Long[] sids=conCostOrg.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conCostOrg.setSid(id);
                row=conCostOrgMapper.updateById( conCostOrg);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conCostOrg.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conCostOrg.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conCostOrg
     * @return
     */
    @Override
    public int check(ConCostOrg conCostOrg){
        int row=0;
        Long[] sids=conCostOrg.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conCostOrg.setSid(id);
                row=conCostOrgMapper.updateById( conCostOrg);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conCostOrg.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
