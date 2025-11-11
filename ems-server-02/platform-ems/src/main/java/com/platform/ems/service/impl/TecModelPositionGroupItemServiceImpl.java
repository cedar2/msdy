package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.mapper.TecModelPositionGroupItemMapper;
import com.platform.ems.domain.TecModelPositionGroupItem;
import com.platform.ems.service.ITecModelPositionGroupItemService;

/**
 * 版型部位组明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-02
 */
@Service
@SuppressWarnings("all")
public class TecModelPositionGroupItemServiceImpl extends ServiceImpl<TecModelPositionGroupItemMapper,TecModelPositionGroupItem>  implements ITecModelPositionGroupItemService {
    @Autowired
    private TecModelPositionGroupItemMapper tecModelPositionGroupItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "版型部位组明细";
    /**
     * 查询版型部位组明细
     *
     * @param groupItemSid 版型部位组明细ID
     * @return 版型部位组明细
     */
    @Override
    public TecModelPositionGroupItem selectTecModelPositionGroupItemById(Long groupItemSid) {
        TecModelPositionGroupItem tecModelPositionGroupItem = tecModelPositionGroupItemMapper.selectTecModelPositionGroupItemById(groupItemSid);
        MongodbUtil.find(tecModelPositionGroupItem);
        return  tecModelPositionGroupItem;
    }

    /**
     * 查询版型部位组明细列表
     *
     * @param tecModelPositionGroupItem 版型部位组明细
     * @return 版型部位组明细
     */
    @Override
    public List<TecModelPositionGroupItem> selectTecModelPositionGroupItemList(TecModelPositionGroupItem tecModelPositionGroupItem) {
        return tecModelPositionGroupItemMapper.selectTecModelPositionGroupItemList(tecModelPositionGroupItem);
    }

    /**
     * 新增版型部位组明细
     * 需要注意编码重复校验
     * @param tecModelPositionGroupItem 版型部位组明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecModelPositionGroupItem(TecModelPositionGroupItem tecModelPositionGroupItem) {
        int row= tecModelPositionGroupItemMapper.insert(tecModelPositionGroupItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(tecModelPositionGroupItem.getGroupItemSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改版型部位组明细
     *
     * @param tecModelPositionGroupItem 版型部位组明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecModelPositionGroupItem(TecModelPositionGroupItem tecModelPositionGroupItem) {
        TecModelPositionGroupItem response = tecModelPositionGroupItemMapper.selectTecModelPositionGroupItemById(tecModelPositionGroupItem.getGroupItemSid());
        int row=tecModelPositionGroupItemMapper.updateById(tecModelPositionGroupItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(tecModelPositionGroupItem.getGroupItemSid(), BusinessType.UPDATE.ordinal(), response,tecModelPositionGroupItem,TITLE);
        }
        return row;
    }

    /**
     * 变更版型部位组明细
     *
     * @param tecModelPositionGroupItem 版型部位组明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecModelPositionGroupItem(TecModelPositionGroupItem tecModelPositionGroupItem) {
        TecModelPositionGroupItem response = tecModelPositionGroupItemMapper.selectTecModelPositionGroupItemById(tecModelPositionGroupItem.getGroupItemSid());
                                                    int row=tecModelPositionGroupItemMapper.updateAllById(tecModelPositionGroupItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(tecModelPositionGroupItem.getGroupItemSid(), BusinessType.CHANGE.ordinal(), response,tecModelPositionGroupItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除版型部位组明细
     *
     * @param groupItemSids 需要删除的版型部位组明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecModelPositionGroupItemByIds(List<Long> groupItemSids) {
        return tecModelPositionGroupItemMapper.deleteBatchIds(groupItemSids);
    }

    /**
    * 启用/停用
    * @param tecModelPositionGroupItem
    * @return
    */
    @Override
    public int changeStatus(TecModelPositionGroupItem tecModelPositionGroupItem){
        int row=0;
        Long[] sids=tecModelPositionGroupItem.getGroupItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                tecModelPositionGroupItem.setGroupItemSid(id);
                row=tecModelPositionGroupItemMapper.updateById( tecModelPositionGroupItem);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark="无";
                MongodbUtil.insertUserLog(tecModelPositionGroupItem.getGroupItemSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param tecModelPositionGroupItem
     * @return
     */
    @Override
    public int check(TecModelPositionGroupItem tecModelPositionGroupItem){
        int row=0;
        Long[] sids=tecModelPositionGroupItem.getGroupItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                tecModelPositionGroupItem.setGroupItemSid(id);
                row=tecModelPositionGroupItemMapper.updateById( tecModelPositionGroupItem);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(tecModelPositionGroupItem.getGroupItemSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    @Override
    public List<TecModelPositionGroupItem> getReportForm(TecModelPositionGroupItem tecModelPositionGroupItem) {
        List<TecModelPositionGroupItem> responseList = tecModelPositionGroupItemMapper.selectTecModelPositionGroupItemList(tecModelPositionGroupItem);
        return responseList;
    }


}
