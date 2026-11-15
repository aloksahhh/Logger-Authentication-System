package com.example.SecureLoginDemo.service;

import com.example.SecureLoginDemo.entity.User;
import com.example.SecureLoginDemo.enumtype.Role;
import com.example.SecureLoginDemo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class Google0idcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Google0idcUserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        String email= oidcUser.getEmail();

        Boolean emailVerified = oidcUser.getClaim("email_verified");

        if(email == null || !Boolean.TRUE.equals(emailVerified)){
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            "invalid user info",
                            " Google email is missing or not verified",
                            null
                    )
            );
        }

        if(userRepository.findByEmail(email).isEmpty()){
            User user = new User();

            user.setEmail(email);

            String userName= generateUniqueUserName(email);

            user.setUsername(userName);

            String randomPassword = UUID.randomUUID().toString()+UUID.randomUUID();

            user.setPassword(passwordEncoder.encode(randomPassword));
            user.setRole(Role.USER);

            userRepository.save(user);
        }
        return oidcUser;
    }

    private String generateUniqueUserName( String email){
        String baseUsername = email.substring(0,email.indexOf("@"));

        String userName;
        do{
            userName=baseUsername+"_"+
                    UUID.randomUUID().toString().substring(0,8);
        }while(userRepository.existsByUsername(userName));

        return userName;
    }
}
