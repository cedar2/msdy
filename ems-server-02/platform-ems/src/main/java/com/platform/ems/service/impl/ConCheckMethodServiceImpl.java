package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ConCheckItem;
import com.platform.ems.domain.ConCheckMethod;
import com.platform.ems.domain.ConCheckStandard;
import com.platform.ems.mapper.ConCheckMethodMapper;
import com.platform.ems.service.IConCheckMethodService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测方法Service业务层处理
 *
 * @author qhq
 * @date 2021-11-01
 */
@Service
@SuppressWarnings("all")
public class ConCheckMethodServiceImpl extends ServiceImpl<ConCheckMethodMapper,ConCheckMethod>  implements IConCheckMethodService {
    @Autowired
    private ConCheckMethodMapper conCheckMethodMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "检测方法";
    /**
     * 查询检测方法
     *
     * @param sid 检测方法ID
     * @return 检测方法
     */
    @Override
    public ConCheckMethod selectConCheckMethodById(Long sid) {
        ConCheckMethod conCheckMethod = conCheckMethodMapper.selectConCheckMethodById(sid);
        MongodbUtil.find(conCheckMethod);
        return  conCheckMethod;
    }

    /**
     * 查询检测方法列表
     *
     * @param conCheckMethod 检测方法
     * @return 检测方法
     */
    @Override
    public List<ConCheckMethod> selectConCheckMethodList(ConCheckMethod conCheckMethod) {
        return conCheckMethodMapper.selectConCheckMethodList(conCheckMethod);
    }

    /**
     * 新增检测方法
     * 需要注意编码重复校验
     * @param conCheckMethod 检测方法
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConCheckMethod(ConCheckMethod conCheckMethod) {
        matchingCode(conCheckMethod);
        matchingName(conCheckMethod);
        int row= conCheckMethodMapper.insert(conCheckMethod);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conCheckMethod.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改检测方法
     *
     * @param conCheckMethod 检测方法
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConCheckMethod(ConCheckMethod conCheckMethod) {
        ConCheckMethod response = conCheckMethodMapper.selectConCheckMethodById(conCheckMethod.getSid());
        if(!response.getCode().equals(conCheckMethod.getCode())){
            matchingCode(conCheckMethod);
        }
        if(!response.getName().equals(conCheckMethod.getName())){
            matchingName(conCheckMethod);
        }
        int row=conCheckMethodMapper.updateById(conCheckMethod);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCheckMethod.getSid(), BusinessType.UPDATE.ordinal(), response,conCheckMethod,TITLE);
        }
        return row;
    }

    /**
     * 变更检测方法
     *
     * @param conCheckMethod 检测方法
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConCheckMethod(ConCheckMethod conCheckMethod) {
        ConCheckMethod response = conCheckMethodMapper.selectConCheckMethodById(conCheckMethod.getSid());
        if(!response.getCode().equals(conCheckMethod.getCode())){
            matchingCode(conCheckMethod);
        }
        if(!response.getName().equals(conCheckMethod.getName())){
            matchingName(conCheckMethod);
        }
        int row=conCheckMethodMapper.updateAllById(conCheckMethod);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCheckMethod.getSid(), BusinessType.CHANGE.ordinal(), response,conCheckMethod,TITLE);
        }
        return row;
    }

    /**
     * 批量删除检测方法
     *
     * @param sids 需要删除的检测方法ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConCheckMethodByIds(List<Long> sids) {
        return conCheckMethodMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conCheckMethod
    * @return
    */
    @Override
    public int changeStatus(ConCheckMethod conCheckMethod){
        int row=0;
        Long[] sids=conCheckMethod.getSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckMethodMapper.update(null, new UpdateWrapper<ConCheckMethod>().lambda().set(ConCheckMethod::getStatus ,conCheckMethod.getStatus() )
                    .in(ConCheckMethod::getSid,sids));
            for(Long id:sids){
                conCheckMethod.setSid(id);
                row=conCheckMethodMapper.updateById( conCheckMethod);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conCheckMethod.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conCheckMethod.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conCheckMethod
     * @return
     */
    @Override
    public int check(ConCheckMethod conCheckMethod){
        int row=0;
        Long[] sids=conCheckMethod.getSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckMethodMapper.update(null,new UpdateWrapper<ConCheckMethod>().lambda().set(ConCheckMethod::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConCheckMethod::getSid,sids));
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
     * @param conCheckStandard
     */
    public void matchingCode(ConCheckMethod conCheckMethod){
        QueryWrapper<ConCheckMethod> wrapper = new QueryWrapper<>();
        wrapper.eq("code",conCheckMethod.getCode());
        int codeMatching = conCheckMethodMapper.selectList(wrapper).size();
        if(codeMatching>0){
            throw new CustomException("已存在相同编码，请重新输入！");
        }
    }

    /**
     * 验证名称重复
     */
    public void matchingName(ConCheckMethod conCheckMethod){
        QueryWrapper<ConCheckMethod> wrapper = new QueryWrapper<>();
        wrapper.eq("name",conCheckMethod.getName());
        int codeMatching = conCheckMethodMapper.selectList(wrapper).size();
        if(codeMatching>0){
            throw new CustomException("已存在相同名称，请重新输入！");
        }
    }
}
