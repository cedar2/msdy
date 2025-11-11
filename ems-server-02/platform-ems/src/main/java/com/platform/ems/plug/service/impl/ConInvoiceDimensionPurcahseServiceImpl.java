package com.platform.ems.plug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConInvoiceDimensionPurcahse;
import com.platform.ems.plug.mapper.ConInvoiceDimensionPurcahseMapper;
import com.platform.ems.plug.service.IConInvoiceDimensionPurcahseService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 发票维度_采购Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConInvoiceDimensionPurcahseServiceImpl extends ServiceImpl<ConInvoiceDimensionPurcahseMapper,ConInvoiceDimensionPurcahse>  implements IConInvoiceDimensionPurcahseService {
    @Autowired
    private ConInvoiceDimensionPurcahseMapper conInvoiceDimensionPurcahseMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "发票维度_采购";
    /**
     * 查询发票维度_采购
     *
     * @param sid 发票维度_采购ID
     * @return 发票维度_采购
     */
    @Override
    public ConInvoiceDimensionPurcahse selectConInvoiceDimensionPurcahseById(Long sid) {
        ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse = conInvoiceDimensionPurcahseMapper.selectConInvoiceDimensionPurcahseById(sid);
        MongodbUtil.find(conInvoiceDimensionPurcahse);
        return  conInvoiceDimensionPurcahse;
    }

    /**
     * 查询发票维度_采购列表
     *
     * @param conInvoiceDimensionPurcahse 发票维度_采购
     * @return 发票维度_采购
     */
    @Override
    public List<ConInvoiceDimensionPurcahse> selectConInvoiceDimensionPurcahseList(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        return conInvoiceDimensionPurcahseMapper.selectConInvoiceDimensionPurcahseList(conInvoiceDimensionPurcahse);
    }

    /**
     * 新增发票维度_采购
     * 需要注意编码重复校验
     * @param conInvoiceDimensionPurcahse 发票维度_采购
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInvoiceDimensionPurcahse(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        String name = conInvoiceDimensionPurcahse.getName();
        String code = conInvoiceDimensionPurcahse.getCode();
        List<ConInvoiceDimensionPurcahse> list = conInvoiceDimensionPurcahseMapper.selectList(new QueryWrapper<ConInvoiceDimensionPurcahse>().lambda()
                .or().eq(ConInvoiceDimensionPurcahse::getName, name)
                .or().eq(ConInvoiceDimensionPurcahse::getCode, code)
        );
        if(CollectionUtils.isNotEmpty(list)){
            throw new CustomException("配置档案已存在相同的名称或编码，不允许重复");
        }
        int row= conInvoiceDimensionPurcahseMapper.insert(conInvoiceDimensionPurcahse);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conInvoiceDimensionPurcahse.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改发票维度_采购
     *
     * @param conInvoiceDimensionPurcahse 发票维度_采购
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInvoiceDimensionPurcahse(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        ConInvoiceDimensionPurcahse response = conInvoiceDimensionPurcahseMapper.selectConInvoiceDimensionPurcahseById(conInvoiceDimensionPurcahse.getSid());
        int row=conInvoiceDimensionPurcahseMapper.updateById(conInvoiceDimensionPurcahse);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceDimensionPurcahse.getSid(), BusinessType.UPDATE.ordinal(), response,conInvoiceDimensionPurcahse,TITLE);
        }
        return row;
    }

    /**
     * 变更发票维度_采购
     *
     * @param conInvoiceDimensionPurcahse 发票维度_采购
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInvoiceDimensionPurcahse(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        String name = conInvoiceDimensionPurcahse.getName();
        ConInvoiceDimensionPurcahse item = conInvoiceDimensionPurcahseMapper.selectOne(new QueryWrapper<ConInvoiceDimensionPurcahse>().lambda()
                .eq(ConInvoiceDimensionPurcahse::getName, name)
        );
        if (item != null && !item.getSid().equals(conInvoiceDimensionPurcahse.getSid())) {
            throw new CustomException("配置档案已存在相同的名称，不允许重复");
        }
        ConInvoiceDimensionPurcahse response = conInvoiceDimensionPurcahseMapper.selectConInvoiceDimensionPurcahseById(conInvoiceDimensionPurcahse.getSid());
        int row = conInvoiceDimensionPurcahseMapper.updateAllById(conInvoiceDimensionPurcahse);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceDimensionPurcahse.getSid(), BusinessType.CHANGE.ordinal(), response, conInvoiceDimensionPurcahse, TITLE);
        }
        return row;
    }

    /**
     * 批量删除发票维度_采购
     *
     * @param sids 需要删除的发票维度_采购ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInvoiceDimensionPurcahseByIds(List<Long> sids) {
        return conInvoiceDimensionPurcahseMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conInvoiceDimensionPurcahse
    * @return
    */
    @Override
    public int changeStatus(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse){
        int row=0;
        Long[] sids=conInvoiceDimensionPurcahse.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conInvoiceDimensionPurcahse.setSid(id);
                row=conInvoiceDimensionPurcahseMapper.updateById( conInvoiceDimensionPurcahse);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conInvoiceDimensionPurcahse.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conInvoiceDimensionPurcahse.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conInvoiceDimensionPurcahse
     * @return
     */
    @Override
    public int check(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse){
        int row=0;
        Long[] sids=conInvoiceDimensionPurcahse.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conInvoiceDimensionPurcahse.setSid(id);
                row=conInvoiceDimensionPurcahseMapper.updateById( conInvoiceDimensionPurcahse);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conInvoiceDimensionPurcahse.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
