//package com.example.demo;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@SpringBootTest
//class DemoApplicationTests {
//
//	@Test
//	void contextLoads() {
//	}
//	@Test
//	public void testPlatformEnum() {
//		List<WmsTruckRateEntity> resultList1 = com.google.common.collect.Lists.newArrayList();
//		List<WmsTruckRateEntity> resultList2 = Collections.synchronizedList(new ArrayList<>());
////        CopyOnWriteArrayList<WmsTruckRateEntity> resultList2 = new CopyOnWriteArrayList<>();
//		List<WmsTruckRateEntity> resultList3 = com.google.common.collect.Lists.newArrayList();
//
//		// 多个问价处理，中途不中断，但记录异常xinxi
//		List<String> errorList = Collections.synchronizedList(new ArrayList<>());
//
//		Long start = System.currentTimeMillis();
//		// 逐个平台遍历问价
//		List<PlatformEnum> plts = com.google.common.collect.Lists.newArrayList(PlatformEnum.WWEX, PlatformEnum.RAPIDDEALS,
//				PlatformEnum.RAPIDDEALS2, PlatformEnum.GIGACLOUD);
//		for (PlatformEnum pe : plts) {
//			try {
//				List<WmsTruckRateEntity> rating = TruckBookingServiceFactory.getService(pe).rating(order);
////                System.out.println("串行--->" + pe + "--->" + rating);
//				resultList1.addAll(rating);
//			} catch (Exception e) {
//				errorList.add(String.format(pe.name() + ": %s", e.getMessage()));
//			}
//		}
//		Long end = System.currentTimeMillis();
//		System.out.println("串行流消耗时间:" + (end - start));
//		System.out.println("串行流数据：" + resultList1);
//
//		// 通过并行流逐个平台遍历
//		Long startStream = System.currentTimeMillis();
//		Stream<PlatformEnum> enumStream = Stream.of(PlatformEnum.RAPIDDEALS, PlatformEnum.RAPIDDEALS2,
//				PlatformEnum.WWEX, PlatformEnum.GIGACLOUD).parallel();
//		enumStream.forEach(pe -> {
//			try {
//				List<WmsTruckRateEntity> rating = TruckBookingServiceFactory.getService(pe).rating(order);
////                System.out.println("并行--->" + pe + "--->" + rating);
//				resultList2.addAll(rating);
//			} catch (IllegalAccessException e) {
//				errorList.add(String.format(pe.name() + ": %s", e.getMessage()));
//			}
//		});
//		Long endStream = System.currentTimeMillis();
//		System.out.println("并行流消耗时间:" + (endStream - startStream));
//		System.out.println("并行流数据：" + resultList2);
//
//		// Fork/Join框架实现
////        Long startFJ = System.currentTimeMillis();
////        ForkJoinPool pool = new ForkJoinPool();
////        List<PlatformEnum> list = com.google.common.collect.Lists.newArrayList(PlatformEnum.RAPIDDEALS, PlatformEnum.RAPIDDEALS2,
////                PlatformEnum.WWEX, PlatformEnum.GIGACLOUD);
////        RateRecursiveTask task = new RateRecursiveTask(list, order, 0, list.size());
////        resultList3 = pool.invoke(task);
////        Long endFJ = System.currentTimeMillis();
////        System.out.println("Fork/Join框架消耗时间:" + (endFJ - startFJ));
////        System.out.println("Fork/Join框架数据：" + resultList3);
//	}
//
//	@Test
//	public void testAOP() {
//		Long timeStamp = System.currentTimeMillis();  //获取当前时间戳
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
//		SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
//		String sd = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));      // 时间戳转换成时间
//		String sdMinute = sdfMinute.format(new Date(Long.parseLong(String.valueOf(timeStamp))));      // 时间戳转换成时间
//		sd += ":" + (Integer.valueOf(sdMinute) - Integer.valueOf(sdMinute) % 5);
//		System.out.println("格式化结果：" + sd);
//	}
//}
//
//class RateRecursiveTask extends RecursiveTask<List<WmsTruckRateEntity>> {
//	private final List<PlatformEnum> plts;
//	private final TruckOrder order;
//	//起始值
//	private final int start;
//	//结束值
//	private final int end;
//
//	public RateRecursiveTask(List<PlatformEnum> plts, TruckOrder order, int start, int end) {
//		this.plts = plts;
//		this.order = order;
//		this.start = start;
//		this.end = end;
//	}
//
//	@Override
//	protected List<WmsTruckRateEntity> compute() {
//		int length = end - start;
//		//计算
//		if (length == 0) {
//			try {
//				List<WmsTruckRateEntity> list = TruckBookingServiceFactory.getService(plts.get(end)).rating(order);
//				return list;
//			} catch (IllegalAccessException e) {
//				throw new RuntimeException(e);
//			}
//		} else {
//			// 拆分
//			int middle = (start + end) / 2;
//			RateRecursiveTask left = new RateRecursiveTask(plts, order, start, middle);
//			RateRecursiveTask right = new RateRecursiveTask(plts, order, middle + 1, end);
//			this.invokeAll(left, right);
//			List<WmsTruckRateEntity> ljoin = left.join();
//			List<WmsTruckRateEntity> rjoin = right.join();
//			return Stream.of(ljoin, rjoin).flatMap(x -> x.stream()).collect(Collectors.toList());
//		}
//	}
//}