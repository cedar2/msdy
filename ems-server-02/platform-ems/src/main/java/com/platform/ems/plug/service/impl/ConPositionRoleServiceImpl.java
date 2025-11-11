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
import com.platform.ems.plug.domain.ConPositionRole;
import com.platform.ems.plug.mapper.ConPositionRoleMapper;
import com.platform.ems.plug.service.IConPositionRoleService;
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
 * 工作角色Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConPositionRoleServiceImpl extends ServiceImpl<ConPositionRoleMapper,ConPositionRole>  implements IConPositionRoleService {
    @Autowired
    private ConPositionRoleMapper conPositionRoleMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "工作角色";
    /**
     * 查询工作角色
     *
     * @param sid 工作角色ID
     * @return 工作角色
     */
    @Override
    public ConPositionRole selectConPositionRoleById(Long sid) {
        ConPositionRole conPositionRole = conPositionRoleMapper.selectConPositionRoleById(sid);
        MongodbUtil.find(conPositionRole);
        return  conPositionRole;
    }

    /**
     * 查询工作角色列表
     *
     * @param conPositionRole 工作角色
     * @return 工作角色
     */
    @Override
    public List<ConPositionRole> selectConPositionRoleList(ConPositionRole conPositionRole) {
        return conPositionRoleMapper.selectConPositionRoleList(conPositionRole);
    }

    /**
     * 新增工作角色
     * 需要注意编码重复校验
     * @param conPositionRole 工作角色
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConPositionRole(ConPositionRole conPositionRole) {
        List<ConPositionRole> codeList = conPositionRoleMapper.selectList(new QueryWrapper<ConPositionRole>().lambda()
                .eq(ConPositionRole::getCode, conPositionRole.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConPositionRole> nameList = conPositionRoleMapper.selectList(new QueryWrapper<ConPositionRole>().lambda()
                .eq(ConPositionRole::getName, conPositionRole.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conPositionRoleMapper.insert(conPositionRole);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conPositionRole.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改工作角色
     *
     * @param conPositionRole 工作角色
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConPositionRole(ConPositionRole conPositionRole) {
        ConPositionRole response = conPositionRoleMapper.selectConPositionRoleById(conPositionRole.getSid());
        int row=conPositionRoleMapper.updateById(conPositionRole);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conPositionRole.getSid(), BusinessType.UPDATE.getValue(), response,conPositionRole,TITLE);
        }
        return row;
    }

    /**
     * 变更工作角色
     *
     * @param conPositionRole 工作角色
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConPositionRole(ConPositionRole conPositionRole) {
        List<ConPositionRole> nameList = conPositionRoleMapper.selectList(new QueryWrapper<ConPositionRole>().lambda()
                .eq(ConPositionRole::getName, conPositionRole.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conPositionRole.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conPositionRole.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConPositionRole response = conPositionRoleMapper.selectConPositionRoleById(conPositionRole.getSid());
        int row = conPositionRoleMapper.updateAllById(conPositionRole);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPositionRole.getSid(), BusinessType.CHANGE.getValue(), response, conPositionRole, TITLE);
        }
        return row;
    }

    /**
     * 批量删除工作角色
     *
     * @param sids 需要删除的工作角色ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConPositionRoleByIds(List<Long> sids) {
        return conPositionRoleMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conPositionRole
    * @return
    */
    @Override
    public int changeStatus(ConPositionRole conPositionRole){
        int row=0;
        Long[] sids=conPositionRole.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPositionRole.setSid(id);
                row=conPositionRoleMapper.updateById( conPositionRole);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conPositionRole.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conPositionRole.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conPositionRole
     * @return
     */
    @Override
    public int check(ConPositionRole conPositionRole){
        int row=0;
        Long[] sids=conPositionRole.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPositionRole.setSid(id);
                row=conPositionRoleMapper.updateById( conPositionRole);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conPositionRole.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
