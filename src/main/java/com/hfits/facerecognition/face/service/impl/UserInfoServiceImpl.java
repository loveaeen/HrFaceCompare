package com.hfits.facerecognition.face.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfits.facerecognition.face.mapper.UserInfoMapper;
import com.hfits.facerecognition.face.service.UserInfoService;
import com.hfits.facerecognition.face.utils.UserRamCache;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserRamCache.UserInfo> implements UserInfoService {
}
