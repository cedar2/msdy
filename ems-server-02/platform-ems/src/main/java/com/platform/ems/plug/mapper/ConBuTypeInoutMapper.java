package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConBuTypeInout;
import com.platform.ems.plug.domain.ConInventoryDocumentCategory;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 业务类型-出入库Mapper接口
 * 
 * @author linhongwei
 * @date 2022-10-09
 */
public interface ConBuTypeInoutMapper extends BaseMapper<ConBuTypeInout> {

    @MapKey("code")
    Map<String, Map<String, String>> selectConInvDocCatAndCodeMap();

    /**
     * 将数据库中所有库存凭证类别都查出来
     * 用于将库存凭证类别 code与name 对应
     * @return
     */
    List<ConInventoryDocumentCategory> selectAllConInventoryDocumentCategory();

    ConBuTypeInout selectConBuTypeInoutById(Long sid);

    List<ConBuTypeInout> selectConBuTypeInoutList(ConBuTypeInout conBuTypeInout);

    /**
     * 查询业务类型-出入库列表  下拉框接口
     *
     * @param conBuTypeInout 业务类型-出入库
     * @return 业务类型-出入库集合
     */
    List<ConBuTypeInout> getConBuTypeInoutList(ConBuTypeInout conBuTypeInout);

    /**
     * 添加多个
     * @param list List ConBuTypeInout
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeInout> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeInout
    * @return int
    */
    int updateAllById(ConBuTypeInout entity);

    /**
     * 更新多个
     * @param list List ConBuTypeInout
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeInout> list);


}
