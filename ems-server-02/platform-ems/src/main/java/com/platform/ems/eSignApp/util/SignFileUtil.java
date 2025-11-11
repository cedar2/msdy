package com.platform.ems.eSignApp.util;

import com.platform.ems.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.text.MessageFormat;

/**
 * @author chenkw
 * @since 2024-02-21
 */
@Component
public class SignFileUtil {

    @Resource
    private MinioConfig minioConfig;
    @Resource
    private MinioClient client;

    /**
     * 从Minio 获取文件字节流
     */
    public GetObjectResponse getInputStream(String filePath) throws Exception {
        String filepath = "/" + minioConfig.getBucketName();
        int size = filepath.length();
        String path = filePath.substring(filePath.indexOf(filepath) + size);
        return client.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(path).build());
    }

    /**
     * 从Minio 获取文件字节流生成临时文件
     */
    public File getTempFile(String filePath, String fileName) throws Exception {
        // 返回去掉最后一个`.`及其后缀的文件名
        String name = fileName.substring(0, fileName.lastIndexOf("."));
        // 临时文件
        return asFileTemp(getInputStream(filePath), name,".pdf");
    }


    /**
     * 生成临时文件
     */
    public static File asFileTemp(InputStream inputStream, String pre,String suf) throws IOException {
        File tmp = File.createTempFile(pre, suf);
        OutputStream os = new FileOutputStream(tmp);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        return tmp;
    }

    /***
     * 计算文件的Content-MD5
     */
    public String getFileContentMD5(File file) {
        // 获取文件MD5的二进制数组（128位）
        byte[] bytes = getFileMD5Bytes128(file);
        // 对文件MD5的二进制数组进行base64编码
        return new String(Base64.encodeBase64(bytes));
    }

    /***
     * 获取文件MD5-二进制数组（128位）
     */
    public byte[] getFileMD5Bytes128(File file) {
        FileInputStream fis = null;
        byte[] md5Bytes = null;
        try {
            fis = new FileInputStream(file);
            // 加密
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md5.update(buffer, 0, length);
            }
            md5Bytes = md5.digest();
            fis.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return md5Bytes;
    }

    /***
     * 远程URL地址读取文件Bytes
     */
    public static byte[] getFileBytesFromUrl(String remoteUrl) throws Exception {
        byte[] fileBytes = null;
        InputStream inStream = null;
        ByteArrayOutputStream bOutStream = null;
        try {
            URL url = new URL(remoteUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.connect();
            inStream = httpConn.getInputStream();
            bOutStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                bOutStream.write(buffer, 0, len);
            }
            fileBytes = bOutStream.toByteArray();
        } catch (UnknownHostException e) {
            String msg = MessageFormat.format("网络请求时发生UnknownHostException异常: {0}", e.getMessage());
            Exception ex = new Exception(msg);
            ex.initCause(e);
            throw ex;
        } catch (MalformedURLException e) {
            String msg = MessageFormat.format("网络请求时发生MalformedURLException异常: {0}", e.getMessage());
            Exception ex = new Exception(msg);
            ex.initCause(e);
            throw ex;
        } catch (IOException e) {
            String msg = MessageFormat.format("从远程Url中获取文件流时发生IOException异常: {0}", e.getMessage());
            Exception ex = new Exception(msg);
            ex.initCause(e);
            throw ex;
        } finally {
            if (null != inStream) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    String msg = MessageFormat.format("关闭InputStream时发生异常: {0}", e.getMessage());
                    Exception ex = new Exception(msg);
                    ex.initCause(e);
                    throw ex;
                }
            }
            if (null != bOutStream) {
                try {
                    bOutStream.close();
                } catch (IOException e) {
                    String msg = MessageFormat.format("关闭ByteArrayOutputStream时发生异常: {0}", e.getMessage());
                    Exception ex = new Exception(msg);
                    ex.initCause(e);
                    throw ex;
                }
            }
        }
        return fileBytes;
    }

    /***
     * 将获取的文件Bytes保存到本地磁盘
     */
    public static void saveBytesAsFile(byte[] bytes, String filePath, String fileName) throws Exception {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        File dir = new File(filePath);
        if (!dir.exists() && dir.isDirectory()) {
            // 文件目录不存在时先创建目录
            dir.mkdirs();
        }
        file = new File(filePath + fileName);
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (FileNotFoundException e) {
            String msg = MessageFormat.format("文件保存时发生FileNotFoundException异常: {0}", e.getMessage());
            Exception ex = new Exception(msg);
            ex.initCause(e);
            throw ex;
        } catch (IOException e) {
            String msg = MessageFormat.format("文件保存时发生IOException异常: {0}", e.getMessage());
            Exception ex = new Exception(msg);
            ex.initCause(e);
            throw ex;
        } finally {
            if (null != bos) {
                try {
                    bos.close();
                } catch (IOException e) {
                    String msg = MessageFormat.format("文件保存时发生IOException异常: {0}", e.getMessage());
                    Exception ex = new Exception(msg);
                    ex.initCause(e);
                    throw ex;
                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    String msg = MessageFormat.format("文件保存时发生IOException异常: {0}", e.getMessage());
                    Exception ex = new Exception(msg);
                    ex.initCause(e);
                    throw ex;
                }
            }
        }
    }

}
