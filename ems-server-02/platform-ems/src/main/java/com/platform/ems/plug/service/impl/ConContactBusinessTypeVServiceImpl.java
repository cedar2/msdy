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
import com.platform.ems.plug.domain.ConContactBusinessTypeV;
import com.platform.ems.plug.mapper.ConContactBusinessTypeVMapper;
import com.platform.ems.plug.service.IConContactBusinessTypeVService;
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
 * 对接业务类型_供应商Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConContactBusinessTypeVServiceImpl extends ServiceImpl<ConContactBusinessTypeVMapper,ConContactBusinessTypeV>  implements IConContactBusinessTypeVService {
    @Autowired
    private ConContactBusinessTypeVMapper conContactBusinessTypeVMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "对接业务类型_供应商";
    /**
     * 查询对接业务类型_供应商
     *
     * @param sid 对接业务类型_供应商ID
     * @return 对接业务类型_供应商
     */
    @Override
    public ConContactBusinessTypeV selectConContactBusinessTypeVById(Long sid) {
        ConContactBusinessTypeV conContactBusinessTypeV = conContactBusinessTypeVMapper.selectConContactBusinessTypeVById(sid);
        MongodbUtil.find(conContactBusinessTypeV);
        return  conContactBusinessTypeV;
    }

    /**
     * 查询对接业务类型_供应商列表
     *
     * @param conContactBusinessTypeV 对接业务类型_供应商
     * @return 对接业务类型_供应商
     */
    @Override
    public List<ConContactBusinessTypeV> selectConContactBusinessTypeVList(ConContactBusinessTypeV conContactBusinessTypeV) {
        return conContactBusinessTypeVMapper.selectConContactBusinessTypeVList(conContactBusinessTypeV);
    }

    /**
     * 新增对接业务类型_供应商
     * 需要注意编码重复校验
     * @param conContactBusinessTypeV 对接业务类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConContactBusinessTypeV(ConContactBusinessTypeV conContactBusinessTypeV) {
        List<ConContactBusinessTypeV> codeList = conContactBusinessTypeVMapper.selectList(new QueryWrapper<ConContactBusinessTypeV>().lambda()
                .eq(ConContactBusinessTypeV::getCode, conContactBusinessTypeV.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConContactBusinessTypeV> nameList = conContactBusinessTypeVMapper.selectList(new QueryWrapper<ConContactBusinessTypeV>().lambda()
                .eq(ConContactBusinessTypeV::getName, conContactBusinessTypeV.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conContactBusinessTypeVMapper.insert(conContactBusinessTypeV);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conContactBusinessTypeV.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改对接业务类型_供应商
     *
     * @param conContactBusinessTypeV 对接业务类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConContactBusinessTypeV(ConContactBusinessTypeV conContactBusinessTypeV) {
        ConContactBusinessTypeV response = conContactBusinessTypeVMapper.selectConContactBusinessTypeVById(conContactBusinessTypeV.getSid());
        int row=conContactBusinessTypeVMapper.updateById(conContactBusinessTypeV);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conContactBusinessTypeV.getSid(), BusinessType.UPDATE.getValue(), response,conContactBusinessTypeV,TITLE);
        }
        return row;
    }

    /**
     * 变更对接业务类型_供应商
     *
     * @param conContactBusinessTypeV 对接业务类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConContactBusinessTypeV(ConContactBusinessTypeV conContactBusinessTypeV) {
        List<ConContactBusinessTypeV> nameList = conContactBusinessTypeVMapper.selectList(new QueryWrapper<ConContactBusinessTypeV>().lambda()
                .eq(ConContactBusinessTypeV::getName, conContactBusinessTypeV.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conContactBusinessTypeV.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conContactBusinessTypeV.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConContactBusinessTypeV response = conContactBusinessTypeVMapper.selectConContactBusinessTypeVById(conContactBusinessTypeV.getSid());
        int row = conContactBusinessTypeVMapper.updateAllById(conContactBusinessTypeV);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conContactBusinessTypeV.getSid(), BusinessType.CHANGE.getValue(), response, conContactBusinessTypeV, TITLE);
        }
        return row;
    }

    /**
     * 批量删除对接业务类型_供应商
     *
     * @param sids 需要删除的对接业务类型_供应商ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConContactBusinessTypeVByIds(List<Long> sids) {
        return conContactBusinessTypeVMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conContactBusinessTypeV
    * @return
    */
    @Override
    public int changeStatus(ConContactBusinessTypeV conContactBusinessTypeV){
        int row=0;
        Long[] sids=conContactBusinessTypeV.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conContactBusinessTypeV.setSid(id);
                row=conContactBusinessTypeVMapper.updateById( conContactBusinessTypeV);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conContactBusinessTypeV.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conContactBusinessTypeV.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conContactBusinessTypeV
     * @return
     */
    @Override
    public int check(ConContactBusinessTypeV conContactBusinessTypeV){
        int row=0;
        Long[] sids=conContactBusinessTypeV.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conContactBusinessTypeV.setSid(id);
                row=conContactBusinessTypeVMapper.updateById( conContactBusinessTypeV);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conContactBusinessTypeV.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
