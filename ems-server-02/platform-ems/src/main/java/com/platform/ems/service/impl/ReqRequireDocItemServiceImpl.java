package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ReqRequireDocItemMapper;
import com.platform.ems.domain.ReqRequireDocItem;
import com.platform.ems.service.IReqRequireDocItemService;

/**
 * 需求单明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-02
 */
@Service
@SuppressWarnings("all")
public class ReqRequireDocItemServiceImpl extends ServiceImpl<ReqRequireDocItemMapper,ReqRequireDocItem>  implements IReqRequireDocItemService {
    @Autowired
    private ReqRequireDocItemMapper reqRequireDocItemMapper;

    /**
     * 查询需求单明细
     * 
     * @param requireDocItemSid 需求单明细ID
     * @return 需求单明细
     */
    @Override
    public ReqRequireDocItem selectReqRequireDocItemById(Long requireDocItemSid) {
        return reqRequireDocItemMapper.selectReqRequireDocItemById(requireDocItemSid);
    }

    /**
     * 查询需求单明细列表
     * 
     * @param reqRequireDocItem 需求单明细
     * @return 需求单明细
     */
    @Override
    public List<ReqRequireDocItem> selectReqRequireDocItemList(ReqRequireDocItem reqRequireDocItem) {
        return reqRequireDocItemMapper.selectReqRequireDocItemList(reqRequireDocItem);
    }

    /**
     * 新增需求单明细
     * 需要注意编码重复校验
     * @param reqRequireDocItem 需求单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReqRequireDocItem(ReqRequireDocItem reqRequireDocItem) {
        return reqRequireDocItemMapper.insert(reqRequireDocItem);
    }

    /**
     * 修改需求单明细
     * 
     * @param reqRequireDocItem 需求单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReqRequireDocItem(ReqRequireDocItem reqRequireDocItem) {
        return reqRequireDocItemMapper.updateById(reqRequireDocItem);
    }

    /**
     * 批量删除需求单明细
     * 
     * @param requireDocItemSids 需要删除的需求单明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReqRequireDocItemByIds(List<Long> requireDocItemSids) {
        return reqRequireDocItemMapper.deleteBatchIds(requireDocItemSids);
    }


}
