package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.StringUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.service.IConCheckStandardItemMethodService;

/**
 * 检测标准/项目/方法关联Service业务层处理
 *
 * @author qhq
 * @date 2021-11-01
 */
@Service
@SuppressWarnings("all")
public class ConCheckStandardItemMethodServiceImpl extends ServiceImpl<ConCheckStandardItemMethodMapper,ConCheckStandardItemMethod>  implements IConCheckStandardItemMethodService {
    @Autowired
    private ConCheckStandardMapper conCheckStandardMapper;
    @Autowired
    private ConCheckItemMapper conCheckItemMapper;
    @Autowired
    private ConCheckMethodMapper conCheckMethodMapper;
    @Autowired
    private ConCheckStandardItemMethodMapper conCheckStandardItemMethodMapper;
    @Autowired
    private ConCheckStandardItemMapper conCheckStandardItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "检测标准/项目/方法关联";
    /**
     * 查询检测标准/项目/方法关联
     *
     * @param checkStandardItemMethodSid 检测标准/项目/方法关联ID
     * @return 检测标准/项目/方法关联
     */
    @Override
    public ConCheckStandardItemMethod selectConCheckStandardItemMethodById(Long checkStandardItemMethodSid) {
        ConCheckStandardItemMethod conCheckStandardItemMethod = conCheckStandardItemMethodMapper.selectConCheckStandardItemMethodById(checkStandardItemMethodSid);
        MongodbUtil.find(conCheckStandardItemMethod);
        return  conCheckStandardItemMethod;
    }

    /**
     * 查询检测标准/项目/方法关联列表
     *
     * @param conCheckStandardItemMethod 检测标准/项目/方法关联
     * @return 检测标准/项目/方法关联
     */
    @Override
    public List<ConCheckStandardItemMethod> selectConCheckStandardItemMethodList(ConCheckStandardItemMethod conCheckStandardItemMethod) {
        List<ConCheckStandard> standardList = new ArrayList<>();
        List<ConCheckItem> itemList = new ArrayList<>();
        List<ConCheckMethod> methodList = new ArrayList<>();
        if(StringUtils.isNotEmpty(conCheckStandardItemMethod.getCheckStandardName())){
            QueryWrapper<ConCheckStandard> standardQueryWrapper = new QueryWrapper<>();
            standardQueryWrapper.eq("name",conCheckStandardItemMethod.getCheckStandardName());
            standardList = conCheckStandardMapper.selectList(standardQueryWrapper);
        }
        if(StringUtils.isNotEmpty(conCheckStandardItemMethod.getCheckItemName())){
            QueryWrapper<ConCheckItem> itemQueryWrapper = new QueryWrapper<>();
            itemQueryWrapper.eq("name",conCheckStandardItemMethod.getCheckItemName());
            itemList = conCheckItemMapper.selectList(itemQueryWrapper);
        }
        if(StringUtils.isNotEmpty(conCheckStandardItemMethod.getCheckMethodName())){
            QueryWrapper<ConCheckMethod> methodQueryWrapper = new QueryWrapper<>();
            methodQueryWrapper.eq("name",conCheckStandardItemMethod.getCheckMethodName());
            methodList = conCheckMethodMapper.selectList(methodQueryWrapper);
        }
        List<String> standardCodeList = new ArrayList<>();
        List<String> itemCodeList = new ArrayList<>();
        List<String> methodCodeList = new ArrayList<>();
        if(standardList.size()>0){
            standardList.forEach(standard->{
                standardCodeList.add(standard.getCode());
            });
        }
        if(itemList.size()>0){
            itemList.forEach(item->{
                itemCodeList.add(item.getCode());
            });
        }
        if(methodList.size()>0){
            methodList.forEach(method->{
                methodCodeList.add(method.getCode());
            });
        }
        QueryWrapper<ConCheckStandardItem> standardItemQueryWrapper = new QueryWrapper<>();
        if(standardCodeList.size()>0){
            standardItemQueryWrapper.in("check_standard_code",standardCodeList);
        }
        if(itemCodeList.size()>0){
            standardItemQueryWrapper.in("check_item_code",itemCodeList);
        }
        List<ConCheckStandardItem> standardItemList = conCheckStandardItemMapper.selectList(standardItemQueryWrapper);
        List<Long> standardItemSidList = new ArrayList<>();
        if(standardItemList.size()>0){
            standardItemList.forEach(si->{
                standardItemSidList.add(si.getCheckStandardItemSid());
            });
        }
        QueryWrapper<ConCheckStandardItemMethod> checkStandardItemMethodQueryWrapper = new QueryWrapper<>();
        if(standardItemSidList.size()>0){
            checkStandardItemMethodQueryWrapper.in("check_standard_item_sid",standardItemSidList);
        }
        if(methodCodeList.size()>0){
            checkStandardItemMethodQueryWrapper.in("check_method_code",methodCodeList);
        }
        if(Objects.nonNull(conCheckStandardItemMethod.getBeginTime())){
            checkStandardItemMethodQueryWrapper.ge("create_date",conCheckStandardItemMethod.getCreateDate());
        }
        if(Objects.nonNull(conCheckStandardItemMethod.getEndTime())){
            checkStandardItemMethodQueryWrapper.le("create_date",conCheckStandardItemMethod.getEndTime());
        }
        List<ConCheckStandardItemMethod> conCheckStandardItemMethodList = conCheckStandardItemMethodMapper.selectList(checkStandardItemMethodQueryWrapper);
        return conCheckStandardItemMethodList;
    }

