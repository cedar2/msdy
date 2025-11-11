package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DelOutsourceDeliveryNote;

import java.util.List;

/**
 * 外发加工收料单Service接口
 *
 * @author linhongwei
 * @date 2021-05-17
 */
public interface IDelOutsourceDeliveryNoteService extends IService<DelOutsourceDeliveryNote> {
    /**
     * 查询外发加工收料单
     *
     * @param deliveryNoteSid 外发加工收料单ID
     * @return 外发加工收料单
     */
    public DelOutsourceDeliveryNote selectDelOutsourceDeliveryNoteById(Long deliveryNoteSid);

    /**
     * 查询外发加工收料单列表
     *
     * @param delOutsourceDeliveryNote 外发加工收料单
     * @return 外发加工收料单集合
     */
    public List<DelOutsourceDeliveryNote> selectDelOutsourceDeliveryNoteList(DelOutsourceDeliveryNote delOutsourceDeliveryNote);

    /**
     * 新增外发加工收料单
     *
     * @param delOutsourceDeliveryNote 外发加工收料单
     * @return 结果
     */
    public int insertDelOutsourceDeliveryNote(DelOutsourceDeliveryNote delOutsourceDeliveryNote);

    /**
     * 修改外发加工收料单
     *
     * @param delOutsourceDeliveryNote 外发加工收料单
     * @return 结果
     */
    public int updateDelOutsourceDeliveryNote(DelOutsourceDeliveryNote delOutsourceDeliveryNote);

    /**
     * 变更外发加工收料单
     *
     * @param delOutsourceDeliveryNote 外发加工收料单
     * @return 结果
     */
    public int changeDelOutsourceDeliveryNote(DelOutsourceDeliveryNote delOutsourceDeliveryNote);

    /**
     * 批量删除外发加工收料单
     *
     * @param deliveryNoteSids 需要删除的外发加工收料单ID
     * @return 结果
     */
    public int deleteDelOutsourceDeliveryNoteByIds(List<Long> deliveryNoteSids);

    /**
     * 更改确认状态
     *
     * @param delOutsourceDeliveryNote
     * @return
     */
    int check(DelOutsourceDeliveryNote delOutsourceDeliveryNote);

    /**
     * 提交前校验-外发加工收料单
     */
    int verify(Long deliveryNoteSid, String handleStatus);
}
