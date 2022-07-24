package com.example.demo.monitoring.vo;

import lombok.Data;

/**
 * @author 一个爱运动的程序员
 */
@Data
public class MonitoringVO {

    /**
     * 请求时间
     */
    private String time;

    /**
     * 接口请求的URL
     */
    private String apiURL;

    /**
     * 接口请求成功的次数
     */
    private Long successfulNum;

    /**
     * 接口请求失败的次数
     */
    private Long failuresNum;

    /**
     * 接口请求成功的累计耗时
     */
    private Long successfulTime;

    /**
     * 接口请求失败的累计耗时
     */
    private Long failuresTime;
}
