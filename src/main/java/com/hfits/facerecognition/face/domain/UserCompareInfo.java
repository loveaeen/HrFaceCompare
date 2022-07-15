package com.hfits.facerecognition.face.domain;


import com.hfits.facerecognition.face.utils.UserRamCache;
import lombok.Data;

/**
 * @author st7251
 * @date 2020/3/11 14:02
 */
@Data
public class UserCompareInfo extends UserRamCache.UserInfo {
    private Float similar;
}
