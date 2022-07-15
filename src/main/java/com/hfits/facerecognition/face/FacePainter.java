package com.hfits.facerecognition.face;

import cn.hutool.log.Log;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import com.hfits.facerecognition.face.domain.ProcessInfo;
import com.hfits.facerecognition.face.domain.UserCompareInfo;
import com.hfits.facerecognition.face.enums.ErrorCodeEnum;
import com.hfits.facerecognition.face.exception.FaceException;
import com.hfits.facerecognition.face.service.FaceEngineService;
import com.hfits.facerecognition.face.service.UserInfoService;
import com.hfits.facerecognition.face.utils.ThreadSearchUtil;
import com.hfits.facerecognition.face.utils.UserRamCache;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * 网络摄像头具体调用层及人脸识别组件集成
 * @author hanzhiguo
 * @date 2021/12/08
 */
@Component
public class FacePainter implements WebcamPanel.Painter {

    static Log log = Log.get(FacePainter.class);

    @Autowired
    private FaceEngineService faceEngineService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ThreadSearchUtil threadSearchUtil;

    /**
     * 单独的线程用于画人脸识别红框
     */
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    /**
     * 用于启动定时比对人脸线程
     */
    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    /**
     * 用于定时比对人脸线程的Future对象，作为判断是否比对结束的用处
     */
    private static ScheduledFuture<?> scheduledFuture = null;
    /**
     * 人脸识别组件
     */
    private static final HaarCascadeDetector detector = new HaarCascadeDetector();
    /**
     * 人脸识别红色框
     */
    private static final Stroke STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);

    /**
     * 人脸识别返回结果
     */
    private static String returnStr = "OK";

    private static CopyOnWriteArrayList<UserRamCache.UserInfo> userInfoList = new CopyOnWriteArrayList<>();

    /**
     * Webcam网络摄像默认RGB组件
     */
    private Webcam webcam = null;
    private WebcamPanel.Painter painter = null;
    private List<DetectedFace> faces = null;
    private WebcamPanel panel = null;
    private JFrame frame = null;

    /**
     * Webcam网络摄像IR组件
     */
    private Webcam iRWebcam = null;

    @PostConstruct
    public void init(){
        System.out.println("开始查询特征值");
        long start = System.currentTimeMillis();
        int count = userInfoService.count();
        long mid = System.currentTimeMillis();
        System.out.println("查询总数量花费了："+(mid-start)+" 毫秒");
        threadSearchUtil.searchAsync(userInfoList,count);
        System.out.println(userInfoList.size());
        long end = System.currentTimeMillis();
        System.out.println("查询特征值用时："+(end-mid)+" 毫秒");
//        String path = "C:\\face\\1.jpg";
//        File subFile = new File(path);
//        UserRamCache.UserInfo userInfo = new UserRamCache.UserInfo();
//        userInfo.setFaceId(subFile.getName());
//        userInfo.setName(subFile.getName());
//        ImageInfo rgbData = ImageFactory.getRGBData(subFile);
//        List<FaceInfo> faceInfos = faceEngineService.detectFaces(rgbData);
//        userInfo.setFaceFeature(faceEngineService.extractFaceFeature(rgbData,faceInfos.get(0)));
//        userInfoService.save(userInfo);
//        int length = 0;
//        for(File f:fs){
//            if(f.isDirectory()){
//                File[] subFiles = f.listFiles();
//                for (File subFile : subFiles) {
//                    if(!subFile.isDirectory()){
//                        UserRamCache.UserInfo userInfo = new UserRamCache.UserInfo();
//                        userInfo.setFaceId(subFile.getName());
//                        userInfo.setName(subFile.getName());
//                        ImageInfo rgbData = ImageFactory.getRGBData(subFile);
//                        List<FaceInfo> faceInfos = faceEngineService.detectFaces(rgbData);
//                        userInfo.setFaceFeature(faceEngineService.extractFaceFeature(rgbData,faceInfos.get(0)));
//                        userInfoService.save(userInfo);
//
//
////                        UserRamCache.UserInfo userInfo = new UserRamCache.UserInfo();
////                        userInfo.setFaceId(subFile.getName());
////                        userInfo.setName(subFile.getName());
////                        long start = System.currentTimeMillis();
////                        ImageInfo rgbData = ImageFactory.getRGBData(subFile);
////                        List<FaceInfo> faceInfos = faceEngineService.detectFaces(rgbData);
////                        userInfo.setFaceFeature(faceEngineService.extractFaceFeature(rgbData,faceInfos.get(0)));
////                        long end = System.currentTimeMillis();
////                        System.out.println("获取人脸特征值所花费的时间为："+(end-start));
////                        userInfoList.add(userInfo);
//                    }
//                }
//            }
//            if(length++ == 100) return;
//        }
    }

    @Override
    public void paintPanel(WebcamPanel panel, Graphics2D g2) {
        if (painter != null) {
            painter.paintPanel(panel, g2);
        }
    }

    @Override
    public void paintImage(WebcamPanel panel, BufferedImage image, Graphics2D g2) {

        if (painter != null) {
            painter.paintImage(panel, image, g2);
        }

        if (faces == null) {
            return;
        }

        Iterator<DetectedFace> dfi = faces.iterator();
        while (dfi.hasNext()) {

            DetectedFace face = dfi.next();
            Rectangle bounds = face.getBounds();

            int dx = (int) (0.1 * bounds.width);
            int dy = (int) (0.2 * bounds.height);
            int x = (int) bounds.x - dx;
            int y = (int) bounds.y - dy;
            int w = (int) bounds.width + 2 * dx;
            int h = (int) bounds.height + dy;

            g2.drawImage(null, x, y, w, h, null);
            g2.setStroke(STROKE);
            g2.setColor(Color.RED);
            g2.drawRect(x, y, w, h);
        }
    }

    /**
     * 人脸识别组件初始化
     */
    public void faceInit(){
        // 查询当前计算机是否存在视频驱动
        List<Webcam> webcams = Webcam.getWebcams();
        if(webcams.isEmpty()){
            log.error("当前计算机未找到视频驱动");
            return;
        }
        // 如果有多驱动的话，让后台摄像启动，采取双拍摄模式
        if(webcams.size() > 1){
            iRWebcam = webcams.get(1);
        }
        // 初始化一些摄像头组件
        frame = new JFrame();
        webcam = webcams.get(0);
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open(true);

        // 初始化摄像区域内容，比如FPS显示，分辨率显示等
        panel = new WebcamPanel(webcam,false);
        panel.setPreferredSize(WebcamResolution.VGA.getSize());
        panel.setPainter(this);
        panel.setFPSDisplayed(true);
        panel.setFPSLimited(true);
        panel.setFPSLimit(20);
        panel.start();

        // 画图组件，让人脸红色识别框展示出来
        painter = panel.getDefaultPainter();

        // 全部集成在一起
        frame.add(panel);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));
        frame.setTitle("Face Detector Example");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 开启人脸识别
     * @return
     * @throws InterruptedException
     */
    public String faceRecognition() throws InterruptedException{
        // 人脸组件及摄像组件初始化
        faceInit();
        // 画框子
        drawFace();
        // 定时器赋值Future
        setScheduledFuture();

        // 查询定时器是否结束/取消，然后返回结果。
        while (!scheduledFuture.isDone() && !scheduledFuture.isCancelled()){
            TimeUnit.MILLISECONDS.sleep(500);
        }
        return returnStr;
    }

    /**
     * 画人脸识别的红框
     */
    public void drawFace(){
        EXECUTOR.execute(()->{
            while (true) {
                if (!webcam.isOpen()) {
                    return;
                }
                // 这是人脸在摄像区域的位置，需要动态获取，所以要用while
                faces = detector.detectFaces(ImageUtilities.createFImage(webcam.getImage()));
            }
        });
    }

    /**
     * 启动定时任务
     */
    public void setScheduledFuture (){
        scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            // 判断摄像组件是否显示，没有显示则表明被用户点了关闭，则将所有组件停止。
            if (!frame.isShowing()) {
                returnStr = "被人为关闭了";
                stopWebCam();
                return;
            }
            try{
                // 判断是否活体
                ImageInfo rgbData = ImageFactory.getRGBData(WebcamUtils.getImageBytes(webcam, "jpg"));
                List<FaceInfo> faceInfos = faceEngineService.detectFaces(rgbData);
                if(faceInfos.isEmpty()){
                    throw new FaceException(ErrorCodeEnum.FACE_NONE);
                }
                List<ProcessInfo> process = faceEngineService.process(rgbData, faceInfos);
                if(!process.isEmpty()){
                    log.info("活体信息为："+process.get(0).getLiveness());
                }else{
                    throw new FaceException(ErrorCodeEnum.FACE_NONE);
                }
                // 判断是否IR活体
                if(iRWebcam != null){
                    ImageInfo irData = ImageFactory.getRGBData(WebcamUtils.getImageBytes(iRWebcam, "jpg"));
                    List<FaceInfo> irFaceInfos = faceEngineService.detectFaces(irData);
                    if(faceInfos.isEmpty()){
                        throw new FaceException(ErrorCodeEnum.FACE_NONE);
                    }
                    List<ProcessInfo> processIr = faceEngineService.processIr(rgbData, faceInfos);
                    if(!processIr.isEmpty()){
                        log.info("活体信息为："+processIr.get(0).getLiveness());
                    }else{
                        throw new FaceException(ErrorCodeEnum.FACE_NONE);
                    }
                }
                
                // 从数据库查询所有数据并比较相似度
                List<UserCompareInfo> userCompareInfos = faceEngineService.faceRecognition(faceEngineService.extractFaceFeature(rgbData,faceInfos.get(0)), userInfoList, 0.4f);
                if(userCompareInfos.isEmpty()){
                    log.info("没有相似的人脸！");
                }else{
                    log.info("匹配相似的照片有："+userCompareInfos.size()+"张，其中最相似的照片名是："+userCompareInfos.get(0).getName()+"，相似度是："+userCompareInfos.get(0).getSimilar());
                }
            }catch(Exception e){
                log.error(e);
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭摄像头组件
     */
    public void stopWebCam(){
        webcam.close();
        panel.stop();
        frame.setVisible(false);
        scheduledFuture.cancel(true);
    }

}
