package com.hxut.dto;

import com.hxut.entity.Dish;
import com.hxut.entity.DishFlavor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:  由于单单一个category表无法接受前端传过来的数据,所以我们封装一个dto
 * @param
 * @return:
 * @author: MR.孙
 * @date: 2022/6/25 9:45
*/
@Data
public class DishDto extends Dish implements Serializable {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
