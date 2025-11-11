package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.CosCostLaborTemplate;

/**
 * 商品成本核算-工价成本模板-主Service接口
 * 
 * @author qhq
 * @date 2021-04-02
 */
public interface ICosCostLaborTemplateService extends IService<CosCostLaborTemplate>{
    /**
     * 查询商品成本核算-工价成本模板-主
     *
     * @param costLaborTemplateSid 商品成本核算-工价成本模板-主ID
     * @return 商品成本核算-工价成本模板-主
     */
    public CosCostLaborTemplate selectCosCostLaborTemplateById(Long costLaborTemplateSid);

    /**
     * 查询商品成本核算-工价成本模板-主列表
     *
     * @param cosCostLaborTemplate 商品成本核算-工价成本模板-主
     * @return 商品成本核算-工价成本模板-主集合
     */
    public List<CosCostLaborTemplate> selectCosCostLaborTemplateList(CosCostLaborTemplate cosCostLaborTemplate);

    /**
     * 新建编辑时校验
     *
     * @param cosCostLaborTemplate 商品成本核算-工价成本模板-主
     * @return 结果 sid
     */
    AjaxResult checkUnique(CosCostLaborTemplate cosCostLaborTemplate);

    /**
     * 新增商品成本核算-工价成本模板-主
     *
     * @param cosCostLaborTemplate 商品成本核算-工价成本模板-主
     * @return 结果
     */
    public String insertCosCostLaborTemplate(CosCostLaborTemplate cosCostLaborTemplate);

    /**
     * 修改商品成本核算-工价成本模板-主
     *
     * @param cosCostLaborTemplate 商品成本核算-工价成本模板-主
     * @return 结果
     */
    public String updateCosCostLaborTemplate(CosCostLaborTemplate cosCostLaborTemplate);

    /**
     * 变更商品成本核算-工价成本模板-主
     *
     * @param cosCostLaborTemplate 商品成本核算-工价成本模板-主
     * @return 结果
     */
    public int changeCosCostLaborTemplate(CosCostLaborTemplate cosCostLaborTemplate);

    /**
     * 批量删除商品成本核算-工价成本模板-主
     *
     * @param costLaborTemplateSids 需要删除的商品成本核算-工价成本模板-主ID
     * @return 结果
     */
    public int deleteCosCostLaborTemplateByIds(List<Long>  costLaborTemplateSids);

    /**
     * 启用/停用
     * @param cosCostLaborTemplate
     * @return
     */
    int changeStatus(CosCostLaborTemplate cosCostLaborTemplate);

    /**
     * 更改确认状态
     * @param cosCostLaborTemplate
     * @return
     */
    int check(CosCostLaborTemplate cosCostLaborTemplate);

    /**
     * 复制
     * @param costLaborTemplateSid
     * @return
     */
    CosCostLaborTemplate copy(Long costLaborTemplateSid, boolean type);

}
