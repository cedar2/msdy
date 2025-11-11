package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FunFundAccountAttach;

/**
 * 资金账户信息-附件Service接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface IFunFundAccountAttachService extends IService<FunFundAccountAttach> {
    /**
     * 查询资金账户信息-附件
     *
     * @param fundAccountAttachSid 资金账户信息-附件ID
     * @return 资金账户信息-附件
     */
    public FunFundAccountAttach selectFunFundAccountAttachById(Long fundAccountAttachSid);

    /**
     * 查询资金账户信息-附件列表
     *
     * @param funFundAccountAttach 资金账户信息-附件
     * @return 资金账户信息-附件集合
     */
    public List<FunFundAccountAttach> selectFunFundAccountAttachList(FunFundAccountAttach funFundAccountAttach);

    /**
     * 新增资金账户信息-附件
     *
     * @param funFundAccountAttach 资金账户信息-附件
     * @return 结果
     */
    public int insertFunFundAccountAttach(FunFundAccountAttach funFundAccountAttach);

    /**
     * 修改资金账户信息-附件
     *
     * @param funFundAccountAttach 资金账户信息-附件
     * @return 结果
     */
    public int updateFunFundAccountAttach(FunFundAccountAttach funFundAccountAttach);

    /**
     * 变更资金账户信息-附件
     *
     * @param funFundAccountAttach 资金账户信息-附件
     * @return 结果
     */
    public int changeFunFundAccountAttach(FunFundAccountAttach funFundAccountAttach);

    /**
     * 批量删除资金账户信息-附件
     *
     * @param fundAccountAttachSids 需要删除的资金账户信息-附件ID
     * @return 结果
     */
    public int deleteFunFundAccountAttachByIds(List<Long> fundAccountAttachSids);


}
