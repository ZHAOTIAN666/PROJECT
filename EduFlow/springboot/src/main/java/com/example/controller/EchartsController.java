package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.entity.*;
import com.example.service.CourseService;
import com.example.service.EquipmentService;
import com.example.service.OrdersService;
import com.example.service.ReserveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Resource
    private EquipmentService equipmentService;
    @Resource
    private OrdersService ordersService;
    @Resource
    private CourseService courseService;
    @Resource
    private ReserveService reserveService;

    @GetMapping("/equipmentData")
    public Result equipmentData() {

        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        List<Equipment> all = equipmentService.selectAll(new Equipment());
        Map<String, Long> collect = all.stream().filter(x -> ObjectUtil.isNotEmpty(x.getStatus()))
                .collect(Collectors.groupingBy(Equipment::getStatus, Collectors.counting()));
        for (String key : collect.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", key);
            map.put("value", collect.get(key));
            list.add(map);
        }

        resultMap.put("text", "Equipment Data");
        resultMap.put("subtext", "Equipment Status");
        resultMap.put("name", "Number");
        resultMap.put("data", list);
        return Result.success(resultMap);
    }

    @GetMapping("/courserData")
    public Result courserData() {

        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        List<Orders> all = ordersService.selectAll(new Orders());
        for (Orders orders : all) {
            Course course = courseService.selectById(orders.getCourseId());
            if (ObjectUtil.isNotEmpty(course)) {
                orders.setCourseName(course.getName());
            }
        }
        Map<String, Double> collect = all.stream().filter(x -> ObjectUtil.isNotEmpty(x.getCourseName()))
                .collect(Collectors.groupingBy(Orders::getCourseName, Collectors.reducing(0.0, Orders::getPrice, Double::sum)));

        for (String key : collect.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", key);
            map.put("value", collect.get(key));
            list.add(map);
        }

        resultMap.put("text", "Fitness Course Sales Statistics");
        resultMap.put("subtext", "Statistics Dimension: Courses Sold");
        resultMap.put("name", "Amount");
        resultMap.put("data", list);
        return Result.success(resultMap);
    }

    @GetMapping("/coachData")
    public Result coachData() {

        Map<String, Object> resultMap = new HashMap<>();
        List<String> xAxis = new ArrayList<>();
        List<Long> yAxis = new ArrayList<>();

        List<Reserve> all = reserveService.selectAll(new Reserve());
        Map<String, Long> collect = all.stream().filter(x -> ObjectUtil.isNotEmpty(x.getCoachName()))
                .collect(Collectors.groupingBy(Reserve::getCoachName, Collectors.counting()));

        for (String key : collect.keySet()) {
            xAxis.add(key);
            yAxis.add(collect.get(key));
        }

        resultMap.put("text", "Coach Data");
        resultMap.put("subtext", "Coach Name");
        resultMap.put("xAxis", xAxis);
        resultMap.put("yAxis", yAxis);
        return Result.success(resultMap);
    }
}
