package com.datsan.caulong.service;

import com.datsan.caulong.model.Role;
import com.datsan.caulong.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role findRoleByName(String name){
        return roleRepository.findByName(name).get();
    }
}
