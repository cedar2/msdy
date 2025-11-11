package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ConCheckItem;
import com.platform.ems.domain.ConCheckStandard;
import com.platform.ems.domain.ConCheckStandardItem;
import com.platform.ems.domain.ConCheckStandardItemMethod;
import com.platform.ems.mapper.ConCheckStandardItemMapper;
import com.platform.ems.mapper.ConCheckStandardItemMethodMapper;
import com.platform.ems.mapper.ConCheckStandardMapper;
import com.platform.ems.service.IConCheckStandardService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测标准Service业务层处理
 *
 * @author qhq
 * @date 2021-11-01
 */
@Service
@SuppressWarnings("all")
public class ConCheckStandardServiceImpl extends ServiceImpl<ConCheckStandardMapper,ConCheckStandard>  implements IConCheckStandardService {
    @Autowired
    private ConCheckStandardMapper conCheckStandardMapper;
    @Autowired
    private ConCheckStandardItemMapper conCheckStandardItemMapper;
    @Autowired
    private ConCheckStandardItemMethodMapper conCheckStandardItemMethodMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "检测标准";
    /**
     * 查询检测标准
     *
     * @param sid 检测标准ID
     * @return 检测标准
     */
    @Override
    public ConCheckStandard selectConCheckStandardById(Long sid) {
        ConCheckStandard conCheckStandard = conCheckStandardMapper.selectConCheckStandardById(sid);
        MongodbUtil.find(conCheckStandard);
        return  conCheckStandard;
    }

    /**
     * 查询检测标准列表
     *
     * @param conCheckStandard 检测标准
     * @return 检测标准
     */
    @Override
    public List<ConCheckStandard> selectConCheckStandardList(ConCheckStandard conCheckStandard) {
        return conCheckStandardMapper.selectConCheckStandardList(conCheckStandard);
    }

    /**
     * 新增检测标准
     * 需要注意编码重复校验
     * @param conCheckStandard 检测标准
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConCheckStandard(ConCheckStandard conCheckStandard) {
        matchingCode(conCheckStandard);
        matchingName(conCheckStandard);
        int row= conCheckStandardMapper.insert(conCheckStandard);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conCheckStandard.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改检测标准
     *
     * @param conCheckStandard 检测标准
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConCheckStandard(ConCheckStandard conCheckStandard) {
        for(Long sid : conCheckStandard.getSidList()){
            ConCheckStandard response = conCheckStandardMapper.selectConCheckStandardById(sid);
            if(!response.getCode().equals(conCheckStandard.getCode())){
                matchingCode(conCheckStandard);
            }
            if(!response.getName().equals(conCheckStandard.getName())){
                matchingName(conCheckStandard);
            }
            int row=conCheckStandardMapper.updateById(conCheckStandard);
            if(row>0){
                //插入日志
                MongodbUtil.insertUserLog(conCheckStandard.getSid(), BusinessType.UPDATE.ordinal(), response,conCheckStandard,TITLE);
            }
        }
        return 1;
    }

    /**
     * 变更检测标准
     *
     * @param conCheckStandard 检测标准
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConCheckStandard(ConCheckStandard conCheckStandard) {
        ConCheckStandard response = conCheckStandardMapper.selectConCheckStandardById(conCheckStandard.getSid());
        if(!response.getCode().equals(conCheckStandard.getCode())){
            matchingCode(conCheckStandard);
        }
        if(!response.getName().equals(conCheckStandard.getName())){
            matchingName(conCheckStandard);
        }
        int row=conCheckStandardMapper.updateAllById(conCheckStandard);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCheckStandard.getSid(), BusinessType.CHANGE.ordinal(), response,conCheckStandard,TITLE);
        }
        return row;
    }

    /**
     * 批量删除检测标准
     *
     * @param sids 需要删除的检测标准ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConCheckStandardByIds(List<Long> sids) {
        return conCheckStandardMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conCheckStandard
    * @return
    */
    @Override
    public int changeStatus(ConCheckStandard conCheckStandard){
        int row=0;
        Long[] sids=conCheckStandard.getSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckStandardMapper.update(null, new UpdateWrapper<ConCheckStandard>().lambda().set(ConCheckStandard::getStatus ,conCheckStandard.getStatus() )
                    .in(ConCheckStandard::getSid,sids));
            for(Long id:sids){
                conCheckStandard.setSid(id);
                row=conCheckStandardMapper.updateById( conCheckStandard);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conCheckStandard.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conCheckStandard.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conCheckStandard
     * @return
     */
    @Override
    public int check(ConCheckStandard conCheckStandard){
        int row=0;
        Long[] sids=conCheckStandard.getSidList();
        if(sids!=null&&sids.length>0){
            row=conCheckStandardMapper.update(null,new UpdateWrapper<ConCheckStandard>().lambda().set(ConCheckStandard::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConCheckStandard::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 分配项目
     * @param conCheckStandard
     * @return
     */
    @Override
    public int addStandardItem(ConCheckStandard conCheckStandard){
        List<ConCheckStandardItem> standardItemList = conCheckStandard.getStandardItemList();
        QueryWrapper<ConCheckStandardItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("check_standard_sid",conCheckStandard.getSid());
        conCheckStandardItemMapper.delete(itemQueryWrapper);
        for (ConCheckStandardItem item : standardItemList){
            item.setCheckStandardSid(conCheckStandard.getSid());
            conCheckStandardItemMapper.insert(item);
        }
        return 1;
    }

    /**
     * 分配方法
     * @param conCheckStandardItem
     * @return
     */
    @Override
    public int addStandardItemMethod(ConCheckStandardItem conCheckStandardItem){
        List<ConCheckStandardItemMethod> standardItemMethodList = conCheckStandardItem.getStandardItemMethodList();
        QueryWrapper<ConCheckStandardItemMethod> methodQueryWrapper = new QueryWrapper<>();
        methodQueryWrapper.eq("check_standard_item_sid",conCheckStandardItem.getCheckStandardItemSid());
        conCheckStandardItemMethodMapper.delete(methodQueryWrapper);
        for(ConCheckStandardItemMethod method : standardItemMethodList){
            method.setCheckStandardItemSid(conCheckStandardItem.getCheckStandardItemSid());
            conCheckStandardItemMethodMapper.insert(method);
        }
        return 1;
    }

    /**
     * 验证编码重复
     * @param conCheckStandard
     */
    public void matchingCode(ConCheckStandard conCheckStandard){
        QueryWrapper<ConCheckStandard> wrapper = new QueryWrapper<>();
        wrapper.eq("code",conCheckStandard.getCode());
        int codeMatching = conCheckStandardMapper.selectList(wrapper).size();
        if(codeMatching>0){
            throw new CustomException("已存在相同编码，请重新输入！");
        }
    }

    /**
     * 验证名称重复
     */
    public void matchingName(ConCheckStandard conCheckStandard){
        QueryWrapper<ConCheckStandard> wrapper = new QueryWrapper<>();
        wrapper.eq("name",conCheckStandard.getName());
        int codeMatching = conCheckStandardMapper.selectList(wrapper).size();
        if(codeMatching>0){
            throw new CustomException("已存在相同名称，请重新输入！");
        }
    }
}
