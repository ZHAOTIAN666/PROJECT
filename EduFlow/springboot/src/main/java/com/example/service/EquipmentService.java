package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Equipment;
import com.example.mapper.EquipmentMapper;
import com.example.utils.TokenUtils;
import com.example.common.utils.GlobalTranslationUtil;  // 添加这个导入
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 健身器材表业务处理
 **/
@Service
public class EquipmentService {

    @Resource
    private EquipmentMapper equipmentMapper;

    /**
     * 新增
     */
    public void add(Equipment equipment) {
        equipmentMapper.insert(equipment);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        equipmentMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            equipmentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Equipment equipment) {
        equipmentMapper.updateById(equipment);
    }

    /**
     * 根据ID查询
     */
    public Equipment selectById(Integer id) {
        Equipment equipment = equipmentMapper.selectById(id);

        // 添加翻译
        if (equipment != null) {
            GlobalTranslationUtil.translateObject(equipment);
        }

        return equipment;
    }

    /**
     * 查询所有
     */
    public List<Equipment> selectAll(Equipment equipment) {
        List<Equipment> list = equipmentMapper.selectAll(equipment);

        // 添加翻译 - 翻译列表中的每个对象
        list.forEach(item -> {
            GlobalTranslationUtil.translateObject(item);
        });

        return list;
    }

    /**
     * 分页查询
     */
    public PageInfo<Equipment> selectPage(Equipment equipment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Equipment> list = equipmentMapper.selectAll(equipment);

        // 添加翻译 - 翻译列表中的每个对象
        list.forEach(item -> {
            GlobalTranslationUtil.translateObject(item);
        });

        return PageInfo.of(list);
    }
}