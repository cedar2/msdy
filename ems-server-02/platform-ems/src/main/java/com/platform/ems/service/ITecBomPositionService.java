package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.TecBomPosition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * BOM部位档案Service接口
 * 
 * @author linhongwei
 * @date 2022-07-07
 */
public interface ITecBomPositionService extends IService<TecBomPosition>{
    /**
     * 查询BOM部位档案
     * 
     * @param bomPositionById BOM部位档案ID
     * @return BOM部位档案
     */
    public TecBomPosition selectTecBomPositionById(Long bomPositionById);

    /**
     * 查询BOM部位档案列表
     * 
     * @param tecBomPosition BOM部位档案
     * @return BOM部位档案集合
     */
    public List<TecBomPosition> selectTecBomPositionList(TecBomPosition tecBomPosition);

    /**
     * 新增BOM部位档案
     * 
     * @param tecBomPosition BOM部位档案
     * @return 结果
     */
    public int insertTecBomPosition(TecBomPosition tecBomPosition);

    /**
     * 修改BOM部位档案
     * 
     * @param tecBomPosition BOM部位档案
     * @return 结果
     */
    public int updateTecBomPosition(TecBomPosition tecBomPosition);

    /**
     * 变更BOM部位档案
     *
     * @param tecBomPosition BOM部位档案
     * @return 结果
     */
    public int changeTecBomPosition(TecBomPosition tecBomPosition);


    /**
     * 批量删除BOM部位档案
     * 
     * @param tecBomPositionSids 需要删除的BOM部位档案ID
     * @return 结果
     */
    public int deleteTecBomPositionByIds(List<Long> tecBomPositionSids);

    /**
    * 启用/停用
    * @param tecBomPosition
    * @return
    */
    int changeStatus(TecBomPosition tecBomPosition);

    /**
     * 更改确认状态
     * @param tecBomPosition
     * @return
     */
    int check(TecBomPosition tecBomPosition);
    /**
     * BOM部位 导入
     */
    public AjaxResult importDataPur(MultipartFile file);

}
