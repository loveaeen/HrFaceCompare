package com.hfits.facerecognition.face.exception;

import lombok.Data;


/**
 * @Author: st7251
 * @Date: 2018/11/23 14:18
 */
@Data
public class FaceException extends RuntimeException {
    private ErrorCode errorCode;
    private String msg;
    private String msgCN;

    public FaceException(Response response) {
        this.errorCode = new ErrorCode() {
            @Override
            public Integer getCode() {
                return response.getCode();
            }

            @Override
            public String getDesc() {
                return response.getMsg();
            }

            @Override
            public String getDescCN() {
                return response.getMsg();
            }
        };
        this.msg=response.getMsg();
        this.msgCN=response.getMsg();
    }

    public FaceException(ErrorCode errorCode) {
        super(errorCode.getDesc());
        this.errorCode = errorCode;
        this.msg= errorCode.getDesc();
        this.msgCN=errorCode.getDescCN();
    }

    public FaceException(ErrorCode errorCode, String msg) {
        super(errorCode.getDesc());
        this.errorCode = errorCode;
        this.msg = msg;
        this.msgCN=msg;
    }

    public FaceException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
        this.msg= errorCode.getDesc();
        this.msgCN=errorCode.getDescCN();
    }


    public FaceException(Throwable cause, ErrorCode errorCode, String msg) {
        super(cause);
        this.errorCode = errorCode;
        this.msg = msg;
        this.msgCN=msg;
    }
}
