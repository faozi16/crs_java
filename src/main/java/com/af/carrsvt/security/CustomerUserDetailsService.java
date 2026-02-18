package com.af.carrsvt.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.af.carrsvt.entity.Customer;
import com.af.carrsvt.repository.CustomerRepository;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> c = customerRepository.findByUsername(username);
        Customer customer = c.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomerUserDetails(customer);
    }
}
