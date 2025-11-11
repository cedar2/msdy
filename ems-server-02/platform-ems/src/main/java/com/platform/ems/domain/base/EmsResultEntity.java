package com.platform.ems.domain.base;

import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求返回可能需要批量报错信息、批量提示信息和请求成功的结果数据
 *
 * @author chenkw
 * @date 2022-07-06
 */
@Data
@Accessors(chain = true)
@ApiModel
public class EmsResultEntity {

    /**
     * 状态码
     */
    public static final String ERROR_TAG = "error";

    /**
     * 返回内容
     */
    public static final String WARN_TAG = "warn";

    /**
     * 数据对象
     */
    public static final String SUCCESS_TAG = "success";

    @ApiModelProperty(value = "标签")
    String tag;

    @ApiModelProperty(value = "结果数据")
    Object data;

    @ApiModelProperty(value = "提示/报错数据信息")
    List<CommonErrMsgResponse> msgList;

    @ApiModelProperty(value = "额外的提示信息")
    List<CommonErrMsgResponse> infoList;

    @ApiModelProperty(value = "提示信息")
    String message;

    /**
     * 初始化一个新创建的 EmsResultEntity 对象，使其表示一个空消息。
     */
    public EmsResultEntity() {
        this.tag = SUCCESS_TAG;
        this.msgList = new ArrayList<>();
    }

    public EmsResultEntity(Object data) {
        this.tag = SUCCESS_TAG;
        this.msgList = new ArrayList<>();
        this.data = data;
    }

    public EmsResultEntity(Object data, String message) {
        this.tag = SUCCESS_TAG;
        this.msgList = new ArrayList<>();
        this.data = data;
        this.message = message;
    }

    public EmsResultEntity(String tag, List<CommonErrMsgResponse> msgList) {
        this.tag = tag;
        this.msgList = msgList;
    }

    public EmsResultEntity(String tag, List<CommonErrMsgResponse> msgList, String message) {
        this.tag = tag;
        this.msgList = msgList;
        this.message = message;
    }

    public EmsResultEntity(String tag, Object data, List<CommonErrMsgResponse> msgList, String message) {
        this.tag = tag;
        this.data = data;
        this.msgList = msgList;
        this.message = message;
    }


    public EmsResultEntity(String tag, Object data, List<CommonErrMsgResponse> msgList, List<CommonErrMsgResponse> infoList, String message) {
        this.tag = tag;
        this.data = data;
        this.msgList = msgList;
        this.infoList = infoList;
        this.message = message;
    }

    /**
     * 直接返回成功
     *
     * @return 成功
     */
    public static EmsResultEntity success() {
        return new EmsResultEntity();
    }

    /**
     * 返回成功数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity success(Object data) {
        return new EmsResultEntity(data);
    }

    /**
     * 返回成功数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity success(Object data, String message) {
        return new EmsResultEntity(data, message);
    }

    /**
     * 返回成功数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity success(Object data, List<CommonErrMsgResponse> msgList, String message) {
        return new EmsResultEntity(SUCCESS_TAG, data, msgList, message);
    }

    /**
     * 返回成功数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity success(Object data, List<CommonErrMsgResponse> msgList, List<CommonErrMsgResponse> infoList, String message) {
        return new EmsResultEntity(SUCCESS_TAG, data, msgList, infoList, message);
    }

    /**
     * 返回提示数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity warning(List<CommonErrMsgResponse> msgList) {
        return new EmsResultEntity(WARN_TAG, msgList);
    }

    /**
     * 返回提示数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity warning(List<CommonErrMsgResponse> msgList, String message) {
        return new EmsResultEntity(WARN_TAG, msgList, message);
    }

    /**
     * 返回提示数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity warning(Object data, List<CommonErrMsgResponse> msgList, String message) {
        return new EmsResultEntity(WARN_TAG, data, msgList, message);
    }

    /**
     * 返回提示数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity warning(Object data, List<CommonErrMsgResponse> msgList, List<CommonErrMsgResponse> infoList, String message) {
        return new EmsResultEntity(WARN_TAG, data, msgList, infoList, message);
    }

    /**
     * 返回报错数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity error(List<CommonErrMsgResponse> msgList) {
        return new EmsResultEntity(ERROR_TAG, msgList);
    }

    /**
     * 返回报错数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity error(List<CommonErrMsgResponse> msgList, String message) {
        return new EmsResultEntity(ERROR_TAG, msgList, message);
    }

    /**
     * 返回提示数据
     *
     * @return 成功数据
     */
    public static EmsResultEntity error(Object data, List<CommonErrMsgResponse> msgList, String message) {
        return new EmsResultEntity(ERROR_TAG, data, msgList, message);
    }
}
