package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


/*
Mapper 方法中，使用了sql注解的是较为简单的sql语句，直接写在mapper层
复杂的sql语句，使用mapper.xml进行动态条件编写，并通过接口方法调用
对于同一个sql,两种方法只能使用一个！
 */
@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /*
     * 新增菜品
     * */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /*
     * 菜品分页查询
     * */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //根据主键查询菜品
    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

//    //根据主键删
//    @Delete("delete from dish where id=#{id}")
//    void deleteById(Long id);

    /*
     * 根据菜品id集合批量删除菜品
     * */
    void deleteByIds(List<Long> ids);

    //根据id动态修改菜品
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    //根据categoryId查询菜品
    @Select("select * from dish where category_id=#{categoryId}")
    List<Dish> getByCategoryId(String categoryId);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);

    /**
     * 动态条件查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);


    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

}


