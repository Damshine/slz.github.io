package com.hxut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxut.entity.Category;

public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
