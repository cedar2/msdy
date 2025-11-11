package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecBomHead;
import com.platform.ems.domain.TecBomItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料清单（BOM）主Mapper接口
 *
 * @author qhq
 * @date 2021-03-15
 */
public interface TecBomHeadMapper  extends BaseMapper<TecBomHead> {


	List<TecBomHead> selectTecBomHeadByMaterialSid(Long materialSid);
    List<TecBomHead> selectTecBomHeadList(TecBomHead tecBomHead);
    List<TecBomHead> selectTecBomHeadNewList(TecBomHead tecBomHead);
    List<TecBomHead> getAllBom();
    /**
     * 添加多个
     * @param list List TecBomHead
     * @return int
     */
    int inserts(@Param("list") List<TecBomHead> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity TecBomHead
     * @return int
     */
    int updateAllById(TecBomHead entity);

    /**
     * 更新多个
     * @param list List TecBomHead
     * @return int
     */
    int updatesAllById(@Param("list") List<TecBomHead> list);

    /**
     *  修改handelStatus
     * @param tecBomHead
     * @return
     */
    int updateHandleStatus(TecBomHead tecBomHead);

    /**
     *  修改Status
     * @param tecBomHead
     * @return
     */
    int updateStatus(TecBomHead tecBomHead);

    /**
     * 修改bomstatus
     */
    int updateBomStatus(TecBomHead tecBomHead);

    /**
     * 修改流程参数
     * @return
     */
    int updateProcessValue(TecBomHead tecBomHead);

    List<TecBomHead> getUsetTaskList(TecBomHead tecBomHead);

    /**
     * 获取Bom明细列表
     * @return
     */
    List<TecBomItem> selectTecBomItemList(TecBomHead tecBomHead);
}
