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
import com.platform.ems.plug.domain.ConOutsourceProcessItemCategory;
import com.platform.ems.plug.mapper.ConOutsourceProcessItemCategoryMapper;
import com.platform.ems.plug.service.IConOutsourceProcessItemCategoryService;
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
 * 行类别_外发加工发料单/收货单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-19
 */
@Service
@SuppressWarnings("all")
public class ConOutsourceProcessItemCategoryServiceImpl extends ServiceImpl<ConOutsourceProcessItemCategoryMapper,ConOutsourceProcessItemCategory>  implements IConOutsourceProcessItemCategoryService {
    @Autowired
    private ConOutsourceProcessItemCategoryMapper conOutsourceProcessItemCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "行类别_外发加工发料单/收货单";
    /**
     * 查询行类别_外发加工发料单/收货单
     *
     * @param sid 行类别_外发加工发料单/收货单ID
     * @return 行类别_外发加工发料单/收货单
     */
    @Override
    public ConOutsourceProcessItemCategory selectConOutsourceProcessItemCategoryById(Long sid) {
        ConOutsourceProcessItemCategory conOutsourceProcessItemCategory = conOutsourceProcessItemCategoryMapper.selectConOutsourceProcessItemCategoryById(sid);
        MongodbUtil.find(conOutsourceProcessItemCategory);
        return  conOutsourceProcessItemCategory;
    }

    /**
     * 查询行类别_外发加工发料单/收货单列表
     *
     * @param conOutsourceProcessItemCategory 行类别_外发加工发料单/收货单
     * @return 行类别_外发加工发料单/收货单
     */
    @Override
    public List<ConOutsourceProcessItemCategory> selectConOutsourceProcessItemCategoryList(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        return conOutsourceProcessItemCategoryMapper.selectConOutsourceProcessItemCategoryList(conOutsourceProcessItemCategory);
    }

    /**
     * 新增行类别_外发加工发料单/收货单
     * 需要注意编码重复校验
     * @param conOutsourceProcessItemCategory 行类别_外发加工发料单/收货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConOutsourceProcessItemCategory(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        List<ConOutsourceProcessItemCategory> codeList = conOutsourceProcessItemCategoryMapper.selectList(new QueryWrapper<ConOutsourceProcessItemCategory>().lambda()
                .eq(ConOutsourceProcessItemCategory::getCode, conOutsourceProcessItemCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConOutsourceProcessItemCategory> nameList = conOutsourceProcessItemCategoryMapper.selectList(new QueryWrapper<ConOutsourceProcessItemCategory>().lambda()
                .eq(ConOutsourceProcessItemCategory::getName, conOutsourceProcessItemCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conOutsourceProcessItemCategoryMapper.insert(conOutsourceProcessItemCategory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conOutsourceProcessItemCategory.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改行类别_外发加工发料单/收货单
     *
     * @param conOutsourceProcessItemCategory 行类别_外发加工发料单/收货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConOutsourceProcessItemCategory(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        ConOutsourceProcessItemCategory response = conOutsourceProcessItemCategoryMapper.selectConOutsourceProcessItemCategoryById(conOutsourceProcessItemCategory.getSid());
        int row=conOutsourceProcessItemCategoryMapper.updateById(conOutsourceProcessItemCategory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conOutsourceProcessItemCategory.getSid(), BusinessType.UPDATE.getValue(), response,conOutsourceProcessItemCategory,TITLE);
        }
        return row;
    }

    /**
     * 变更行类别_外发加工发料单/收货单
     *
     * @param conOutsourceProcessItemCategory 行类别_外发加工发料单/收货单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConOutsourceProcessItemCategory(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory) {
        List<ConOutsourceProcessItemCategory> nameList = conOutsourceProcessItemCategoryMapper.selectList(new QueryWrapper<ConOutsourceProcessItemCategory>().lambda()
                .eq(ConOutsourceProcessItemCategory::getName, conOutsourceProcessItemCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conOutsourceProcessItemCategory.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conOutsourceProcessItemCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConOutsourceProcessItemCategory response = conOutsourceProcessItemCategoryMapper.selectConOutsourceProcessItemCategoryById(conOutsourceProcessItemCategory.getSid());
        int row = conOutsourceProcessItemCategoryMapper.updateAllById(conOutsourceProcessItemCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conOutsourceProcessItemCategory.getSid(), BusinessType.CHANGE.getValue(), response, conOutsourceProcessItemCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除行类别_外发加工发料单/收货单
     *
     * @param sids 需要删除的行类别_外发加工发料单/收货单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConOutsourceProcessItemCategoryByIds(List<Long> sids) {
        return conOutsourceProcessItemCategoryMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conOutsourceProcessItemCategory
    * @return
    */
    @Override
    public int changeStatus(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory){
        int row=0;
        Long[] sids=conOutsourceProcessItemCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conOutsourceProcessItemCategory.setSid(id);
                row=conOutsourceProcessItemCategoryMapper.updateById( conOutsourceProcessItemCategory);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conOutsourceProcessItemCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conOutsourceProcessItemCategory.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conOutsourceProcessItemCategory
     * @return
     */
    @Override
    public int check(ConOutsourceProcessItemCategory conOutsourceProcessItemCategory){
        int row=0;
        Long[] sids=conOutsourceProcessItemCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conOutsourceProcessItemCategory.setSid(id);
                row=conOutsourceProcessItemCategoryMapper.updateById( conOutsourceProcessItemCategory);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conOutsourceProcessItemCategory.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConOutsourceProcessItemCategory> getConOutsourceProcessItemCategoryList() {
        return conOutsourceProcessItemCategoryMapper.getConOutsourceProcessItemCategoryList();
    }


}
