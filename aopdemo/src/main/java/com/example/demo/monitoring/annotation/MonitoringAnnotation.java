package com.example.demo.monitoring.annotation;

import java.lang.annotation.*;

/**
 * 自定义接口监控注解
 *
 * @author 一个爱运动的程序员
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MonitoringAnnotation {
}
