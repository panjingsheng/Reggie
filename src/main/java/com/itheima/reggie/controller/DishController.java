package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
@Autowired
    private DishService dishService;

@Autowired
    private DishFlavorService dishFlavorService;
@Autowired
private CategoryService categoryService;

@PostMapping
public R<String> save(@RequestBody DishDto dishDto){
log.info(dishDto.toString());
dishService.saveWithFlavor(dishDto);

    return R.success("新增菜品成功！");
}


@GetMapping("/page")
public R<Page> page(int page,int pageSize,String name){
    Page<Dish> pageInfo = new Page<>(page,pageSize);
    Page<DishDto> dishDtopage = new Page<>();

    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
   queryWrapper.like(name!=null,Dish::getName,name);
   queryWrapper.orderByDesc(Dish::getUpdateTime);

   dishService.page(pageInfo,queryWrapper);

    BeanUtils.copyProperties(pageInfo,dishDtopage,"records");

    List<Dish> records = pageInfo.getRecords();
    List<DishDto> list= records.stream().map((item)->{
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(item,dishDto);
        Long categoryId = item.getCategoryId();
        Category category = categoryService.getById(categoryId);

        if (category!=null) {
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
        }

        return dishDto;
    }).collect(Collectors.toList());



    dishDtopage.setRecords(list);
    return R.success(dishDtopage);
}



@GetMapping("/{id}")
public R<DishDto> get(@PathVariable Long id){
    DishDto dishDto = dishService.getByIdWithFlavor(id);

    return R.success(dishDto);
}



    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功！");
    }


    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);


        return R.success(list);
    }


}
