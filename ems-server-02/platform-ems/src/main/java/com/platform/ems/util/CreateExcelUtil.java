package com.platform.ems.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.platform.common.utils.bean.BeanUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 创建excel文件
 * @param <T>
 * @author c
 */
@Slf4j
@SuppressWarnings("all")
public class CreateExcelUtil<T> {

    /**
     *创建xlsx格式的 ExcelWriter writer = ExcelUtil.getWriter();
     * @param t
     * @param title 标题
     * @return
     */
    public File createExcel(List<T> t, String title) {
        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.setColumnWidth(-1,28);
        FileOutputStream fos=null;
        try {
            //out为OutputStream，需要写出到的目标流
            File excelfile = File.createTempFile(title + DateUtil.format(new Date(), "yyyyMMdd"), ".xlsx");
            fos = new FileOutputStream(excelfile, true);
            // 一次性写出内容，使用默认样式，强制输出标题
            List<T> rows = new ArrayList<>();
            rows.addAll(t);
            T o = t.get(0);
            List<Field> fieldList = BeanUtils.getAllField(o);
            writer.merge(fieldList.size()-1, title);
            fieldList.forEach(f -> {
                ApiModelProperty property = f.getAnnotation(ApiModelProperty.class);
                writer.addHeaderAlias(f.getName(), property.value());
            });
            writer.write(rows, true);
            writer.flush(fos);
            return excelfile;
        } catch (Exception e) {
           log.error(e.getMessage());
        } finally {
            // 关闭writer，释放内存
            if(writer!=null){
                try {
                    writer.close();
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
            if(fos!=null){
                try {
                    fos.close();
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        }
        return null;
    }


}
