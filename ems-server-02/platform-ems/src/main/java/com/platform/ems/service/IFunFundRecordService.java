package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FunFundRecord;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资金流水Service接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface IFunFundRecordService extends IService<FunFundRecord> {
    /**
     * 查询资金流水
     *
     * @param fundRecordSid 资金流水ID
     * @return 资金流水
     */
    public FunFundRecord selectFunFundRecordById(Long fundRecordSid);

    /**
     * 查询资金流水列表
     *
     * @param funFundRecord 资金流水
     * @return 资金流水集合
     */
    public List<FunFundRecord> selectFunFundRecordList(FunFundRecord funFundRecord);

    /**
     * 新增资金流水
     *
     * @param funFundRecord 资金流水
     * @return 结果
     */
    public int insertFunFundRecord(FunFundRecord funFundRecord);

    /**
     * 修改资金流水
     *
     * @param funFundRecord 资金流水
     * @return 结果
     */
    public int updateFunFundRecord(FunFundRecord funFundRecord);

    /**
     * 更改资金账户
     *
     * @param funFundRecord 资金流水
     * @return 结果
     */
    public int setAccountNameById(FunFundRecord funFundRecord);

    /**
     * 更改所用汇票
     *
     * @param funFundRecord 资金流水
     * @return 结果
     */
    public int setHuipiaoCodeById(FunFundRecord funFundRecord);

    /**
     * 设置其它信息
     *
     * @param funFundRecord 资金流水
     * @return 结果
     */
    public int setDateStatus(FunFundRecord funFundRecord);

    /**
     * 变更资金流水
     *
     * @param funFundRecord 资金流水
     * @return 结果
     */
    public int changeFunFundRecord(FunFundRecord funFundRecord);

    /**
     * 批量删除资金流水
     *
     * @param fundRecordSids 需要删除的资金流水ID
     * @return 结果
     */
    public int deleteFunFundRecordByIds(List<Long> fundRecordSids);

    /**
     * 更改确认状态
     *
     * @param funFundRecord
     * @return
     */
    int check(FunFundRecord funFundRecord);

    /**
     * 导入
     * @param file
     * @return
     */
    Object importData(MultipartFile file);

}
