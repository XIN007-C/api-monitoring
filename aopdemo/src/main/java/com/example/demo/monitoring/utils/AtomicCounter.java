package com.example.demo.monitoring.utils;

import com.example.demo.monitoring.vo.MonitoringVO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 记录API接口访问成功/失败的计数及耗时
 *
 * @author 一个爱运动的程序员
 */
public class AtomicCounter {

    /**
     * Guava Cache 缓存API访问记录
     */
    private static Cache<String, ConcurrentHashMap<AtomicCounter, MonitoringVO>> cache = CacheBuilder.newBuilder().expireAfterWrite(60 * 10, TimeUnit.SECONDS).build();

    /**
	 * 调用的计数对象
	 *
	 * @param key
	 * @return
	 */
	public static ConcurrentHashMap<AtomicCounter, MonitoringVO> getCountObject(String key) {
        ConcurrentHashMap<AtomicCounter, MonitoringVO> ifPresent = cache.getIfPresent(key);
        if (ifPresent == null || ifPresent.isEmpty()) {
            ifPresent = new ConcurrentHashMap<>();
            AtomicCounter atomicCounter = new AtomicCounter();
            MonitoringVO monitoringVO = new MonitoringVO();
            String[] split = key.split("--");
            monitoringVO.setTime(split[0]);
            monitoringVO.setApiURL(split[1]);
            monitoringVO.setSuccessfulNum(0L);
            monitoringVO.setFailuresNum(0L);
            monitoringVO.setSuccessfulTime(0L);
            monitoringVO.setFailuresTime(0L);
            ifPresent.put(atomicCounter, monitoringVO);
            cache.put(key, ifPresent);
		}
		return ifPresent;
	}

    /**
     * 增加访问接口调用成功的次数
     * @param concurrentHashMap
     * @return
     */
    public static void increaseSucceed(ConcurrentHashMap<AtomicCounter, MonitoringVO> concurrentHashMap) {
        MonitoringVO monitoringVO = null;
        for (MonitoringVO m : concurrentHashMap.values()) monitoringVO = m;
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            monitoringVO.setSuccessfulNum(monitoringVO.getSuccessfulNum() + 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 更新调用成功的耗时
     * @param key
     * @param successTime
     * @throws ExecutionException
     */
    public static void setSuccessfulTime(String key, Long successTime) {
        ConcurrentHashMap<AtomicCounter, MonitoringVO> map = getCountObject(key);
        MonitoringVO monitoringVO = null;
        for (MonitoringVO m : map.values()) monitoringVO = m;
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            monitoringVO.setSuccessfulTime(monitoringVO.getSuccessfulTime() + successTime);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 增加访问接口调用失败的次数
     * @param concurrentHashMap
     * @return
     */
	public static void increaseFail(ConcurrentHashMap<AtomicCounter, MonitoringVO> concurrentHashMap) {
        MonitoringVO monitoringVO = null;
        for (MonitoringVO m : concurrentHashMap.values()) monitoringVO = m;
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            monitoringVO.setFailuresNum(monitoringVO.getFailuresNum() + 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
	}

    /**
     *更新调用失败的耗时
     * @param key
     * @param failTime
     */
    public static void setMultiMap(String key, Long failTime) {
        ConcurrentHashMap<AtomicCounter, MonitoringVO> map = getCountObject(key);
        MonitoringVO monitoringVO = null;
        for (MonitoringVO m : map.values()) monitoringVO = m;
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            monitoringVO.setFailuresTime(monitoringVO.getFailuresTime() + failTime);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取时间戳，并以5分钟为节点
     * @return
     */
    public static String getTimeStamp() {
        // 获取时间戳
        Long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
        // 时间戳转换成时间
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
        // 时间戳转换成时间
        String sdMinute = sdfMinute.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
        sd += ":" + (Integer.valueOf(sdMinute) - Integer.valueOf(sdMinute) % 5);
        return sd;
    }

    /**
     * 获取API的接口URL
     * @param request
     * @return
     */
    public static String getApiURL(HttpServletRequest request) {
        // 请求路径
        String requestURI = request.getRequestURI();
        return requestURI;
    }

    /**
	 * 获取API监控信息
	 *
	 * @return
	 */
	public static CopyOnWriteArrayList<MonitoringVO> getCacheMap() {
        ConcurrentMap<String, ConcurrentHashMap<AtomicCounter, MonitoringVO>> stringConcurrentHashMapConcurrentMap = cache.asMap();
        CopyOnWriteArrayList<MonitoringVO> list = new CopyOnWriteArrayList<>();
		for (ConcurrentHashMap<AtomicCounter, MonitoringVO> c : stringConcurrentHashMapConcurrentMap.values())
			for (MonitoringVO m : c.values()) list.add(m);
		return list;
	}
}