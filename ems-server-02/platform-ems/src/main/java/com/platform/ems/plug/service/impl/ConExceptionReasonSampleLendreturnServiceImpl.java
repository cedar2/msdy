package com.platform.ems.plug.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConExceptionReasonSampleLendreturnMapper;
import com.platform.ems.plug.domain.ConExceptionReasonSampleLendreturn;
import com.platform.ems.plug.service.IConExceptionReasonSampleLendreturnService;

/**
 * 异常明细配置Service业务层处理
 *
 * @author yangqz
 * @date 2022-04-25
 */
@Service
@SuppressWarnings("all")
public class ConExceptionReasonSampleLendreturnServiceImpl extends ServiceImpl<ConExceptionReasonSampleLendreturnMapper,ConExceptionReasonSampleLendreturn>  implements IConExceptionReasonSampleLendreturnService {
    @Autowired
    private ConExceptionReasonSampleLendreturnMapper conExceptionReasonSampleLendreturnMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "异常明细配置";
    /**
     * 查询异常明细配置
     *
     * @param sid 异常明细配置ID
     * @return 异常明细配置
     */
    @Override
    public ConExceptionReasonSampleLendreturn selectConExceptionReasonSampleLendreturnById(Long sid) {
        ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn = conExceptionReasonSampleLendreturnMapper.selectConExceptionReasonSampleLendreturnById(sid);
        MongodbUtil.find(conExceptionReasonSampleLendreturn);
        return  conExceptionReasonSampleLendreturn;
    }

    /**
     * 查询异常明细配置列表
     *
     * @param conExceptionReasonSampleLendreturn 异常明细配置
     * @return 异常明细配置
     */
    @Override
    public List<ConExceptionReasonSampleLendreturn> selectConExceptionReasonSampleLendreturnList(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn) {
        return conExceptionReasonSampleLendreturnMapper.selectConExceptionReasonSampleLendreturnList(conExceptionReasonSampleLendreturn);
    }

    /**
     * 查询异常明细配置下拉列表
     *
     */
    @Override
    public List<ConExceptionReasonSampleLendreturn> getList(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn) {
        return conExceptionReasonSampleLendreturnMapper.getList(conExceptionReasonSampleLendreturn);
    }
    /**
     * 新增异常明细配置
     * 需要注意编码重复校验
     * @param conExceptionReasonSampleLendreturn 异常明细配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConExceptionReasonSampleLendreturn(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn) {
        int row= conExceptionReasonSampleLendreturnMapper.insert(conExceptionReasonSampleLendreturn);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conExceptionReasonSampleLendreturn.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改异常明细配置
     *
     * @param conExceptionReasonSampleLendreturn 异常明细配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConExceptionReasonSampleLendreturn(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn) {
        ConExceptionReasonSampleLendreturn response = conExceptionReasonSampleLendreturnMapper.selectConExceptionReasonSampleLendreturnById(conExceptionReasonSampleLendreturn.getSid());
        int row=conExceptionReasonSampleLendreturnMapper.updateById(conExceptionReasonSampleLendreturn);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conExceptionReasonSampleLendreturn.getSid(), BusinessType.UPDATE.ordinal(), response,conExceptionReasonSampleLendreturn,TITLE);
        }
        return row;
    }

    /**
     * 变更异常明细配置
     *
     * @param conExceptionReasonSampleLendreturn 异常明细配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConExceptionReasonSampleLendreturn(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn) {
        ConExceptionReasonSampleLendreturn response = conExceptionReasonSampleLendreturnMapper.selectConExceptionReasonSampleLendreturnById(conExceptionReasonSampleLendreturn.getSid());
                                                                    int row=conExceptionReasonSampleLendreturnMapper.updateAllById(conExceptionReasonSampleLendreturn);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conExceptionReasonSampleLendreturn.getSid(), BusinessType.CHANGE.ordinal(), response,conExceptionReasonSampleLendreturn,TITLE);
        }
        return row;
    }

    /**
     * 批量删除异常明细配置
     *
     * @param sids 需要删除的异常明细配置ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConExceptionReasonSampleLendreturnByIds(List<Long> sids) {
        return conExceptionReasonSampleLendreturnMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conExceptionReasonSampleLendreturn
    * @return
    */
    @Override
    public int changeStatus(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn){
        int row=0;
        Long[] sids=conExceptionReasonSampleLendreturn.getSidList();
        if(sids!=null&&sids.length>0){
            row=conExceptionReasonSampleLendreturnMapper.update(null, new UpdateWrapper<ConExceptionReasonSampleLendreturn>().lambda().set(ConExceptionReasonSampleLendreturn::getStatus ,conExceptionReasonSampleLendreturn.getStatus() )
                    .in(ConExceptionReasonSampleLendreturn::getSid,sids));
            for(Long id:sids){
                conExceptionReasonSampleLendreturn.setSid(id);
                row=conExceptionReasonSampleLendreturnMapper.updateById( conExceptionReasonSampleLendreturn);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conExceptionReasonSampleLendreturn.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conExceptionReasonSampleLendreturn.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conExceptionReasonSampleLendreturn
     * @return
     */
    @Override
    public int check(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn){
        int row=0;
        Long[] sids=conExceptionReasonSampleLendreturn.getSidList();
        if(sids!=null&&sids.length>0){
            row=conExceptionReasonSampleLendreturnMapper.update(null,new UpdateWrapper<ConExceptionReasonSampleLendreturn>().lambda().set(ConExceptionReasonSampleLendreturn::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConExceptionReasonSampleLendreturn::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
