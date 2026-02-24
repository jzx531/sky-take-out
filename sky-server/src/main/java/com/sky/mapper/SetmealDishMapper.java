package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 判断当前菜品类是否被套餐关联了
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 删除套餐和菜品的关联关系
     * @param setmealId
     */
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐信息查询菜品信息
     * @param setmealId
     * @return
     */
    List<SetmealDish> getBySetmealId(Long setmealId);
}
