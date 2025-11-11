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
import com.platform.ems.plug.domain.ConDocTypeOutsourcePo;
import com.platform.ems.plug.mapper.ConDocTypeOutsourcePoMapper;
import com.platform.ems.plug.service.IConDocTypeOutsourcePoService;
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
 * 单据类型_外发加工单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeOutsourcePoServiceImpl extends ServiceImpl<ConDocTypeOutsourcePoMapper,ConDocTypeOutsourcePo>  implements IConDocTypeOutsourcePoService {
    @Autowired
    private ConDocTypeOutsourcePoMapper conDocTypeOutsourcePoMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_外发加工单";
    /**
     * 查询单据类型_外发加工单
     *
     * @param sid 单据类型_外发加工单ID
     * @return 单据类型_外发加工单
     */
    @Override
    public ConDocTypeOutsourcePo selectConDocTypeOutsourcePoById(Long sid) {
        ConDocTypeOutsourcePo conDocTypeOutsourcePo = conDocTypeOutsourcePoMapper.selectConDocTypeOutsourcePoById(sid);
        MongodbUtil.find(conDocTypeOutsourcePo);
        return  conDocTypeOutsourcePo;
    }

    /**
     * 查询单据类型_外发加工单列表
     *
     * @param conDocTypeOutsourcePo 单据类型_外发加工单
     * @return 单据类型_外发加工单
     */
    @Override
    public List<ConDocTypeOutsourcePo> selectConDocTypeOutsourcePoList(ConDocTypeOutsourcePo conDocTypeOutsourcePo) {
        return conDocTypeOutsourcePoMapper.selectConDocTypeOutsourcePoList(conDocTypeOutsourcePo);
    }

    /**
     * 新增单据类型_外发加工单
     * 需要注意编码重复校验
     * @param conDocTypeOutsourcePo 单据类型_外发加工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeOutsourcePo(ConDocTypeOutsourcePo conDocTypeOutsourcePo) {
        List<ConDocTypeOutsourcePo> codeList = conDocTypeOutsourcePoMapper.selectList(new QueryWrapper<ConDocTypeOutsourcePo>().lambda()
                .eq(ConDocTypeOutsourcePo::getCode, conDocTypeOutsourcePo.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeOutsourcePo> nameList = conDocTypeOutsourcePoMapper.selectList(new QueryWrapper<ConDocTypeOutsourcePo>().lambda()
                .eq(ConDocTypeOutsourcePo::getName, conDocTypeOutsourcePo.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeOutsourcePoMapper.insert(conDocTypeOutsourcePo);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeOutsourcePo.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_外发加工单
     *
     * @param conDocTypeOutsourcePo 单据类型_外发加工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeOutsourcePo(ConDocTypeOutsourcePo conDocTypeOutsourcePo) {
        ConDocTypeOutsourcePo response = conDocTypeOutsourcePoMapper.selectConDocTypeOutsourcePoById(conDocTypeOutsourcePo.getSid());
        int row=conDocTypeOutsourcePoMapper.updateById(conDocTypeOutsourcePo);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeOutsourcePo.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeOutsourcePo,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_外发加工单
     *
     * @param conDocTypeOutsourcePo 单据类型_外发加工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeOutsourcePo(ConDocTypeOutsourcePo conDocTypeOutsourcePo) {
        List<ConDocTypeOutsourcePo> nameList = conDocTypeOutsourcePoMapper.selectList(new QueryWrapper<ConDocTypeOutsourcePo>().lambda()
                .eq(ConDocTypeOutsourcePo::getName, conDocTypeOutsourcePo.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeOutsourcePo.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeOutsourcePo.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeOutsourcePo response = conDocTypeOutsourcePoMapper.selectConDocTypeOutsourcePoById(conDocTypeOutsourcePo.getSid());
        int row = conDocTypeOutsourcePoMapper.updateAllById(conDocTypeOutsourcePo);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeOutsourcePo.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeOutsourcePo, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_外发加工单
     *
     * @param sids 需要删除的单据类型_外发加工单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeOutsourcePoByIds(List<Long> sids) {
        return conDocTypeOutsourcePoMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeOutsourcePo
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeOutsourcePo conDocTypeOutsourcePo){
        int row=0;
        Long[] sids=conDocTypeOutsourcePo.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeOutsourcePo.setSid(id);
                row=conDocTypeOutsourcePoMapper.updateById( conDocTypeOutsourcePo);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeOutsourcePo.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeOutsourcePo.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeOutsourcePo
     * @return
     */
    @Override
    public int check(ConDocTypeOutsourcePo conDocTypeOutsourcePo){
        int row=0;
        Long[] sids=conDocTypeOutsourcePo.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeOutsourcePo.setSid(id);
                row=conDocTypeOutsourcePoMapper.updateById( conDocTypeOutsourcePo);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeOutsourcePo.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDocTypeOutsourcePo> getConDocTypeOutsourcePoList() {
        return conDocTypeOutsourcePoMapper.getConDocTypeOutsourcePoList();
    }
}
