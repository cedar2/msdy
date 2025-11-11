package com.platform.ems.service;

import java.io.IOException;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.TecBomHead;
import com.platform.ems.domain.TecBomItem;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.BomSortResponse;
import com.platform.ems.domain.dto.response.TecBomHeadMaterialReportResponse;
import com.platform.ems.domain.dto.response.TecBomHeadReportResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 物料清单（BOM）主Service接口
 *
 * @author qhq
 * @date 2021-03-15
 */
public interface ITecBomHeadService extends IService<TecBomHead>{
    /**
     * 查询物料清单（BOM）主
     *
     * @param tecBomHead 物料清单（BOM）主ID
     * @return 物料清单（BOM）主
     */
    public List<TecBomHead> selectTecBomHeadById(TecBomHead tecBomHead);

    public TecBomHead getBom(TecBomHead tecBomHead);

    /**
     * 查询物料清单（BOM）主
     *
     * @paramh  bom拷贝
     * @return 物料清单（BOM）主
     */
    public List<TecBomItem>  getBomItemM(TecBomHead tecBomHead);

    public void export(HttpServletResponse response, TecBomHead tecBomHead) throws IOException;

    /**
     * 查询物料清单（BOM）主列表
     *
     * @param tecBomHead 物料清单（BOM）主
     * @return 物料清单（BOM）主集合
     */
    public List<TecBomHead> selectTecBomHeadList(TecBomHead tecBomHead);

    public List<TecBomHead> selectTecBomHeadListNew(TecBomHead tecBomHead);

    /**
     * bom报表
     *
     */
    public List<TecBomHeadReportResponse> report(TecBomHeadReportRequest tecBomHead);
    /**
     * 更改采购类型
     *
     */
    public int changePurchaseType(TecBomHeadReportPurchaseRequest request);

    public List<TecBomHeadMaterialReportResponse> reportMaterial(TecBomHeadReportRequest tecBomHead);

    /**
     * BOM序号排序 刷新序号
     *
     */
    public List<TecBomItem> sortItem(List<TecBomItem> itemList);
    /**
     * BOM序号排序 刷新序号 新建
     *
     */
    public List<BomSortResponse> sortItemAdd(List<BomSortResponse> itemList);

    public List<Long>  test(List<Long> sids);

    public void setIsM(List<TecBomHeadReportResponse> list);
    /**
     * 组合拉链物料
     */
    public int insertZipper(TecBomHead tecBomHead);

    /**
     * 组合拉链物料 详情
     */
    public TecBomHead getZipper(Long materialSid);

    /**
     * 组合拉链物料 修改
     */
    public int editZipper(TecBomHead tecBomHead);

    /**
     * 新增物料清单（BOM）主
     *
     * @param bomList 物料清单（BOM）主
     * @return 结果
     */
    public int insertTecBomHead(List<TecBomHead> bomList);

    /**
     * 修改物料清单（BOM）主
     *
     * @param bomList 物料清单（BOM）主
     * @return 结果
     */
    public int updateTecBomHead(List<TecBomHead> bomList);

    /**
     * 修改物料清单（BOM）主 新
     *
     * @param bomHead 物料清单（BOM）主
     * @return 结果
     */
    public int editTecBomHead(TecBomHead bomHead);
    //bom 物料替换
    public int exChange(TecBomHeadExchangeRequest request);
    //物料颜色替换校验
    public int changeJudge(List<TecBomHeadReportExSidRequest> list);

    public AjaxResult Judge(TecBomHeadExChangeJudgeRequest request);
    /**
     * 批量删除物料清单（BOM）主
     *
     * @param materialSids 需要删除的物料清单（BOM）主ID
     * @return 结果
     */
    public int deleteTecBomHeadByIds(List<Long>  materialSids);

    /**
     * 删除物料清单（BOM）主信息
     *
     * @param bomId 物料清单（BOM）主ID
     * @return 结果
     */
    public int deleteTecBomHeadById(Long bomId);

    /**
     * 修改处理状态
     * @param tecBomHead
     * @return
     */
    int editHandleStatus(TecBomHead tecBomHead);

    /**
     * 修改启停用状态
     * @param tecBomHead
     * @return
     */
    int editStatus(TecBomHead tecBomHead) ;

    /**
     * 修改bomStatus
     * @param tecBomHead
     * @return
     */
    int updateBomStatus(TecBomHead tecBomHead);


    /**
     * 获取Bom明细列表
     * @return
     */
    List<TecBomItem> selectTecBomItemList(TecBomHead tecBomHead);

    /**
     * 根据materialSid查询主表记录
     */
    List<TecBomHead> getListByMaterialSid(Long materialSid);

    /**
     * 提交审批验证
     * @param bomHead
     * @return
     */
    public AjaxResult processCheck(TecBomHead bomHead);

    public void insertZipperSku(List<TecBomHead> bomList);
    /**
     * bom导入 -编辑页面
     */
    public AjaxResult importBOM(MultipartFile file, String materialCode, String sampleCodeSelf);

    /**
     * bom导入-新建页面
     */
    public AjaxResult importBOMAdd(MultipartFile file, String materialCode, String sampleCodeSelf);

}
