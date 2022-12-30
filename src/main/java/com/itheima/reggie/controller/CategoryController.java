package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
public R<String> save(@RequestBody Category category){
    log.info("{}",category);
categoryService.save(category);
return R.success("新增分类成功！");


}


@GetMapping("/page")
public R<Page> page(int page,int pageSize){
    Page<Category> pageInfo = new Page<>(page,pageSize);
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.orderByAsc(Category::getSort);
categoryService.page(pageInfo,queryWrapper);
return R.success(pageInfo);

}



@DeleteMapping
public R<String> delete(Long ids){
log.info("删除id为{}",ids);
//categoryService.removeById(id);

    categoryService.remove(ids);
return R.success("分类信息删除成功！");
}


@PutMapping
public R<String> update(@RequestBody Category category){
        log.info("修改信息");
        categoryService.updateById(category);
        return R.success("修改分类信息成功！");
}


@GetMapping("/list")
public R<List<Category>> list(Category category){
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
    List<Category> list = categoryService.list(queryWrapper);
    return R.success(list);
}





}
