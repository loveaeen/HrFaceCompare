package com.hfits.facerecognition.face.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfits.facerecognition.face.utils.UserRamCache;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserRamCache.UserInfo> {
}
