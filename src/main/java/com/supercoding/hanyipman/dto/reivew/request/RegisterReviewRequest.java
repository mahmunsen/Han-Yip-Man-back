package com.supercoding.hanyipman.dto.reivew.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterReviewRequest {
    private Long shopId;
    private String reviewContent;
    private Integer reviewScore;
    private MultipartFile reviewImage;
}
