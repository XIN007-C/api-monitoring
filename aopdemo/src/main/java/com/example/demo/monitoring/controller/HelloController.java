package com.example.demo.monitoring.controller;

import com.example.demo.monitoring.annotation.MonitoringAnnotation;
import com.example.demo.monitoring.utils.AtomicCounter;
import com.example.demo.monitoring.vo.MonitoringVO;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/aop")
public class HelloController {

    static int r = 0;

    @GetMapping("success")
    public String success() {
        return "调用成功";
    }

    @GetMapping("fail")
    @MonitoringAnnotation
    public String fail() {
        if (r == 3) {
            r++;
            return "调用错误接口，成功一次";
        } else {
            r++;
            int i = 1 / 0;
            return "测试报错的AOP方法";
        }
    }

    @GetMapping("cache")
    public String get() {
        CopyOnWriteArrayList<MonitoringVO> list = AtomicCounter.getCacheMap();
        return JSONObject.valueToString(list);
    }
}