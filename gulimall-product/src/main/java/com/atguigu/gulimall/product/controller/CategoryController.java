package com.atguigu.gulimall.product.controller;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 商品三级分类
 *
 * @author yaojunyi
 * @email yao_junyi@qq.com
 * @date 2021-04-30 11:20:32
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有
     * 分类以及子类，以树形结构组装
     */
    @RequestMapping("/list/tree")
    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    public R list() {
        List<CategoryEntity> entities = categoryService.listWithTree();
        // 筛选出所有一级分类
        List<CategoryEntity> level1Menus = entities.stream().
                filter((categoryEntity) -> categoryEntity.getParentCid() == 0)
                .map((menu) -> {
                    menu.setChildren(getChildren(menu, entities));
                    return menu;
                }).sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());
        return R.ok().put("data", level1Menus);
    }

    @RequestMapping("/list/updateTree")
    @CacheEvict(value = {"category"},allEntries = true)
    public R updateList() {
        return R.ok().put("data", "ok");
    }



    /**
     * 分页查询
     */
    @RequestMapping("/selectPage")
    public IPage<CategoryEntity> selectUserPage(Page<CategoryEntity> page, Integer state) {
        // 不进行 count sql 优化，解决 MP 无法自动优化 SQL 问题，这时候你需要自己查询 count 部分
        // page.setOptimizeCountSql(false);
        // 当 total 为小于 0 或者设置 setSearchCount(false) 分页插件不会进行 count 查询
        // 要点!! 分页返回的对象与传入的对象是同一个
        return categoryService.selectPageVo(page, state);
    }

    /**
     * 递归找所有的子菜单、中途要排序
     */
    List<CategoryEntity> getChildren(CategoryEntity categoryEntity, List<CategoryEntity> all) {
        return all.stream().filter((entity) ->
                entity.getParentCid() == categoryEntity.getCatId()).
                map((entity) -> {
                    entity.setChildren(getChildren(entity, all));
                    return entity;
                }).
                sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }).
                collect(Collectors.toList());
    }




    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
  //  @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
  //  @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
