package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasSkuGroup;
import com.platform.ems.domain.TecModelPositionGroup;

/**
 * 版型部位组档案Service接口
 *
 * @author linhongwei
 * @date 2021-06-02
 */
public interface ITecModelPositionGroupService extends IService<TecModelPositionGroup>{
    /**
     * 查询版型部位组档案
     *
     * @param groupSid 版型部位组档案ID
     * @return 版型部位组档案
     */
    public TecModelPositionGroup selectTecModelPositionGroupById(Long groupSid);

    /**
     * 查询版型部位组档案列表
     *
     * @param tecModelPositionGroup 版型部位组档案
     * @return 版型部位组档案集合
     */
    public List<TecModelPositionGroup> selectTecModelPositionGroupList(TecModelPositionGroup tecModelPositionGroup);

    List<TecModelPositionGroup>  getList();

    /**
     * 新增版型部位组档案
     *
     * @param tecModelPositionGroup 版型部位组档案
     * @return 结果
     */
    public int insertTecModelPositionGroup(TecModelPositionGroup tecModelPositionGroup);

    /**
     * 修改版型部位组档案
     *
     * @param tecModelPositionGroup 版型部位组档案
     * @return 结果
     */
    public int updateTecModelPositionGroup(TecModelPositionGroup tecModelPositionGroup);

    /**
     * 变更版型部位组档案
     *
     * @param tecModelPositionGroup 版型部位组档案
     * @return 结果
     */
    public int changeTecModelPositionGroup(TecModelPositionGroup tecModelPositionGroup);

    /**
     * 批量删除版型部位组档案
     *
     * @param groupSids 需要删除的版型部位组档案ID
     * @return 结果
     */
    public int deleteTecModelPositionGroupByIds(List<String> groupSids);

    /**
    * 启用/停用
    * @param tecModelPositionGroup
    * @return
    */
    int changeStatus(TecModelPositionGroup tecModelPositionGroup);

    /**
     * 更改确认状态
     * @param tecModelPositionGroup
     * @return
     */
    int check(TecModelPositionGroup tecModelPositionGroup);

}
