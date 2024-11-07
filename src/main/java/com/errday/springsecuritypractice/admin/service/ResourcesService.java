package com.errday.springsecuritypractice.admin.service;

import com.errday.springsecuritypractice.domain.entity.Resources;

import java.util.List;

public interface ResourcesService {
    Resources getResources(long id);
    List<Resources> getResources();

    void createResources(Resources Resources);

    void deleteResources(long id);
}
