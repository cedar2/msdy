package com.platform.ems.service.impl;

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
import com.platform.ems.mapper.TecLinePositionGroupItemMapper;
import com.platform.ems.domain.TecLinePositionGroupItem;
import com.platform.ems.service.ITecLinePositionGroupItemService;

/**
 * 线部位组明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-08-19
 */
@Service
@SuppressWarnings("all")
public class TecLinePositionGroupItemServiceImpl extends ServiceImpl<TecLinePositionGroupItemMapper,TecLinePositionGroupItem>  implements ITecLinePositionGroupItemService {
    @Autowired
    private TecLinePositionGroupItemMapper tecLinePositionGroupItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "线部位组明细";
    /**
     * 查询线部位组明细
     *
     * @param groupItemSid 线部位组明细ID
     * @return 线部位组明细
     */
    @Override
    public TecLinePositionGroupItem selectTecLinePositionGroupItemById(Long groupItemSid) {
        TecLinePositionGroupItem tecLinePositionGroupItem = tecLinePositionGroupItemMapper.selectTecLinePositionGroupItemById(groupItemSid);
        MongodbUtil.find(tecLinePositionGroupItem);
        return  tecLinePositionGroupItem;
    }

    /**
     * 查询线部位组明细列表
     *
     * @param tecLinePositionGroupItem 线部位组明细
     * @return 线部位组明细
     */
    @Override
    public List<TecLinePositionGroupItem> selectTecLinePositionGroupItemList(TecLinePositionGroupItem tecLinePositionGroupItem) {
        return tecLinePositionGroupItemMapper.selectTecLinePositionGroupItemList(tecLinePositionGroupItem);
    }

    /**
     * 新增线部位组明细
     * 需要注意编码重复校验
     * @param tecLinePositionGroupItem 线部位组明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecLinePositionGroupItem(TecLinePositionGroupItem tecLinePositionGroupItem) {
        int row= tecLinePositionGroupItemMapper.insert(tecLinePositionGroupItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(tecLinePositionGroupItem.getGroupItemSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改线部位组明细
     *
     * @param tecLinePositionGroupItem 线部位组明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecLinePositionGroupItem(TecLinePositionGroupItem tecLinePositionGroupItem) {
        TecLinePositionGroupItem response = tecLinePositionGroupItemMapper.selectTecLinePositionGroupItemById(tecLinePositionGroupItem.getGroupItemSid());
        int row=tecLinePositionGroupItemMapper.updateById(tecLinePositionGroupItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(tecLinePositionGroupItem.getGroupItemSid(), BusinessType.UPDATE.ordinal(), response,tecLinePositionGroupItem,TITLE);
        }
        return row;
    }

    /**
     * 变更线部位组明细
     *
     * @param tecLinePositionGroupItem 线部位组明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecLinePositionGroupItem(TecLinePositionGroupItem tecLinePositionGroupItem) {
        TecLinePositionGroupItem response = tecLinePositionGroupItemMapper.selectTecLinePositionGroupItemById(tecLinePositionGroupItem.getGroupItemSid());
                                                    int row=tecLinePositionGroupItemMapper.updateAllById(tecLinePositionGroupItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(tecLinePositionGroupItem.getGroupItemSid(), BusinessType.CHANGE.ordinal(), response,tecLinePositionGroupItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除线部位组明细
     *
     * @param groupItemSids 需要删除的线部位组明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecLinePositionGroupItemByIds(List<Long> groupItemSids) {
        return tecLinePositionGroupItemMapper.deleteBatchIds(groupItemSids);
    }

}
