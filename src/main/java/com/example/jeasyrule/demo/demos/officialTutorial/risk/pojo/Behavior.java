package com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo;

import java.util.EnumSet;
import java.util.Set;

public enum Behavior {
    LOGIN_OVERSEAS,            // 海外登录
    LOGIN_DEVICE_CHANGE,       // 更换终端
    TRANSFER_MANY_SMALL,       // 频繁小额转出
    TRANSFER_LARGE_NIGHT,      // 深夜大额
    MULTI_FAILED_LOGIN         // 多次失败登录
}

