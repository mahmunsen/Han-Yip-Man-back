package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.dto.auth.request.SellerSignUpRequest;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Transactional
    public String sellerSignup(SellerSignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new CustomException(UserErrorCode.DUPLICATE_MEMBER_ID);


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); //비밀번호를 암호화하는 데 사용할 수 있는 메서드

        if (!Objects.equals(request.getPassword(), request.getPasswordCheck()))
            throw new CustomException(UserErrorCode.INVALID_PASSWORD_CONFIRMATION);

        String encodingPassword = passwordEncoder.encode(request.getPassword());
        User user = User.toUser(request, encodingPassword);
        User savedUser = userRepository.save(user);
        Seller seller = Seller.toSeller(request, savedUser);
        sellerRepository.save(seller);

        return savedUser.getEmail();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
