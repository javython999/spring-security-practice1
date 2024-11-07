package com.errday.springsecuritypractice.admin.service;

import com.errday.springsecuritypractice.domain.dto.AccountDto;
import com.errday.springsecuritypractice.domain.entity.Account;

import java.util.List;

public interface UserManagementService {

    void modifyUser(AccountDto accountDto);

    List<Account> getUsers();
    AccountDto getUser(Long id);

    void deleteUser(Long idx);
}
