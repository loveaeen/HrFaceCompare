package com.hfits.facerecognition.face;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ExtractType;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        FaceEngine faceEngine = new FaceEngine("C:\\face\\libs");
        EngineConfiguration detectCfg = new EngineConfiguration();
        FunctionConfiguration detectFunctionCfg = new FunctionConfiguration();
        detectFunctionCfg.setSupportFaceDetect(true);//开启人脸检测功能
        detectFunctionCfg.setSupportFaceRecognition(true);//开启人脸识别功能
        detectFunctionCfg.setSupportAge(true);//开启年龄检测功能
        detectFunctionCfg.setSupportGender(true);//开启性别检测功能
        detectFunctionCfg.setSupportLiveness(true);//开启活体检测功能
        detectCfg.setFunctionConfiguration(detectFunctionCfg);
        detectCfg.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);//图片检测模式，如果是连续帧的视频流图片，那么改成VIDEO模式
        detectCfg.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);//人脸旋转角度
        int initCode = faceEngine.init(detectCfg);
        System.out.println("引擎初始化返回"+initCode);

        ImageInfo imageInfo1 = ImageFactory.getRGBData(new File("C:\\face\\1.jpg"));

        ImageInfo imageInfo2 = ImageFactory.getRGBData(new File("C:\\face\\2.jpg"));

        List<FaceInfo> faceInfoList1 = new ArrayList<FaceInfo>();
        List<FaceInfo> faceInfoList2 = new ArrayList<FaceInfo>();
        // 获取人脸
        faceEngine.detectFaces(imageInfo1.getImageData(), imageInfo1.getWidth(), imageInfo1.getHeight(), imageInfo1.getImageFormat(), faceInfoList1);
        faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo2.getImageFormat(), faceInfoList2);
        // 获取特征
        FaceFeature faceFeature1 = new FaceFeature();
        FaceFeature faceFeature2 = new FaceFeature();
        //提取人脸特征
        faceEngine.extractFaceFeature(imageInfo1, faceInfoList1.get(0), ExtractType.REGISTER, 0, faceFeature1);
        faceEngine.extractFaceFeature(imageInfo2, faceInfoList2.get(0), ExtractType.REGISTER, 0, faceFeature2);

        // 开始比对
        FaceFeature faceFeature12 = new FaceFeature();
        faceFeature1.setFeatureData(faceFeature1.getFeatureData());
        FaceFeature faceFeature22 = new FaceFeature();
        FaceSimilar faceSimilar = new FaceSimilar();
        faceFeature2.setFeatureData(faceFeature2.getFeatureData());
        faceEngine.compareFaceFeature(faceFeature12, faceFeature22, faceSimilar);
        System.out.println("特征值比对结果为"+faceSimilar.getScore());
    }
}
