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
import com.platform.ems.plug.domain.ConProductTechniqueType;
import com.platform.ems.plug.mapper.ConProductTechniqueTypeMapper;
import com.platform.ems.plug.service.IConProductTechniqueTypeService;
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
 * 生产工艺方法(编织方法)Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConProductTechniqueTypeServiceImpl extends ServiceImpl<ConProductTechniqueTypeMapper,ConProductTechniqueType>  implements IConProductTechniqueTypeService {
    @Autowired
    private ConProductTechniqueTypeMapper conProductTechniqueTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产工艺方法(编织方法)";
    /**
     * 查询生产工艺方法(编织方法)
     *
     * @param sid 生产工艺方法(编织方法)ID
     * @return 生产工艺方法(编织方法)
     */
    @Override
    public ConProductTechniqueType selectConProductTechniqueTypeById(Long sid) {
        ConProductTechniqueType conProductTechniqueType = conProductTechniqueTypeMapper.selectConProductTechniqueTypeById(sid);
        MongodbUtil.find(conProductTechniqueType);
        return  conProductTechniqueType;
    }

    /**
     * 查询生产工艺方法(编织方法)列表
     *
     * @param conProductTechniqueType 生产工艺方法(编织方法)
     * @return 生产工艺方法(编织方法)
     */
    @Override
    public List<ConProductTechniqueType> selectConProductTechniqueTypeList(ConProductTechniqueType conProductTechniqueType) {
        return conProductTechniqueTypeMapper.selectConProductTechniqueTypeList(conProductTechniqueType);
    }

    /**
     * 新增生产工艺方法(编织方法)
     * 需要注意编码重复校验
     * @param conProductTechniqueType 生产工艺方法(编织方法)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConProductTechniqueType(ConProductTechniqueType conProductTechniqueType) {
        List<ConProductTechniqueType> codeList = conProductTechniqueTypeMapper.selectList(new QueryWrapper<ConProductTechniqueType>().lambda()
                .eq(ConProductTechniqueType::getCode, conProductTechniqueType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConProductTechniqueType> nameList = conProductTechniqueTypeMapper.selectList(new QueryWrapper<ConProductTechniqueType>().lambda()
                .eq(ConProductTechniqueType::getName, conProductTechniqueType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conProductTechniqueTypeMapper.insert(conProductTechniqueType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conProductTechniqueType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改生产工艺方法(编织方法)
     *
     * @param conProductTechniqueType 生产工艺方法(编织方法)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConProductTechniqueType(ConProductTechniqueType conProductTechniqueType) {
        ConProductTechniqueType response = conProductTechniqueTypeMapper.selectConProductTechniqueTypeById(conProductTechniqueType.getSid());
        int row=conProductTechniqueTypeMapper.updateById(conProductTechniqueType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conProductTechniqueType.getSid(), BusinessType.UPDATE.getValue(), response,conProductTechniqueType,TITLE);
        }
        return row;
    }

    /**
     * 变更生产工艺方法(编织方法)
     *
     * @param conProductTechniqueType 生产工艺方法(编织方法)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConProductTechniqueType(ConProductTechniqueType conProductTechniqueType) {
        List<ConProductTechniqueType> nameList = conProductTechniqueTypeMapper.selectList(new QueryWrapper<ConProductTechniqueType>().lambda()
                .eq(ConProductTechniqueType::getName, conProductTechniqueType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conProductTechniqueType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conProductTechniqueType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConProductTechniqueType response = conProductTechniqueTypeMapper.selectConProductTechniqueTypeById(conProductTechniqueType.getSid());
        int row = conProductTechniqueTypeMapper.updateAllById(conProductTechniqueType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conProductTechniqueType.getSid(), BusinessType.CHANGE.getValue(), response, conProductTechniqueType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产工艺方法(编织方法)
     *
     * @param sids 需要删除的生产工艺方法(编织方法)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConProductTechniqueTypeByIds(List<Long> sids) {
        return conProductTechniqueTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conProductTechniqueType
    * @return
    */
    @Override
    public int changeStatus(ConProductTechniqueType conProductTechniqueType){
        int row=0;
        Long[] sids=conProductTechniqueType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conProductTechniqueType.setSid(id);
                row=conProductTechniqueTypeMapper.updateById( conProductTechniqueType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conProductTechniqueType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conProductTechniqueType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conProductTechniqueType
     * @return
     */
    @Override
    public int check(ConProductTechniqueType conProductTechniqueType){
        int row=0;
        Long[] sids=conProductTechniqueType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conProductTechniqueType.setSid(id);
                row=conProductTechniqueTypeMapper.updateById( conProductTechniqueType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conProductTechniqueType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConProductTechniqueType> getConProductTechniqueTypeList() {
        return conProductTechniqueTypeMapper.getConProductTechniqueTypeList();
    }

}
