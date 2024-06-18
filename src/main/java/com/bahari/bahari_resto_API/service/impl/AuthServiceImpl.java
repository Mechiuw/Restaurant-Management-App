package com.bahari.bahari_resto_API.service.impl;

import com.bahari.bahari_resto_API.constant.ERole;
import com.bahari.bahari_resto_API.model.dto.request.AuthRequest;
import com.bahari.bahari_resto_API.model.dto.request.CustomerRequest;
import com.bahari.bahari_resto_API.model.dto.response.LoginResponse;
import com.bahari.bahari_resto_API.model.dto.response.RegisterResponse;
import com.bahari.bahari_resto_API.model.entity.Customer;
import com.bahari.bahari_resto_API.model.entity.Role;
import com.bahari.bahari_resto_API.model.entity.UserCredential;
import com.bahari.bahari_resto_API.repository.UserCredentialRepository;
import com.bahari.bahari_resto_API.security.JwtUtil;
import com.bahari.bahari_resto_API.service.AuthService;
import com.bahari.bahari_resto_API.service.CustomerService;
import com.bahari.bahari_resto_API.service.RoleService;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final RoleService roleService;
    @Override
    @Transactional(rollbackOn = Exception.class)
    public RegisterResponse registerCustomer(AuthRequest authRequest) {
        try {
            //TODO 1 set the role
            Role role = roleService.getOrSave(ERole.USER);

            //TODO 2 set Credential
            UserCredential userCredential = UserCredential.builder()
                    .username(authRequest.getUsername().toLowerCase())
                    .password(passwordEncoder.encode(authRequest.getPassword()))
                    .role(role)
                    .build();
            userCredentialRepository.saveAndFlush(userCredential);

            //TODO 3 set Customer
            Customer customer = Customer.builder()
                    .name(authRequest.getName())
                    .address(authRequest.getAddress())
                    .email(authRequest.getEmail())
                    .phoneNum(authRequest.getMobilePhone())
                    .userCredential(userCredential)
                    .build();

            CustomerRequest customerRequest = CustomerRequest.builder()
                    .name(customer.getName())
                    .address(customer.getAddress())
                    .email(customer.getEmail())
                    .phoneNum(customer.getPhoneNum())
                    .build();
            customerService.create(customerRequest);

            return RegisterResponse.builder()
                    .username(userCredential.getUsername())
                    .role(userCredential.getRole().getName().toString())
                    .build();

        } catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"user already exist");
        }
    }

    @Override
    public LoginResponse login(AuthRequest authRequest) {
        return null;
    }
}
