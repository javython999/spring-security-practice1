package com.errday.springsecuritypractice.admin.service.impl;

import com.errday.springsecuritypractice.admin.repository.RoleHierarchyRepository;
import com.errday.springsecuritypractice.admin.service.RoleHierarchyService;
import com.errday.springsecuritypractice.domain.entity.RoleHierarchy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

    private final RoleHierarchyRepository roleHierarchyRepository;


    @Transactional
    @Override
    public String findAllHierarchy() {
        List<RoleHierarchy> roleHierarchy = roleHierarchyRepository.findAll();

        Iterator<RoleHierarchy> iterator = roleHierarchy.iterator();
        StringBuilder hierarchyRole = new StringBuilder();

        while (iterator.hasNext()) {
            RoleHierarchy role = iterator.next();

            if (role.getParent() != null) {
                hierarchyRole.append(role.getParent().getRoleName());
                hierarchyRole.append(" > ");
                hierarchyRole.append(role.getRoleName());
                hierarchyRole.append("\n");
            }
        }

        return hierarchyRole.toString();
    }
}
