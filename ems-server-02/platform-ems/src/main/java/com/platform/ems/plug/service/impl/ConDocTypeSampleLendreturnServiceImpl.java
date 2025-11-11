package com.platform.ems.plug.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.plug.domain.ConDocTypeSampleLendreturn;
import com.platform.ems.plug.mapper.ConDocTypeSampleLendreturnMapper;
import com.platform.ems.plug.service.IConDocTypeSampleLendreturnService;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;


/**
 * 单据类型_样品借还单Service业务层处理
 *
 * @author linhongwei
 * @date 2022-01-24
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeSampleLendreturnServiceImpl extends ServiceImpl<ConDocTypeSampleLendreturnMapper,ConDocTypeSampleLendreturn>  implements IConDocTypeSampleLendreturnService {
    @Autowired
    private ConDocTypeSampleLendreturnMapper conDocTypeSampleLendreturnMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_样品借还单";
    /**
     * 查询单据类型_样品借还单
     *
     * @param sid 单据类型_样品借还单ID
     * @return 单据类型_样品借还单
     */
    @Override
    public ConDocTypeSampleLendreturn selectConDocTypeSampleLendreturnById(Long sid) {
        ConDocTypeSampleLendreturn conDocTypeSampleLendreturn = conDocTypeSampleLendreturnMapper.selectConDocTypeSampleLendreturnById(sid);
        MongodbUtil.find(conDocTypeSampleLendreturn);
        return  conDocTypeSampleLendreturn;
    }

    /**
     * 查询单据类型_样品借还单列表
     *
     * @param conDocTypeSampleLendreturn 单据类型_样品借还单
     * @return 单据类型_样品借还单
     */
    @Override
    public List<ConDocTypeSampleLendreturn> selectConDocTypeSampleLendreturnList(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        return conDocTypeSampleLendreturnMapper.selectConDocTypeSampleLendreturnList(conDocTypeSampleLendreturn);
    }

    /**
     * 新增单据类型_样品借还单
     * 需要注意编码重复校验
     * @param conDocTypeSampleLendreturn 单据类型_样品借还单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeSampleLendreturn(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        int row= conDocTypeSampleLendreturnMapper.insert(conDocTypeSampleLendreturn);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeSampleLendreturn.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_样品借还单
     *
     * @param conDocTypeSampleLendreturn 单据类型_样品借还单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeSampleLendreturn(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        ConDocTypeSampleLendreturn response = conDocTypeSampleLendreturnMapper.selectConDocTypeSampleLendreturnById(conDocTypeSampleLendreturn.getSid());
        int row=conDocTypeSampleLendreturnMapper.updateById(conDocTypeSampleLendreturn);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeSampleLendreturn.getSid(), BusinessType.UPDATE.ordinal(), response,conDocTypeSampleLendreturn,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_样品借还单
     *
     * @param conDocTypeSampleLendreturn 单据类型_样品借还单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeSampleLendreturn(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        ConDocTypeSampleLendreturn response = conDocTypeSampleLendreturnMapper.selectConDocTypeSampleLendreturnById(conDocTypeSampleLendreturn.getSid());
                                                                        int row=conDocTypeSampleLendreturnMapper.updateAllById(conDocTypeSampleLendreturn);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeSampleLendreturn.getSid(), BusinessType.CHANGE.ordinal(), response,conDocTypeSampleLendreturn,TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_样品借还单
     *
     * @param sids 需要删除的单据类型_样品借还单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeSampleLendreturnByIds(List<Long> sids) {
        return conDocTypeSampleLendreturnMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeSampleLendreturn
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn){
        int row=0;
        Long[] sids=conDocTypeSampleLendreturn.getSidList();
        if(sids!=null&&sids.length>0){
            row=conDocTypeSampleLendreturnMapper.update(null, new UpdateWrapper<ConDocTypeSampleLendreturn>().lambda().set(ConDocTypeSampleLendreturn::getStatus ,conDocTypeSampleLendreturn.getStatus() )
                    .in(ConDocTypeSampleLendreturn::getSid,sids));
            for(Long id:sids){
                conDocTypeSampleLendreturn.setSid(id);
                row=conDocTypeSampleLendreturnMapper.updateById( conDocTypeSampleLendreturn);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeSampleLendreturn.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeSampleLendreturn.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeSampleLendreturn
     * @return
     */
    @Override
    public int check(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn){
        int row=0;
        Long[] sids=conDocTypeSampleLendreturn.getSidList();
        if(sids!=null&&sids.length>0){
            row=conDocTypeSampleLendreturnMapper.update(null,new UpdateWrapper<ConDocTypeSampleLendreturn>().lambda().set(ConDocTypeSampleLendreturn::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConDocTypeSampleLendreturn::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
