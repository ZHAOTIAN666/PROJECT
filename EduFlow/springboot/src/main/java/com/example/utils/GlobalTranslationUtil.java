package com.example.common.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalTranslationUtil {

    private static final Map<String, String> TRANSLATION_MAP = new HashMap<>();

    static {
        // 状态翻译
        TRANSLATION_MAP.put("空闲中", "Idle");
        TRANSLATION_MAP.put("使用中", "In Use");
        TRANSLATION_MAP.put("维护中", "Under Maintenance");
        TRANSLATION_MAP.put("损坏", "Broken");
        TRANSLATION_MAP.put("正常", "Normal");
        TRANSLATION_MAP.put("异常", "Abnormal");

        // 设备类型翻译
        TRANSLATION_MAP.put("有氧设备", "Cardio Equipment");
        TRANSLATION_MAP.put("力量设备", "Strength Equipment");
        TRANSLATION_MAP.put("跑步机", "Treadmill");
        TRANSLATION_MAP.put("动感单车", "Spinning Bike");
        TRANSLATION_MAP.put("哑铃", "Dumbbell");
        TRANSLATION_MAP.put("杠铃", "Barbell");

        // 位置翻译
        TRANSLATION_MAP.put("A区", "Zone A");
        TRANSLATION_MAP.put("B区", "Zone B");
        TRANSLATION_MAP.put("C区", "Zone C");
        TRANSLATION_MAP.put("一楼", "1st Floor");
        TRANSLATION_MAP.put("二楼", "2nd Floor");
        TRANSLATION_MAP.put("三楼", "3rd Floor");
        TRANSLATION_MAP.put("健身房A区", "Gym Zone A");
        TRANSLATION_MAP.put("健身房B区", "Gym Zone B");
        TRANSLATION_MAP.put("A1", "Zone A1");
        TRANSLATION_MAP.put("A2", "Zone A2");
        TRANSLATION_MAP.put("B1", "Zone B1");
        TRANSLATION_MAP.put("B2", "Zone B2");

        // 会员状态翻译
        TRANSLATION_MAP.put("活跃", "Active");
        TRANSLATION_MAP.put("非活跃", "Inactive");
        TRANSLATION_MAP.put("暂停", "Suspended");
        TRANSLATION_MAP.put("过期", "Expired");

        // 课程类型翻译
        TRANSLATION_MAP.put("瑜伽", "Yoga");
        TRANSLATION_MAP.put("普拉提", "Pilates");
        TRANSLATION_MAP.put("有氧运动", "Aerobics");
        TRANSLATION_MAP.put("力量训练", "Strength Training");
        TRANSLATION_MAP.put("游泳", "Swimming");
        TRANSLATION_MAP.put("舞蹈", "Dance");

        // 时间相关翻译
        TRANSLATION_MAP.put("上午", "Morning");
        TRANSLATION_MAP.put("下午", "Afternoon");
        TRANSLATION_MAP.put("晚上", "Evening");
        TRANSLATION_MAP.put("周一", "Monday");
        TRANSLATION_MAP.put("周二", "Tuesday");
        TRANSLATION_MAP.put("周三", "Wednesday");
        TRANSLATION_MAP.put("周四", "Thursday");
        TRANSLATION_MAP.put("周五", "Friday");
        TRANSLATION_MAP.put("周六", "Saturday");
        TRANSLATION_MAP.put("周日", "Sunday");

        // 性别翻译
        TRANSLATION_MAP.put("男", "Male");
        TRANSLATION_MAP.put("女", "Female");

        // 通用词汇翻译
        TRANSLATION_MAP.put("是", "Yes");
        TRANSLATION_MAP.put("否", "No");
        TRANSLATION_MAP.put("开启", "Enabled");
        TRANSLATION_MAP.put("关闭", "Disabled");
        TRANSLATION_MAP.put("可用", "Available");
        TRANSLATION_MAP.put("不可用", "Unavailable");

        // 操作状态翻译
        TRANSLATION_MAP.put("已完成", "Completed");
        TRANSLATION_MAP.put("进行中", "In Progress");
        TRANSLATION_MAP.put("已取消", "Cancelled");
        TRANSLATION_MAP.put("待审核", "Pending Review");
        TRANSLATION_MAP.put("已审核", "Reviewed");

        // 支付状态翻译
        TRANSLATION_MAP.put("已支付", "Paid");
        TRANSLATION_MAP.put("未支付", "Unpaid");
        TRANSLATION_MAP.put("部分支付", "Partially Paid");
        TRANSLATION_MAP.put("退款", "Refunded");

        // 设备名称翻译
        TRANSLATION_MAP.put("Equipment1", "Equipment1");
        TRANSLATION_MAP.put("设备1", "Equipment1");
        TRANSLATION_MAP.put("器械1", "Equipment1");
    }

    /**
     * 翻译单个字符串
     */
    public static String translate(String chinese) {
        if (chinese == null || chinese.trim().isEmpty()) {
            return chinese;
        }
        return TRANSLATION_MAP.getOrDefault(chinese.trim(), chinese);
    }

    /**
     * 翻译对象中的所有字符串字段
     */
    public static <T> T translateObject(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            Class<?> clazz = obj.getClass();
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                if (field.getType() == String.class) {
                    String value = (String) field.get(obj);
                    if (value != null) {
                        field.set(obj, translate(value));
                    }
                }
            }
        } catch (Exception e) {
            // 静默处理异常，返回原对象
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * 翻译字符串列表
     */
    public static java.util.List<String> translateList(java.util.List<String> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        return list.stream()
                .map(GlobalTranslationUtil::translate)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 添加新的翻译映射
     */
    public static void addTranslation(String chinese, String english) {
        TRANSLATION_MAP.put(chinese, english);
    }

    /**
     * 批量添加翻译映射
     */
    public static void addTranslations(Map<String, String> translations) {
        TRANSLATION_MAP.putAll(translations);
    }
}