package com.hxut.dto;


import com.hxut.entity.Setmeal;
import com.hxut.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
