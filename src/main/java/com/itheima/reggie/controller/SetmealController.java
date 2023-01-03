package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;



@PostMapping
@CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
log.info("套餐信息：{}",setmealDto);

setmealService.saveWithDish(setmealDto);
return R.success("新增套餐成功！");
    }


@GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
    Page<Setmeal> setmealPage = new Page<>(page,pageSize);
    Page<SetmealDto> setmealDtoPage = new Page<>();

    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name!=null,Setmeal::getName,name);
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);

    setmealService.page(setmealPage,queryWrapper);

    BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
    List<Setmeal> records = setmealPage.getRecords();



   List<SetmealDto> list= records.stream().map((item)->{
    SetmealDto setmealDto = new SetmealDto();
    BeanUtils.copyProperties(item,setmealDto);

    Long categoryId = item.getCategoryId();
    Category category = categoryService.getById(categoryId);
    if (category!=null) {
        String categoryName = category.getName();
        setmealDto.setCategoryName(categoryName);

    }
    return setmealDto;
}).collect(Collectors.toList());

   setmealDtoPage.setRecords(list);
    return R.success(setmealDtoPage);
    }




@DeleteMapping
@CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
       setmealService.removeWithDish(ids);

    return R.success("套餐数据删除成功！");
    }



    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list( Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
return R.success(list);
    }



}
