package com.hfits.facerecognition.face.factory;

import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.ErrorInfo;
import com.hfits.facerecognition.face.enums.ErrorCodeEnum;
import com.hfits.facerecognition.face.exception.FaceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

@Slf4j
public class FaceEngineFactory extends BasePooledObjectFactory<FaceEngine> {

    private String libPath;
    private String appId;
    private String sdkKey;
    private String activeKey;
    private EngineConfiguration engineConfiguration;


    public FaceEngineFactory(String libPath, String appId, String sdkKey, String activeKey, EngineConfiguration engineConfiguration) {
        this.appId = appId;
        this.sdkKey = sdkKey;
        this.activeKey = activeKey;
        this.libPath = libPath;
        this.engineConfiguration = engineConfiguration;
    }


    @Override
    public FaceEngine create() {


        FaceEngine faceEngine = new FaceEngine(libPath);
        int activeCode = faceEngine.activeOnline(appId, sdkKey,activeKey);
        if (activeCode != ErrorInfo.MOK.getValue() && activeCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            log.error("引擎激活失败" + activeCode);
            throw new FaceException(ErrorCodeEnum.FAIL, "引擎激活失败" + activeCode);
        }
        int initCode = faceEngine.init(engineConfiguration);
        if (initCode != ErrorInfo.MOK.getValue()) {
            log.error("引擎初始化失败" + initCode);
            throw new FaceException(ErrorCodeEnum.FAIL, "引擎初始化失败" + initCode);
        }
        return faceEngine;
    }

    @Override
    public PooledObject<FaceEngine> wrap(FaceEngine faceEngine) {
        return new DefaultPooledObject<>(faceEngine);
    }


    @Override
    public void destroyObject(PooledObject<FaceEngine> p) throws Exception {
        FaceEngine faceEngine = p.getObject();
        int result = faceEngine.unInit();
        super.destroyObject(p);
    }
}
