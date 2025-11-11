package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConLaborTypeItem;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工价类型/工价费用项对照Service接口
 *
 * @author linhongwei
 * @date 2021-06-10
 */
public interface IConLaborTypeItemService extends IService<ConLaborTypeItem>{
    /**
     * 查询工价类型/工价费用项对照
     *
     * @param laborTypeItemSid 工价类型/工价费用项对照ID
     * @return 工价类型/工价费用项对照
     */
    public ConLaborTypeItem selectConLaborTypeItemById(Long laborTypeItemSid);

    /**
     * 查询工价类型/工价费用项对照列表  (主表详情的明细页面按序号+名称排序)
     *
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 工价类型/工价费用项对照集合
     */
    public List<ConLaborTypeItem> selectConLaborTypeItemList(ConLaborTypeItem conLaborTypeItem);

    /**
     * 查询工价类型/工价费用项对照列表  (查询页面按工价类型+编码排序)
     * @author chenkw
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 工价类型/工价费用项对照集合
     */
    public List<ConLaborTypeItem> selectTypeItemList(ConLaborTypeItem conLaborTypeItem);

    /**
     * 新增工价类型/工价费用项对照
     *
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 结果
     */
    public int insertConLaborTypeItem(ConLaborTypeItem conLaborTypeItem);

    /**
     * 修改工价类型/工价费用项对照
     *
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 结果
     */
    public int updateConLaborTypeItem(ConLaborTypeItem conLaborTypeItem);

    /**
     * 变更工价类型/工价费用项对照
     *
     * @param conLaborTypeItem 工价类型/工价费用项对照
     * @return 结果
     */
    public int changeConLaborTypeItem(ConLaborTypeItem conLaborTypeItem);

    /**
     * 批量删除工价类型/工价费用项对照
     *
     * @param laborTypeItemSids 需要删除的工价类型/工价费用项对照ID
     * @return 结果
     */
    public int deleteConLaborTypeItemByIds(List<Long> laborTypeItemSids);

    /**
    * 启用/停用
    * @param conLaborTypeItem
    * @return
    */
    int changeStatus(ConLaborTypeItem conLaborTypeItem);

    /**
     * 更改确认状态
     * @param conLaborTypeItem
     * @return
     */
    int check(ConLaborTypeItem conLaborTypeItem);


    /**  获取下拉列表 */
    List<ConLaborTypeItem> getConLaborTypeItemList();

    /**
     * 导入
     * @param file
     * @return
     */
    Object importData(MultipartFile file);

}
