package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConDiscountType;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeOutsourcePo;

/**
 * 业务类型_外发加工单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeOutsourcePoMapper  extends BaseMapper<ConBuTypeOutsourcePo> {


    ConBuTypeOutsourcePo selectConBuTypeOutsourcePoById(Long sid);

    List<ConBuTypeOutsourcePo> selectConBuTypeOutsourcePoList(ConBuTypeOutsourcePo conBuTypeOutsourcePo);

    /**
     * 添加多个
     * @param list List ConBuTypeOutsourcePo
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeOutsourcePo> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeOutsourcePo
    * @return int
    */
    int updateAllById(ConBuTypeOutsourcePo entity);

    /**
     * 更新多个
     * @param list List ConBuTypeOutsourcePo
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeOutsourcePo> list);

    /** 获取下拉列表 */
    List<ConBuTypeOutsourcePo> getConBuTypeOutsourcePoList();
}
