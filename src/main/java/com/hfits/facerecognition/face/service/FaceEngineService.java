package com.hfits.facerecognition.face.service;


import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.toolkit.ImageInfo;
import com.hfits.facerecognition.face.domain.ProcessInfo;
import com.hfits.facerecognition.face.domain.UserCompareInfo;
import com.hfits.facerecognition.face.utils.UserRamCache;

import java.util.List;


public interface FaceEngineService {

    /**
     * 获取该图片的人脸信息
     * @param imageInfo
     * @return
     */
    List<FaceInfo> detectFaces(ImageInfo imageInfo);

    /**
     * 对比两张图片的相似度
     * @param imageInfo1
     * @param imageInfo2
     * @return
     */
    FaceSimilar compareFace(ImageInfo imageInfo1, ImageInfo imageInfo2) ;

    /**
     * 提取人脸特征值
     * @param imageInfo
     * @param faceInfo
     * @return
     */
    byte[] extractFaceFeature(ImageInfo imageInfo,FaceInfo faceInfo);

    /**
     * 一组图片与单张图片进行人脸识别
     * @param faceFeature
     * @param userInfoList
     * @param passRate
     * @return 识别率排序的集合
     */
    List<UserCompareInfo> faceRecognition(byte[] faceFeature, List<UserRamCache.UserInfo> userInfoList, float passRate) ;

    /**
     * 返回图片的活体信息
     * @param imageInfo
     * @param faceInfoList
     * @return
     */
    List<ProcessInfo> process(ImageInfo imageInfo, List<FaceInfo> faceInfoList);
    
    /**
     * 返回图片的IR活体信息
     * @param imageInfo
     * @param faceInfoList
     * @return
     */
    List<ProcessInfo> processIr(ImageInfo imageInfo, List<FaceInfo> faceInfoList);





}
