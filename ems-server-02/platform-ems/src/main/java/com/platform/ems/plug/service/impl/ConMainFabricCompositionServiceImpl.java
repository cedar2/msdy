package com.platform.ems.plug.service.impl;

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
import com.platform.ems.plug.mapper.ConMainFabricCompositionMapper;
import com.platform.ems.plug.domain.ConMainFabricComposition;
import com.platform.ems.plug.service.IConMainFabricCompositionService;

/**
 * 主面料成分Service业务层处理
 *
 * @author chenkw
 * @date 2022-06-01
 */
@Service
@SuppressWarnings("all")
public class ConMainFabricCompositionServiceImpl extends ServiceImpl<ConMainFabricCompositionMapper, ConMainFabricComposition> implements IConMainFabricCompositionService {
    @Autowired
    private ConMainFabricCompositionMapper conMainFabricCompositionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "主面料成分";

    /**
     * 查询主面料成分
     *
     * @param sid 主面料成分ID
     * @return 主面料成分
     */
    @Override
    public ConMainFabricComposition selectConMainFabricCompositionById(Long sid) {
        ConMainFabricComposition conMainFabricComposition = conMainFabricCompositionMapper.selectConMainFabricCompositionById(sid);
        MongodbUtil.find(conMainFabricComposition);
        return conMainFabricComposition;
    }

    /**
     * 查询主面料成分列表
     *
     * @param conMainFabricComposition 主面料成分
     * @return 主面料成分
     */
    @Override
    public List<ConMainFabricComposition> selectConMainFabricCompositionList(ConMainFabricComposition conMainFabricComposition) {
        return conMainFabricCompositionMapper.selectConMainFabricCompositionList(conMainFabricComposition);
    }

    /**
     * 新增主面料成分
     * 需要注意编码重复校验
     *
     * @param conMainFabricComposition 主面料成分
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConMainFabricComposition(ConMainFabricComposition conMainFabricComposition) {
        int row = conMainFabricCompositionMapper.insert(conMainFabricComposition);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conMainFabricComposition.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改主面料成分
     *
     * @param conMainFabricComposition 主面料成分
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConMainFabricComposition(ConMainFabricComposition conMainFabricComposition) {
        ConMainFabricComposition response = conMainFabricCompositionMapper.selectConMainFabricCompositionById(conMainFabricComposition.getSid());
        int row = conMainFabricCompositionMapper.updateById(conMainFabricComposition);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conMainFabricComposition.getSid(), BusinessType.UPDATE.getValue(), response, conMainFabricComposition, TITLE);
        }
        return row;
    }

    /**
     * 变更主面料成分
     *
     * @param conMainFabricComposition 主面料成分
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConMainFabricComposition(ConMainFabricComposition conMainFabricComposition) {
        ConMainFabricComposition response = conMainFabricCompositionMapper.selectConMainFabricCompositionById(conMainFabricComposition.getSid());
        int row = conMainFabricCompositionMapper.updateAllById(conMainFabricComposition);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conMainFabricComposition.getSid(), BusinessType.CHANGE.getValue(), response, conMainFabricComposition, TITLE);
        }
        return row;
    }

    /**
     * 批量删除主面料成分
     *
     * @param sids 需要删除的主面料成分ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConMainFabricCompositionByIds(List<Long> sids) {
        return conMainFabricCompositionMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conMainFabricComposition
     * @return
     */
    @Override
    public int changeStatus(ConMainFabricComposition conMainFabricComposition) {
        int row = 0;
        Long[] sids = conMainFabricComposition.getSidList();
        if (sids != null && sids.length > 0) {
            row = conMainFabricCompositionMapper.update(null, new UpdateWrapper<ConMainFabricComposition>().lambda().set(ConMainFabricComposition::getStatus, conMainFabricComposition.getStatus())
                    .in(ConMainFabricComposition::getSid, sids));
            for (Long id : sids) {
                conMainFabricComposition.setSid(id);
                row = conMainFabricCompositionMapper.updateById(conMainFabricComposition);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conMainFabricComposition.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conMainFabricComposition.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conMainFabricComposition
     * @return
     */
    @Override
    public int check(ConMainFabricComposition conMainFabricComposition) {
        int row = 0;
        Long[] sids = conMainFabricComposition.getSidList();
        if (sids != null && sids.length > 0) {
            row = conMainFabricCompositionMapper.update(null, new UpdateWrapper<ConMainFabricComposition>().lambda().set(ConMainFabricComposition::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConMainFabricComposition::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
