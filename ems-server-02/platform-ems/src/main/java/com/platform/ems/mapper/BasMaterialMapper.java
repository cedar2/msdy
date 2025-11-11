package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.TecBomHead;
import com.platform.ems.domain.TecBomItem;
import com.platform.ems.domain.dto.response.BasMaterialDropDown;
import com.platform.ems.domain.dto.response.BasMaterialPicture;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料&商品&服务档案Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-12
 */
public interface BasMaterialMapper extends BaseMapper<BasMaterial> {

    BasMaterial selectBasMaterialById(Long materialSid);

    /**
     * 查询图片
     */
    BasMaterialPicture selectBasMaterialPicture(Long materialSid);

    BasMaterial selectBasMaterialBomById(Long materialSid);

    List<BasMaterial> selectBasMaterialList(BasMaterial basMaterial);

    /**
     * 下拉框接口
     */
    List<BasMaterialDropDown> selectMaterialList(BasMaterial basMaterial);

    /**
     * 添加多个
     *
     * @param list List BasMaterial
     * @return int
     */
    int inserts(@Param("list") List<BasMaterial> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasMaterial
     * @return int
     */
    int updateAllById(BasMaterial entity);

    /**
     * 更新多个
     *
     * @param list List BasMaterial
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterial> list);

    /**
     * 批量删除物料&商品&服务档案
     *
     * @param materialSids 需要删除的物料&商品&服务档案ID
     * @return 结果
     */
    int deleteBasMaterialByIds(@Param("array")Long[] materialSids);

    int countByDomain(BasMaterial params);

    /**
     * 验证物料/商品/服务编码是否已存在
     *
     * @param materialCode 物料/商品/服务编码
     * @return 结果
     */
    int checkCodeUnique(String materialCode);

    /**
     * 验证物料/商品/服务名称是否已存在
     *
     * @param basMaterial 物料/商品/服务名称
     * @return 结果
     */
    int checkNameUnique(BasMaterial basMaterial);

    /**
     * 查询物料&商品&服务档案
     *
     * @param materialCode 物料&商品&服务档案编码
     * @return 物料&商品&服务档案
     */
    BasMaterial selectBasMaterialByCode(String materialCode);

    /**
     * 根据物料编码查询物料档案Sid
     * @param materialCode
     * @return
     */
    String selectMaterialSidByCode(String materialCode);

    /**
     * 物料&商品&服务档案确认或启用/停用
     */
    int confirm(BasMaterial basMaterial);

    List<TecBomItem> selectTecBomItemList(TecBomHead tecBomHead);

    /**
     * 用来查询商品附件中没有上传工艺单附件的所有商品
     */
    List<BasMaterial> selectNotGydList(BasMaterial basMaterial);

    BasMaterial getName(BasMaterial basMaterial);
}
