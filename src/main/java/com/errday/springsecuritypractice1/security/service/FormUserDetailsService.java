package com.errday.springsecuritypractice1.security.service;

import com.errday.springsecuritypractice1.users.domain.dto.AccountContext;
import com.errday.springsecuritypractice1.users.domain.dto.AccountDto;
import com.errday.springsecuritypractice1.users.domain.entity.Account;
import com.errday.springsecuritypractice1.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsService")
@RequiredArgsConstructor
public class FormUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account findByUsername = userRepository.findByUsername(username);

        if (findByUsername == null) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(findByUsername.getRoles()));
        ModelMapper modelMapper = new ModelMapper();
        AccountDto accountDto = modelMapper.map(findByUsername, AccountDto.class);

        return new AccountContext(accountDto, authorities);
    }
}