    /**
     * 新增检测标准/项目/方法关联
     * 需要注意编码重复校验
     * @param conCheckStandardItemMethod 检测标准/项目/方法关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConCheckStandardItemMethod(ConCheckStandardItemMethod conCheckStandardItemMethod) {
        matching(conCheckStandardItemMethod);
        int row= conCheckStandardItemMethodMapper.insert(conCheckStandardItemMethod);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conCheckStandardItemMethod.getCheckStandardItemMethodSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改检测标准/项目/方法关联
     *
     * @param conCheckStandardItemMethod 检测标准/项目/方法关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConCheckStandardItemMethod(ConCheckStandardItemMethod conCheckStandardItemMethod) {
        ConCheckStandardItemMethod response = conCheckStandardItemMethodMapper.selectConCheckStandardItemMethodById(conCheckStandardItemMethod.getCheckStandardItemMethodSid());
        int row=conCheckStandardItemMethodMapper.updateById(conCheckStandardItemMethod);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCheckStandardItemMethod.getCheckStandardItemMethodSid(), BusinessType.UPDATE.ordinal(), response,conCheckStandardItemMethod,TITLE);
        }
        return row;
    }

    /**
     * 变更检测标准/项目/方法关联
     *
     * @param conCheckStandardItemMethod 检测标准/项目/方法关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConCheckStandardItemMethod(ConCheckStandardItemMethod conCheckStandardItemMethod) {
        ConCheckStandardItemMethod response = conCheckStandardItemMethodMapper.selectConCheckStandardItemMethodById(conCheckStandardItemMethod.getCheckStandardItemMethodSid());
                                                                                                    int row=conCheckStandardItemMethodMapper.updateAllById(conCheckStandardItemMethod);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCheckStandardItemMethod.getCheckStandardItemMethodSid(), BusinessType.CHANGE.ordinal(), response,conCheckStandardItemMethod,TITLE);
        }
        return row;
    }

    /**
     * 批量删除检测标准/项目/方法关联
     *
     * @param checkStandardItemMethodSids 需要删除的检测标准/项目/方法关联ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConCheckStandardItemMethodByIds(List<Long> checkStandardItemMethodSids) {
        return conCheckStandardItemMethodMapper.deleteBatchIds(checkStandardItemMethodSids);
    }

    public void matching (ConCheckStandardItemMethod conCheckStandardItemMethod){
        QueryWrapper<ConCheckStandardItemMethod> itemMethodQueryWrapper = new QueryWrapper<>();
        itemMethodQueryWrapper.eq("check_method_sid",conCheckStandardItemMethod.getCheckMethodSid());
        itemMethodQueryWrapper.eq("check_method_code",conCheckStandardItemMethod.getCheckMethodCode());
        itemMethodQueryWrapper.eq("check_standard_item_sid",conCheckStandardItemMethod.getCheckStandardItemSid());
        int i = conCheckStandardItemMethodMapper.selectList(itemMethodQueryWrapper).size();
        if(i>0){
            throw new CustomException("已存在相同组合，请重新选择！");
        }
    }

    /**
    * 启用/停用
    * @param conCheckStandardItemMethod
    * @return
    @Override
    public int changeStatus(ConCheckStandardItemMethod conCheckStandardItemMethod){
        int row=0;
        Long[] sids=conCheckStandardItemMethod.getCheckStandardItemMethodSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckStandardItemMethodMapper.update(null, new UpdateWrapper<ConCheckStandardItemMethod>().lambda().set(ConCheckStandardItemMethod::getStatus ,conCheckStandardItemMethod.getStatus() )
                    .in(ConCheckStandardItemMethod::getCheckStandardItemMethodSid,sids));
            for(Long id:sids){
                conCheckStandardItemMethod.setCheckStandardItemMethodSid(id);
                row=conCheckStandardItemMethodMapper.updateById( conCheckStandardItemMethod);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conCheckStandardItemMethod.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conCheckStandardItemMethod.getCheckStandardItemMethodSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }*/


    /**
     *更改确认状态
     * @param conCheckStandardItemMethod
     * @return

    @Override
    public int check(ConCheckStandardItemMethod conCheckStandardItemMethod){
        int row=0;
        Long[] sids=conCheckStandardItemMethod.getCheckStandardItemMethodSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckStandardItemMethodMapper.update(null,new UpdateWrapper<ConCheckStandardItemMethod>().lambda().set(ConCheckStandardItemMethod::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConCheckStandardItemMethod::getCheckStandardItemMethodSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }*/


}
