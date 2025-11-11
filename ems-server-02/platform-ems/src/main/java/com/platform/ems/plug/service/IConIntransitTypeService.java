package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConIntransitType;

/**
 * 在途类型Service接口
 * 
 * @author linhongwei
 * @date 2021-05-20
 */
public interface IConIntransitTypeService extends IService<ConIntransitType>{
    /**
     * 查询在途类型
     * 
     * @param sid 在途类型ID
     * @return 在途类型
     */
    public ConIntransitType selectConIntransitTypeById(Long sid);

    /**
     * 查询在途类型列表
     * 
     * @param conIntransitType 在途类型
     * @return 在途类型集合
     */
    public List<ConIntransitType> selectConIntransitTypeList(ConIntransitType conIntransitType);

    /**
     * 新增在途类型
     * 
     * @param conIntransitType 在途类型
     * @return 结果
     */
    public int insertConIntransitType(ConIntransitType conIntransitType);

    /**
     * 修改在途类型
     * 
     * @param conIntransitType 在途类型
     * @return 结果
     */
    public int updateConIntransitType(ConIntransitType conIntransitType);

    /**
     * 变更在途类型
     *
     * @param conIntransitType 在途类型
     * @return 结果
     */
    public int changeConIntransitType(ConIntransitType conIntransitType);

    /**
     * 批量删除在途类型
     * 
     * @param sids 需要删除的在途类型ID
     * @return 结果
     */
    public int deleteConIntransitTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conIntransitType
    * @return
    */
    int changeStatus(ConIntransitType conIntransitType);

    /**
     * 更改确认状态
     * @param conIntransitType
     * @return
     */
    int check(ConIntransitType conIntransitType);

}
