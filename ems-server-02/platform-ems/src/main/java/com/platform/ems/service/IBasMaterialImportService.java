package com.platform.ems.service;

import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

/**
 * 物料&商品&服务档案导入Service接口
 *
 * @author chenkw
 * @date 2022-01-20
 */
public interface IBasMaterialImportService {

    /**
     * 导入文件的数据批量写入
     *
     * @author chenkw
     */
    int importDataSecond(List<BasMaterial> basMaterialList);

    /**
     * 常规物料
     *
     * @author chenkw
     * @date 2022-01-20
     */
    HashMap<String, Object> importDataCg(MultipartFile file);

    /**
     * 辅料
     *
     * @author chenkw
     * @date 2022-01-20
     */
    HashMap<String, Object> importData(MultipartFile file);

    /**
     * 面料
     *
     * @author chenkw
     * @date 2022-01-20
     */
    HashMap<String, Object>  importDataM(MultipartFile file);

    /**
     * 商品(鞋服)
     *
     * @author chenkw
     */
    HashMap<String, Object> importDataG(MultipartFile file);

    /**
     * 普通商品
     *
     * @author chenkw
     */
    HashMap<String, Object> importDataProduct(MultipartFile file);

    /**
     * 更新数据导入运营状态
     * @param file 文件
     * @return 返回
     */
    EmsResultEntity importUpdateSaleStation(MultipartFile file);
}
