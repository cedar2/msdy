package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOutsourceSettleAttach;

/**
 * 外发加工费结算单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-06-10
 */
public interface IManManufactureOutsourceSettleAttachService extends IService<ManManufactureOutsourceSettleAttach>{
    /**
     * 查询外发加工费结算单-附件
     * 
     * @param manufactureOutsourceSettleAttachSid 外发加工费结算单-附件ID
     * @return 外发加工费结算单-附件
     */
    public ManManufactureOutsourceSettleAttach selectManManufactureOutsourceSettleAttachById(Long manufactureOutsourceSettleAttachSid);

    /**
     * 查询外发加工费结算单-附件列表
     * 
     * @param manManufactureOutsourceSettleAttach 外发加工费结算单-附件
     * @return 外发加工费结算单-附件集合
     */
    public List<ManManufactureOutsourceSettleAttach> selectManManufactureOutsourceSettleAttachList(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach);

    /**
     * 新增外发加工费结算单-附件
     * 
     * @param manManufactureOutsourceSettleAttach 外发加工费结算单-附件
     * @return 结果
     */
    public int insertManManufactureOutsourceSettleAttach(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach);

    /**
     * 修改外发加工费结算单-附件
     * 
     * @param manManufactureOutsourceSettleAttach 外发加工费结算单-附件
     * @return 结果
     */
    public int updateManManufactureOutsourceSettleAttach(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach);

    /**
     * 变更外发加工费结算单-附件
     *
     * @param manManufactureOutsourceSettleAttach 外发加工费结算单-附件
     * @return 结果
     */
    public int changeManManufactureOutsourceSettleAttach(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach);

    /**
     * 批量删除外发加工费结算单-附件
     * 
     * @param manufactureOutsourceSettleAttachSids 需要删除的外发加工费结算单-附件ID
     * @return 结果
     */
    public int deleteManManufactureOutsourceSettleAttachByIds(List<Long> manufactureOutsourceSettleAttachSids);

    /**
     * 更改确认状态
     * @param manManufactureOutsourceSettleAttach
     * @return
     */
    int check(ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach);

}
