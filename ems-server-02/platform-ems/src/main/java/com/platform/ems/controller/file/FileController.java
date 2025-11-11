package com.platform.ems.controller.file;

import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;


/**
 * 附件下载
 *
 * @author chen
 * @date 2021-09-07
 */
@RestController
@RequestMapping("/file")
@Api(tags = "附件")
public class FileController {

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    @PostMapping("/downloadTemplate")
    public void download(String filePath, HttpServletRequest request, HttpServletResponse response) throws Exception{
        String filepath = "/" + minioConfig.getBucketName();
        int size = filepath.length();
        filePath = filePath.substring(filePath.indexOf(filepath) + size);
        InputStream inputStream = client.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(filePath).build());
        OutputStream outputStream = null;
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            response.reset();
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filePath, "UTF-8"));
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            outputStream = response.getOutputStream();
            //输出文件
            while((len = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }finally {
            if(inputStream!=null){
                inputStream.close();
            }
            if(outputStream!=null){
                outputStream.close();
            }
        }
    }

    @PostMapping("/delete")
    public AjaxResult deleteFile(String filePath){
        try {
            String filepath = "/" + minioConfig.getBucketName();
            int size = filepath.length();
            filePath = filePath.substring(filePath.indexOf(filepath) + size);
            client.removeObject(RemoveObjectArgs.builder().bucket(minioConfig.getBucketName()).object(filePath).build());
        }catch (Exception e){
            throw new BaseException("删除失败，请重试！");
        }
        return AjaxResult.success();
    }



}
