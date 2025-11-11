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
import com.platform.ems.plug.domain.ConBuTypeOutsourceMi;
import com.platform.ems.plug.mapper.ConBuTypeOutsourceMiMapper;
import com.platform.ems.plug.service.IConBuTypeOutsourceMiService;
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
 * 业务类型_外发加工发料单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeOutsourceMiServiceImpl extends ServiceImpl<ConBuTypeOutsourceMiMapper,ConBuTypeOutsourceMi>  implements IConBuTypeOutsourceMiService {
    @Autowired
    private ConBuTypeOutsourceMiMapper conBuTypeOutsourceMiMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_外发加工发料单";
    /**
     * 查询业务类型_外发加工发料单
     *
     * @param sid 业务类型_外发加工发料单ID
     * @return 业务类型_外发加工发料单
     */
    @Override
    public ConBuTypeOutsourceMi selectConBuTypeOutsourceMiById(Long sid) {
        ConBuTypeOutsourceMi conBuTypeOutsourceMi = conBuTypeOutsourceMiMapper.selectConBuTypeOutsourceMiById(sid);
        MongodbUtil.find(conBuTypeOutsourceMi);
        return  conBuTypeOutsourceMi;
    }

    /**
     * 查询业务类型_外发加工发料单列表
     *
     * @param conBuTypeOutsourceMi 业务类型_外发加工发料单
     * @return 业务类型_外发加工发料单
     */
    @Override
    public List<ConBuTypeOutsourceMi> selectConBuTypeOutsourceMiList(ConBuTypeOutsourceMi conBuTypeOutsourceMi) {
        return conBuTypeOutsourceMiMapper.selectConBuTypeOutsourceMiList(conBuTypeOutsourceMi);
    }

    /**
     * 新增业务类型_外发加工发料单
     * 需要注意编码重复校验
     * @param conBuTypeOutsourceMi 业务类型_外发加工发料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeOutsourceMi(ConBuTypeOutsourceMi conBuTypeOutsourceMi) {
        List<ConBuTypeOutsourceMi> codeList = conBuTypeOutsourceMiMapper.selectList(new QueryWrapper<ConBuTypeOutsourceMi>().lambda()
                .eq(ConBuTypeOutsourceMi::getCode, conBuTypeOutsourceMi.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeOutsourceMi> nameList = conBuTypeOutsourceMiMapper.selectList(new QueryWrapper<ConBuTypeOutsourceMi>().lambda()
                .eq(ConBuTypeOutsourceMi::getName, conBuTypeOutsourceMi.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeOutsourceMiMapper.insert(conBuTypeOutsourceMi);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeOutsourceMi.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_外发加工发料单
     *
     * @param conBuTypeOutsourceMi 业务类型_外发加工发料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeOutsourceMi(ConBuTypeOutsourceMi conBuTypeOutsourceMi) {
        ConBuTypeOutsourceMi response = conBuTypeOutsourceMiMapper.selectConBuTypeOutsourceMiById(conBuTypeOutsourceMi.getSid());
        int row=conBuTypeOutsourceMiMapper.updateById(conBuTypeOutsourceMi);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeOutsourceMi.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeOutsourceMi,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_外发加工发料单
     *
     * @param conBuTypeOutsourceMi 业务类型_外发加工发料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeOutsourceMi(ConBuTypeOutsourceMi conBuTypeOutsourceMi) {
        List<ConBuTypeOutsourceMi> nameList = conBuTypeOutsourceMiMapper.selectList(new QueryWrapper<ConBuTypeOutsourceMi>().lambda()
                .eq(ConBuTypeOutsourceMi::getName, conBuTypeOutsourceMi.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeOutsourceMi.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeOutsourceMi.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeOutsourceMi response = conBuTypeOutsourceMiMapper.selectConBuTypeOutsourceMiById(conBuTypeOutsourceMi.getSid());
        int row = conBuTypeOutsourceMiMapper.updateAllById(conBuTypeOutsourceMi);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeOutsourceMi.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeOutsourceMi, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_外发加工发料单
     *
     * @param sids 需要删除的业务类型_外发加工发料单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeOutsourceMiByIds(List<Long> sids) {
        return conBuTypeOutsourceMiMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeOutsourceMi
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeOutsourceMi conBuTypeOutsourceMi){
        int row=0;
        Long[] sids=conBuTypeOutsourceMi.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeOutsourceMi.setSid(id);
                row=conBuTypeOutsourceMiMapper.updateById( conBuTypeOutsourceMi);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeOutsourceMi.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeOutsourceMi.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeOutsourceMi
     * @return
     */
    @Override
    public int check(ConBuTypeOutsourceMi conBuTypeOutsourceMi){
        int row=0;
        Long[] sids=conBuTypeOutsourceMi.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeOutsourceMi.setSid(id);
                row=conBuTypeOutsourceMiMapper.updateById( conBuTypeOutsourceMi);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeOutsourceMi.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
