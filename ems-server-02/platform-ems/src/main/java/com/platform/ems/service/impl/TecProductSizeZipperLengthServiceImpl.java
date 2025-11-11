package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.TecProductSizeZipperLength;
import com.platform.ems.mapper.TecProductSizeZipperLengthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.service.ITecProductSizeZipperLengthService;

/**
 * 商品尺码拉链长度明细Service业务层处理
 *
 * @author c
 * @date 2021-08-03
 */
@Service
@SuppressWarnings("all")
public class TecProductSizeZipperLengthServiceImpl extends ServiceImpl<TecProductSizeZipperLengthMapper, TecProductSizeZipperLength>  implements ITecProductSizeZipperLengthService {
    @Autowired
    private TecProductSizeZipperLengthMapper tecProductSizeZipperLengthMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "商品尺码拉链长度明细";
    /**
     * 查询商品尺码拉链长度明细
     *
     * @param productZipperSid 商品尺码拉链长度明细ID
     * @return 商品尺码拉链长度明细
     */
    @Override
    public TecProductSizeZipperLength selectTecProductSizeZipperLengthById(Long productZipperSid) {
        TecProductSizeZipperLength tecProductSizeZipperLength = tecProductSizeZipperLengthMapper.selectTecProductSizeZipperLengthById(productZipperSid);
        MongodbUtil.find(tecProductSizeZipperLength);
        return  tecProductSizeZipperLength;
    }

    /**
     * 查询商品尺码拉链长度明细列表
     *
     * @param tecProductSizeZipperLength 商品尺码拉链长度明细
     * @return 商品尺码拉链长度明细
     */
    @Override
    public List<TecProductSizeZipperLength> selectTecProductSizeZipperLengthList(TecProductSizeZipperLength tecProductSizeZipperLength) {
        return tecProductSizeZipperLengthMapper.selectTecProductSizeZipperLengthList(tecProductSizeZipperLength);
    }

    /**
     * 新增商品尺码拉链长度明细
     * 需要注意编码重复校验
     * @param tecProductSizeZipperLength 商品尺码拉链长度明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecProductSizeZipperLength(TecProductSizeZipperLength tecProductSizeZipperLength) {
        int row= tecProductSizeZipperLengthMapper.insert(tecProductSizeZipperLength);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(tecProductSizeZipperLength.getProductZipperSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改商品尺码拉链长度明细
     *
     * @param tecProductSizeZipperLength 商品尺码拉链长度明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecProductSizeZipperLength(TecProductSizeZipperLength tecProductSizeZipperLength) {
        TecProductSizeZipperLength response = tecProductSizeZipperLengthMapper.selectTecProductSizeZipperLengthById(tecProductSizeZipperLength.getProductZipperSid());
        int row=tecProductSizeZipperLengthMapper.updateById(tecProductSizeZipperLength);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(tecProductSizeZipperLength.getProductZipperSid(), BusinessType.UPDATE.ordinal(), response,tecProductSizeZipperLength,TITLE);
        }
        return row;
    }

    /**
     * 变更商品尺码拉链长度明细
     *
     * @param tecProductSizeZipperLength 商品尺码拉链长度明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecProductSizeZipperLength(TecProductSizeZipperLength tecProductSizeZipperLength) {
        TecProductSizeZipperLength response = tecProductSizeZipperLengthMapper.selectTecProductSizeZipperLengthById(tecProductSizeZipperLength.getProductZipperSid());
                                                                                int row=tecProductSizeZipperLengthMapper.updateAllById(tecProductSizeZipperLength);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(tecProductSizeZipperLength.getProductZipperSid(), BusinessType.CHANGE.ordinal(), response,tecProductSizeZipperLength,TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品尺码拉链长度明细
     *
     * @param productZipperSids 需要删除的商品尺码拉链长度明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecProductSizeZipperLengthByIds(List<Long> productZipperSids) {
        return tecProductSizeZipperLengthMapper.deleteBatchIds(productZipperSids);
    }


    /**
     *更改确认状态
     * @param tecProductSizeZipperLength
     * @return
     */
    @Override
    public int check(TecProductSizeZipperLength tecProductSizeZipperLength){
        int row=0;
        Long[] sids=tecProductSizeZipperLength.getProductZipperSidList();
        if(sids!=null&&sids.length>0){
            row=tecProductSizeZipperLengthMapper.update(null,new UpdateWrapper<TecProductSizeZipperLength>().lambda().set(TecProductSizeZipperLength::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(TecProductSizeZipperLength::getProductZipperSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
