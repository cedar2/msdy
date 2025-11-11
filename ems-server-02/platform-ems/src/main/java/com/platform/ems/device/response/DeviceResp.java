package com.platform.ems.device.response;

import com.platform.common.exception.CheckedException;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static com.platform.ems.device.api.DeviceAPI.G;

/**
 * @author Straw
 * @since 2023/3/22
 */
@Data
@Accessors(chain = true)
@SuppressWarnings("SpellCheckingInspection")
public class DeviceResp {
    String body; // 原始json数据，返给前端，便于debug

    String result_flag;
    String return_infor;
    String msg;
    String data_item_num; // 接收数据条数，统计员工记录条数

    public static <T extends DeviceResp> T wrap(CloseableHttpResponse resp, Class<T> t) {
        try {
            String body = EntityUtils.toString(resp.getEntity());
            // noinspection unchecked
            return (T) G.fromJson(body, t).setBody(body);
        } catch (IOException e) {
            throw new CheckedException("解析物联设备的响应失败: " + e);
        }

    }

    public boolean isSuccess() {
        return "S".equals(result_flag);
    }

    public void requireSuccess() {
        body = StringEscapeUtils.unescapeJava(body);
        String throwMessage;

        if (result_flag == null) {
            throwMessage = "物联设备响应异常";
        } else {
            switch (result_flag) {
                case "S":
                    return;
                case "E":
                    throwMessage = "物联设备响应失败";
                    break;
                case "W":
                    throwMessage = "物联设备响应警告";
                    break;
                default:
                    throwMessage = "物联设备响应异常";
                    break;
            }
        }

        throwMessage += "：" + body;
        System.out.println(throwMessage);
        throw new CheckedException(throwMessage);
    }

}
