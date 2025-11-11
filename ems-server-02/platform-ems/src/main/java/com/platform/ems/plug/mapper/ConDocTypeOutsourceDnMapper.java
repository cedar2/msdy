package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConDocTypeOutsourcePo;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeOutsourceDn;

/**
 * 单据类型_外发加工交货单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeOutsourceDnMapper  extends BaseMapper<ConDocTypeOutsourceDn> {


    ConDocTypeOutsourceDn selectConDocTypeOutsourceDnById(Long sid);

    List<ConDocTypeOutsourceDn> selectConDocTypeOutsourceDnList(ConDocTypeOutsourceDn conDocTypeOutsourceDn);

    /**
     * 添加多个
     * @param list List ConDocTypeOutsourceDn
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeOutsourceDn> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeOutsourceDn
    * @return int
    */
    int updateAllById(ConDocTypeOutsourceDn entity);

    /**
     * 更新多个
     * @param list List ConDocTypeOutsourceDn
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeOutsourceDn> list);

    /** 获取下拉列表 */
    List<ConDocTypeOutsourceDn> getConDocTypeOutsourceDnList();
}
