package com.racha.ChatWithMe.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.racha.ChatWithMe.model.Account;
import com.racha.ChatWithMe.repository.AccountRepository;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Account save(Account account) {
        try {

            // Generate a new ID if not provided
            if (account.getId() == null || account.getId().isEmpty()) {
                // Check if the email already exists
                if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
                    throw new RuntimeException("Email already exists");
                } else if(accountRepository.findByUsername(account.getUsername()).isPresent()) {
                    throw new RuntimeException("User name already exists");
                }
                Optional<Account> maxIdOpt = findMaxId();
                String newidString = maxIdOpt.map(Account::getId).orElse("0");
                Long newId = Long.parseLong(newidString) + 1;
                account.setId(String.valueOf(newId));
            }

            // Encode the password
            account.setPassword(passwordEncoder.encode(account.getPassword()));

            if (account.getAuthorities() == null) {
                account.setAuthorities("USER"); // Default role for new accounts
            }

            return accountRepository.save(account);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (e.getMessage().contains("duplicate key error")) {
                throw new RuntimeException("Email already exists");
            }
            throw new RuntimeException("An error occurred while saving the account" + e.getMessage());
        }
    }

    public Optional<Account> findMaxId() {
        return accountRepository.findTopByOrderByIdDesc();
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(String id) {
        return accountRepository.findById(id);
        // .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Optional<Account> findTopByOrderByIdDesc() {
        return accountRepository.findTopByOrderByIdDesc();
    }

    public void delete(Account account) {
        accountRepository.delete(account);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("Account not found");
        }
        Account account = optionalAccount.get();
        List<GrantedAuthority> grantedAuthority = new ArrayList<>();

        grantedAuthority.add(new SimpleGrantedAuthority(account.getAuthorities()));
        return new User(account.getEmail(), account.getPassword(), grantedAuthority);

    }
}
