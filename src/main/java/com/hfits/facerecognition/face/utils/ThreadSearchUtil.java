package com.hfits.facerecognition.face.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfits.facerecognition.face.service.UserInfoService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程查询特征值数据的工具类
 * @author hanzhiguo
 */
@Component
public class ThreadSearchUtil {

    private static UserInfoService userInfoService;

    @Autowired
    public void setUserInfoService(UserInfoService userInfoService){
        ThreadSearchUtil.userInfoService = userInfoService;
    }

    /**
     * 异步多线程用于快速查询特征值数据
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static final int PAGE_SIZE = 500;

    /**
     * 查询特征值信息
     * @param userInfoList 需要填充的集合
     * @param count 表数据行数
     * @return
     */
    public CopyOnWriteArrayList<UserRamCache.UserInfo> searchAsync(CopyOnWriteArrayList<UserRamCache.UserInfo> userInfoList, int count){
        int len = count%PAGE_SIZE == 0?count/PAGE_SIZE:(count/PAGE_SIZE)+1;
        CountDownLatch latch = new CountDownLatch(len);
        for (int i = 1; i < len+1; i++) {
            Page<UserRamCache.UserInfo> page = new Page(i,PAGE_SIZE,false);
            executorService.execute(() -> {
                List<UserRamCache.UserInfo> records = userInfoService.page(page).getRecords();
                userInfoList.addAll(records);
                latch.countDown();
            });
        }
        try {
            latch.await();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return userInfoList;
    }
}
