package com.example.demo.monitoring.aop;

import com.example.demo.monitoring.utils.AtomicCounter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;


/**
 * API访问历史统计
 *
 * @author 一个爱运动的程序员
 */
@Component
@Aspect
public class ApiVisitHistory {

    private Logger log = LoggerFactory.getLogger(ApiVisitHistory.class);

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 定义切面
     * - 此处代表com.example.demo.monitoring.controller包下的所有接口都会被统计
     */
//    @Pointcut("execution(* com.example.demo.monitoring.controller..*.*(..))")
    @Pointcut("@annotation(com.example.demo.monitoring.annotation.MonitoringAnnotation)")
    public void pointCut() {

    }

    /**
     * 在接口原有的方法执行前，将会首先执行此处的代码
     */
    @Before("pointCut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        //获取传入目标方法的参数
        log.info("类名：{}", joinPoint.getSignature().getDeclaringType().getSimpleName());
        log.info("方法名:{}", joinPoint.getSignature().getName());
    }

    /**
     * 只有正常返回才会执行此方法
     * 如果程序执行失败，则不执行此方法
     */
    @Async
    @AfterReturning(returning = "returnVal", pointcut = "pointCut()")
    public void doAfterReturning(JoinPoint joinPoint, Object returnVal) throws ExecutionException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String key = AtomicCounter.getTimeStamp() + "--" + AtomicCounter.getApiURL(request);
        // 成功计数+1
        AtomicCounter.increaseSucceed(AtomicCounter.getCountObject(key));
        // 耗时计算
        Long succeedTime = System.currentTimeMillis() - startTime.get();
        log.info("接口访问成功，URI:[{}], 耗费时间:[{}] ms", request.getRequestURI(), succeedTime);
        // 存储API接口访问信息
        AtomicCounter.setSuccessfulTime(key, succeedTime);
    }

    /**
     * 当接口报错时执行此方法
     */
    @AfterThrowing(pointcut = "pointCut()")
    public void doAfterThrowing(JoinPoint joinPoint) throws ExecutionException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String key = AtomicCounter.getTimeStamp() + "--" + AtomicCounter.getApiURL(request);
        // 失败计数+1
        AtomicCounter.increaseFail(AtomicCounter.getCountObject(key));
        // 耗时计算
        Long failTime = System.currentTimeMillis() - startTime.get();
        log.info("接口访问失败，URI:[{}], 耗费时间:[{}] ms", request.getRequestURI(), failTime);
        AtomicCounter.setMultiMap(key, failTime);
    }
}