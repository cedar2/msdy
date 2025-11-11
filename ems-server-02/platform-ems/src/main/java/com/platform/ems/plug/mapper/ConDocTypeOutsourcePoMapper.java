package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConBuTypeOutsourceDn;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeOutsourcePo;

/**
 * 单据类型_外发加工单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeOutsourcePoMapper  extends BaseMapper<ConDocTypeOutsourcePo> {


    ConDocTypeOutsourcePo selectConDocTypeOutsourcePoById(Long sid);

    List<ConDocTypeOutsourcePo> selectConDocTypeOutsourcePoList(ConDocTypeOutsourcePo conDocTypeOutsourcePo);

    /**
     * 添加多个
     * @param list List ConDocTypeOutsourcePo
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeOutsourcePo> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeOutsourcePo
    * @return int
    */
    int updateAllById(ConDocTypeOutsourcePo entity);

    /**
     * 更新多个
     * @param list List ConDocTypeOutsourcePo
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeOutsourcePo> list);

    /** 获取下拉列表 */
    List<ConDocTypeOutsourcePo> getConDocTypeOutsourcePoList();
}
