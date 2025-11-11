package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.FinBookAccountReceivable;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-应收Service接口
 *
 * @author linhongwei
 * @date 2021-06-11
 */
public interface IFinBookAccountReceivableService extends IService<FinBookAccountReceivable>{

    /**
     * 报表流水查询
     * @param request FinBookAccountReceivableFormRequest
     * @return
     */
    List<FinBookAccountReceivable> getReportForm(FinBookAccountReceivable request);

    /**
     * 导入
     * @param file
     * @return
     */
    AjaxResult importData(MultipartFile file);


    /**
     * 记账
     * @param list
     * @return
     */
    int addForm(List<FinBookAccountReceivable> list);
}
