package com.hfits.facerecognition.face.core;

import cn.hutool.core.codec.Base64;
import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

/**
 * 人脸识别算法调用层
 * @author hanzhiguo
 * @date 2021/12/08
 */
public class FaceRecognition {
    static FaceEngine faceEngine = null;
    static int errorCode = 0;

    @Value("${arcsoft.appId}")
    public String appId;

    @Value("${arcsoft.sdkKey}")
    public String sdkKey;

    @Value("${arcsoft.activeKey}")
    public String activeKey;

    @Value("${arcsoft.libPath}")
    public String libPath;



    public FaceRecognition(){
        faceEngine = new FaceEngine(libPath);
        //激活引擎
        errorCode = faceEngine.activeOnline(appId, sdkKey,activeKey);

        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }
        ActiveFileInfo activeFileInfo=new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
//        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
//        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);


        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            System.out.println("初始化引擎失败");
        }
    }


    /**
     * 根据人脸图片转为的byte数组进行比较
     * @param faceByte
     * @return
     */
//    public FaceSimilar compare(byte[] faceByte){
//        //人脸检测
//        ImageInfo imageInfo = getRGBData(faceByte);
//        List<FaceInfo> faceInfoList = new ArrayList<>();
//        errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
////        System.out.println(faceInfoList);
//        // 表示摄像头没有拍到人脸啦
//        if(faceInfoList.isEmpty()) return null;
//
//        //特征提取
//        FaceFeature faceFeature = new FaceFeature();
//        errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);
////        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);
//
//        String path = "C:\\lfw";
//        File file = new File(path);
//        File[] fs = file.listFiles();
//        int length = 0;
//        for(File f:fs){
//            if(f.isDirectory()){
//                File[] subFiles = f.listFiles();
//                for (File subFile : subFiles) {
//                    if(!subFile.isDirectory()){
//                        ImageInfo imageInfo2 = getRGBData(subFile);
//                        List<FaceInfo> faceInfoList2 = new ArrayList<>();
//                        errorCode = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(),imageInfo.getImageFormat(), faceInfoList2);
////        System.out.println(faceInfoList);
//
//                        //特征提取2
//                        FaceFeature faceFeature2 = new FaceFeature();
//                        errorCode = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2.get(0), faceFeature2);
////        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);
//
//                        //特征比对
//                        FaceFeature targetFaceFeature = new FaceFeature();
//                        targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
//                        FaceFeature sourceFaceFeature = new FaceFeature();
//                        sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
//                        FaceSimilar faceSimilar = new FaceSimilar();
//                        errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
//                        System.out.println(subFile.getName()+"-----的识别率："+faceSimilar.getScore());
//                    }
//                }
//            }
//            if(length++ == 100) return null;
//        }




        //人脸检测2
        /*ImageInfo imageInfo2 = getRGBData(new File("c:\\face\\1.jpg"));
        List<FaceInfo> faceInfoList2 = new ArrayList<>();
        errorCode = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(),imageInfo.getImageFormat(), faceInfoList2);
//        System.out.println(faceInfoList);

        //特征提取2
        FaceFeature faceFeature2 = new FaceFeature();
        errorCode = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2.get(0), faceFeature2);
//        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);

        //特征比对
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
        FaceSimilar faceSimilar = new FaceSimilar();

        errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);*/

        /*//设置活体测试
        errorCode = faceEngine.setLivenessParam(0.5f, 0.7f);
        //人脸属性检测
        FunctionConfiguration configuration = new FunctionConfiguration();
        configuration.setSupportAge(true);
        configuration.setSupportFace3dAngle(true);
        configuration.setSupportGender(true);
        configuration.setSupportLiveness(true);
        errorCode = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList, configuration);


        //性别检测
        List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
        errorCode = faceEngine.getGender(genderInfoList);
        System.out.println("性别：" + genderInfoList.get(0).getGender());*/

//        return null;
//    }
}
