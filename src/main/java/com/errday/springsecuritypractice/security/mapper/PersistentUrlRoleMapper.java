package com.errday.springsecuritypractice.security.mapper;

import com.errday.springsecuritypractice.admin.repository.ResourcesRepository;
import com.errday.springsecuritypractice.domain.entity.Resources;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersistentUrlRoleMapper implements UrlRoleMapper {

    private final LinkedHashMap<String, String> urlRoleMappings = new LinkedHashMap<>();
    private final ResourcesRepository resourcesRepository;

    public PersistentUrlRoleMapper(ResourcesRepository resourcesRepository) {
        this.resourcesRepository = resourcesRepository;
    }

    @Override
    public Map<String, String> getUrlRoleMappings() {
        urlRoleMappings.clear();
        List<Resources> resourcesList = resourcesRepository.findAllResources();

        resourcesList.forEach(resources -> resources.getRoleSet()
                .forEach(role -> urlRoleMappings.put(resources.getResourceName(), role.getRoleName()))
        );

        return urlRoleMappings;
    }
}
