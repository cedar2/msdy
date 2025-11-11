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
import com.platform.ems.plug.domain.ConBuTypeOutsourcePo;
import com.platform.ems.plug.mapper.ConBuTypeOutsourcePoMapper;
import com.platform.ems.plug.service.IConBuTypeOutsourcePoService;
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
 * 业务类型_外发加工单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeOutsourcePoServiceImpl extends ServiceImpl<ConBuTypeOutsourcePoMapper,ConBuTypeOutsourcePo>  implements IConBuTypeOutsourcePoService {
    @Autowired
    private ConBuTypeOutsourcePoMapper conBuTypeOutsourcePoMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_外发加工单";
    /**
     * 查询业务类型_外发加工单
     *
     * @param sid 业务类型_外发加工单ID
     * @return 业务类型_外发加工单
     */
    @Override
    public ConBuTypeOutsourcePo selectConBuTypeOutsourcePoById(Long sid) {
        ConBuTypeOutsourcePo conBuTypeOutsourcePo = conBuTypeOutsourcePoMapper.selectConBuTypeOutsourcePoById(sid);
        MongodbUtil.find(conBuTypeOutsourcePo);
        return  conBuTypeOutsourcePo;
    }

    /**
     * 查询业务类型_外发加工单列表
     *
     * @param conBuTypeOutsourcePo 业务类型_外发加工单
     * @return 业务类型_外发加工单
     */
    @Override
    public List<ConBuTypeOutsourcePo> selectConBuTypeOutsourcePoList(ConBuTypeOutsourcePo conBuTypeOutsourcePo) {
        return conBuTypeOutsourcePoMapper.selectConBuTypeOutsourcePoList(conBuTypeOutsourcePo);
    }

    /**
     * 新增业务类型_外发加工单
     * 需要注意编码重复校验
     * @param conBuTypeOutsourcePo 业务类型_外发加工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeOutsourcePo(ConBuTypeOutsourcePo conBuTypeOutsourcePo) {
        List<ConBuTypeOutsourcePo> codeList = conBuTypeOutsourcePoMapper.selectList(new QueryWrapper<ConBuTypeOutsourcePo>().lambda()
                .eq(ConBuTypeOutsourcePo::getCode, conBuTypeOutsourcePo.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeOutsourcePo> nameList = conBuTypeOutsourcePoMapper.selectList(new QueryWrapper<ConBuTypeOutsourcePo>().lambda()
                .eq(ConBuTypeOutsourcePo::getName, conBuTypeOutsourcePo.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeOutsourcePoMapper.insert(conBuTypeOutsourcePo);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeOutsourcePo.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_外发加工单
     *
     * @param conBuTypeOutsourcePo 业务类型_外发加工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeOutsourcePo(ConBuTypeOutsourcePo conBuTypeOutsourcePo) {
        ConBuTypeOutsourcePo response = conBuTypeOutsourcePoMapper.selectConBuTypeOutsourcePoById(conBuTypeOutsourcePo.getSid());
        int row=conBuTypeOutsourcePoMapper.updateById(conBuTypeOutsourcePo);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeOutsourcePo.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeOutsourcePo,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_外发加工单
     *
     * @param conBuTypeOutsourcePo 业务类型_外发加工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeOutsourcePo(ConBuTypeOutsourcePo conBuTypeOutsourcePo) {
        List<ConBuTypeOutsourcePo> nameList = conBuTypeOutsourcePoMapper.selectList(new QueryWrapper<ConBuTypeOutsourcePo>().lambda()
                .eq(ConBuTypeOutsourcePo::getName, conBuTypeOutsourcePo.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeOutsourcePo.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeOutsourcePo.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeOutsourcePo response = conBuTypeOutsourcePoMapper.selectConBuTypeOutsourcePoById(conBuTypeOutsourcePo.getSid());
        int row = conBuTypeOutsourcePoMapper.updateAllById(conBuTypeOutsourcePo);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeOutsourcePo.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeOutsourcePo, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_外发加工单
     *
     * @param sids 需要删除的业务类型_外发加工单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeOutsourcePoByIds(List<Long> sids) {
        return conBuTypeOutsourcePoMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeOutsourcePo
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeOutsourcePo conBuTypeOutsourcePo){
        int row=0;
        Long[] sids=conBuTypeOutsourcePo.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeOutsourcePo.setSid(id);
                row=conBuTypeOutsourcePoMapper.updateById( conBuTypeOutsourcePo);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeOutsourcePo.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeOutsourcePo.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeOutsourcePo
     * @return
     */
    @Override
    public int check(ConBuTypeOutsourcePo conBuTypeOutsourcePo){
        int row=0;
        Long[] sids=conBuTypeOutsourcePo.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeOutsourcePo.setSid(id);
                row=conBuTypeOutsourcePoMapper.updateById( conBuTypeOutsourcePo);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeOutsourcePo.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConBuTypeOutsourcePo> getConBuTypeOutsourcePoList() {
        return conBuTypeOutsourcePoMapper.getConBuTypeOutsourcePoList();
    }

}
