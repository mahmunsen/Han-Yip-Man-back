package com.supercoding.hanyipman.dto.vo;

import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.TokenErrorCode;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.stream.Collectors;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationTestDto {

    private Long userId;
    private String email;
    private String role;

    public AuthenticationTestDto toAuthentication(UsernamePasswordAuthenticationToken token) {
        // 리스트형태 토큰 문자열로 파싱
        String role = token.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Object principal = token.getPrincipal();
        if (principal instanceof UserDetails) {
            String stringUserId = ((UserDetails) principal).getPassword();
            try {
                userId = Long.parseLong(stringUserId);
            } catch (NumberFormatException e) {
                throw new CustomException(TokenErrorCode.CANNOT_CONVERT_TO_LONG);
            }
        }


        return AuthenticationTestDto.builder()
                .userId(userId)
                .email(token.getName())
                .role(role)
                .build();
    }

}
