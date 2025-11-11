package com.platform.ems.plug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConDocTypeOutsourceDn;
import com.platform.ems.plug.mapper.ConDocTypeOutsourceDnMapper;
import com.platform.ems.plug.service.IConDocTypeOutsourceDnService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 单据类型_外发加工交货单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeOutsourceDnServiceImpl extends ServiceImpl<ConDocTypeOutsourceDnMapper,ConDocTypeOutsourceDn>  implements IConDocTypeOutsourceDnService {
    @Autowired
    private ConDocTypeOutsourceDnMapper conDocTypeOutsourceDnMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_外发加工交货单";
    /**
     * 查询单据类型_外发加工交货单
     *
     * @param sid 单据类型_外发加工交货单ID
     * @return 单据类型_外发加工交货单
     */
    @Override
    public ConDocTypeOutsourceDn selectConDocTypeOutsourceDnById(Long sid) {
        ConDocTypeOutsourceDn conDocTypeOutsourceDn = conDocTypeOutsourceDnMapper.selectConDocTypeOutsourceDnById(sid);
        MongodbUtil.find(conDocTypeOutsourceDn);
        return  conDocTypeOutsourceDn;
    }

    /**
     * 查询单据类型_外发加工交货单列表
     *
     * @param conDocTypeOutsourceDn 单据类型_外发加工交货单
     * @return 单据类型_外发加工交货单
     */
    @Override
    public List<ConDocTypeOutsourceDn> selectConDocTypeOutsourceDnList(ConDocTypeOutsourceDn conDocTypeOutsourceDn) {
        return conDocTypeOutsourceDnMapper.selectConDocTypeOutsourceDnList(conDocTypeOutsourceDn);
    }

    /**
     * 新增单据类型_外发加工交货单
     * 需要注意编码重复校验
     * @param conDocTypeOutsourceDn 单据类型_外发加工交货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeOutsourceDn(ConDocTypeOutsourceDn conDocTypeOutsourceDn) {
        String name = conDocTypeOutsourceDn.getName();
        String code = conDocTypeOutsourceDn.getCode();
        List<ConDocTypeOutsourceDn> list = conDocTypeOutsourceDnMapper.selectList(new QueryWrapper<ConDocTypeOutsourceDn>().lambda()
                .or().eq(ConDocTypeOutsourceDn::getName, name)
                .or().eq(ConDocTypeOutsourceDn::getCode, code)
        );
        if(CollectionUtils.isNotEmpty(list)){
            throw new CustomException("配置档案已存在相同的名称或编码，不允许重复");
        }
        int row= conDocTypeOutsourceDnMapper.insert(conDocTypeOutsourceDn);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeOutsourceDn.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_外发加工交货单
     *
     * @param conDocTypeOutsourceDn 单据类型_外发加工交货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeOutsourceDn(ConDocTypeOutsourceDn conDocTypeOutsourceDn) {
        ConDocTypeOutsourceDn response = conDocTypeOutsourceDnMapper.selectConDocTypeOutsourceDnById(conDocTypeOutsourceDn.getSid());
        int row=conDocTypeOutsourceDnMapper.updateById(conDocTypeOutsourceDn);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeOutsourceDn.getSid(), BusinessType.UPDATE.ordinal(), response,conDocTypeOutsourceDn,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_外发加工交货单
     *
     * @param conDocTypeOutsourceDn 单据类型_外发加工交货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeOutsourceDn(ConDocTypeOutsourceDn conDocTypeOutsourceDn) {
        String name = conDocTypeOutsourceDn.getName();
        ConDocTypeOutsourceDn item = conDocTypeOutsourceDnMapper.selectOne(new QueryWrapper<ConDocTypeOutsourceDn>().lambda()
                .eq(ConDocTypeOutsourceDn::getName, name)
        );
        if (item != null && !item.getSid().equals(conDocTypeOutsourceDn.getSid())) {
            throw new CustomException("配置档案已存在相同的名称，不允许重复");
        }
        ConDocTypeOutsourceDn response = conDocTypeOutsourceDnMapper.selectConDocTypeOutsourceDnById(conDocTypeOutsourceDn.getSid());
        int row = conDocTypeOutsourceDnMapper.updateAllById(conDocTypeOutsourceDn);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeOutsourceDn.getSid(), BusinessType.CHANGE.ordinal(), response, conDocTypeOutsourceDn, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_外发加工交货单
     *
     * @param sids 需要删除的单据类型_外发加工交货单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeOutsourceDnByIds(List<Long> sids) {
        return conDocTypeOutsourceDnMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeOutsourceDn
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeOutsourceDn conDocTypeOutsourceDn){
        int row=0;
        Long[] sids=conDocTypeOutsourceDn.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeOutsourceDn.setSid(id);
                row=conDocTypeOutsourceDnMapper.updateById( conDocTypeOutsourceDn);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeOutsourceDn.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeOutsourceDn.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeOutsourceDn
     * @return
     */
    @Override
    public int check(ConDocTypeOutsourceDn conDocTypeOutsourceDn){
        int row=0;
        Long[] sids=conDocTypeOutsourceDn.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeOutsourceDn.setSid(id);
                row=conDocTypeOutsourceDnMapper.updateById( conDocTypeOutsourceDn);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeOutsourceDn.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDocTypeOutsourceDn> getConDocTypeOutsourceDnList() {
        return conDocTypeOutsourceDnMapper.getConDocTypeOutsourceDnList();
    }
}
