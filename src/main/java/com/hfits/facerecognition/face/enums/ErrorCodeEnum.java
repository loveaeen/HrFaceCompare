package com.hfits.facerecognition.face.enums;


import com.hfits.facerecognition.face.exception.ErrorCode;
import lombok.Getter;

/**
 * @author st7251
 * @date 2019/7/2 14:28
 */
@Getter
public enum ErrorCodeEnum implements ErrorCode {

    /**
     * 成功
     */
    SUCCESS(0, "success", "成功"),
    FAIL(1, "fail", "失败"),
    PARAM_ERROR(2, "param error", "参数错误"),
    FACE_NONE(3, "No face detected", "未检测到人脸"),
    SYSTEM_ERROR(999, "system error", "系统错误"),

            ;
    private Integer code;
    private String desc;
    private String descCN;

    ErrorCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    ErrorCodeEnum(Integer code, String desc, String descCN) {
        this.code = code;
        this.desc = desc;
        this.descCN = descCN;
    }

}
