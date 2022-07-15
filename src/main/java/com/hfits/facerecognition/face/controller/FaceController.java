package com.hfits.facerecognition.face.controller;

import com.hfits.facerecognition.face.FacePainter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 人脸识别入口
 * @author hanzhiguo
 * @date 2021/12/08
 */
@Controller
@RequestMapping()
public class FaceController {

    @Autowired
    private FacePainter facePainter;

    /**
     * 调用人脸识别，并返回结果
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("/startFaceRecognition")
    @ResponseBody
    public String face() throws InterruptedException {
        return facePainter.faceRecognition();
    }
}
