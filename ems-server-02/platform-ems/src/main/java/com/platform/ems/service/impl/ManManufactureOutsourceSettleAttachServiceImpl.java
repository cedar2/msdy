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
import com.platform.ems.mapper.ManManufactureOutsourceSettleAttachMapper;
import com.platform.ems.domain.ManManufactureOutsourceSettleAttach;
import com.platform.ems.service.IManManufactureOutsourceSettleAttachService;

/**
 * 外发加工费结算单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-10
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOutsourceSettleAttachServiceImpl extends ServiceImpl<ManManufactureOutsourceSettleAttachMapper,ManManufactureOutsourceSettleAttach>  implements IManManufactureOutsourceSettleAttachService {
    @Autowired
    private ManManufactureOutsourceSettleAttachMapper manManufactureOutsourceSettleAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工费结算单-附件";
    /**
     * 查询外发加工费结算单-附件
     *
     * @param manufactureOutsourceSettleAttachSid 外发加工费结算单-附件ID
     * @return 外发加工费结算单-附件
     */
    @Override
    public ManManufactureOutsourceSettleAttach selectManManufactureOutsourceSettleAttachById(Long manufactureOutsourceSettleAttachSid) {
        ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach = manManufactureOutsourceSettleAttachMapper.selectManManufactureOutsourceSettleAttachById(manufactureOutsourceSettleAttachSid);
        MongodbUtil.find(manManufactureOutsourceSettleAttach);
        return  manManufactureOutsourceSettleAttach;
    }

    /**
     * 查询外发加工费结算单-附件列表
     *
     * @param manManufactureOutsourceSettleAttach 外发加工费结算单-附件
     * @return 外发加工费结算单-附件
     */
    @Override
    public List<ManManufactureOutsourceSettleAttach> selectManManufactureOutsourceSettleAttachList(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach) {
        return manManufactureOutsourceSettleAttachMapper.selectManManufactureOutsourceSettleAttachList(manManufactureOutsourceSettleAttach);
    }

    /**
     * 新增外发加工费结算单-附件
     * 需要注意编码重复校验
     * @param manManufactureOutsourceSettleAttach 外发加工费结算单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureOutsourceSettleAttach(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach) {
        int row= manManufactureOutsourceSettleAttachMapper.insert(manManufactureOutsourceSettleAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(manManufactureOutsourceSettleAttach.getManufactureOutsourceSettleAttachSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改外发加工费结算单-附件
     *
     * @param manManufactureOutsourceSettleAttach 外发加工费结算单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureOutsourceSettleAttach(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach) {
        ManManufactureOutsourceSettleAttach response = manManufactureOutsourceSettleAttachMapper.selectManManufactureOutsourceSettleAttachById(manManufactureOutsourceSettleAttach.getManufactureOutsourceSettleAttachSid());
        int row=manManufactureOutsourceSettleAttachMapper.updateById(manManufactureOutsourceSettleAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(manManufactureOutsourceSettleAttach.getManufactureOutsourceSettleAttachSid(), BusinessType.UPDATE.getValue(), response,manManufactureOutsourceSettleAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更外发加工费结算单-附件
     *
     * @param manManufactureOutsourceSettleAttach 外发加工费结算单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureOutsourceSettleAttach(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach) {
        ManManufactureOutsourceSettleAttach response = manManufactureOutsourceSettleAttachMapper.selectManManufactureOutsourceSettleAttachById(manManufactureOutsourceSettleAttach.getManufactureOutsourceSettleAttachSid());
                                                        int row=manManufactureOutsourceSettleAttachMapper.updateAllById(manManufactureOutsourceSettleAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(manManufactureOutsourceSettleAttach.getManufactureOutsourceSettleAttachSid(), BusinessType.CHANGE.getValue(), response,manManufactureOutsourceSettleAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外发加工费结算单-附件
     *
     * @param manufactureOutsourceSettleAttachSids 需要删除的外发加工费结算单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureOutsourceSettleAttachByIds(List<Long> manufactureOutsourceSettleAttachSids) {
        return manManufactureOutsourceSettleAttachMapper.deleteBatchIds(manufactureOutsourceSettleAttachSids);
    }

    /**
     *更改确认状态
     * @param manManufactureOutsourceSettleAttach
     * @return
     */
    @Override
    public int check(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach){
        int row=0;
        Long[] sids=manManufactureOutsourceSettleAttach.getManufactureOutsourceSettleAttachSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                manManufactureOutsourceSettleAttach.setManufactureOutsourceSettleAttachSid(id);
                row=manManufactureOutsourceSettleAttachMapper.updateById( manManufactureOutsourceSettleAttach);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(manManufactureOutsourceSettleAttach.getManufactureOutsourceSettleAttachSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
