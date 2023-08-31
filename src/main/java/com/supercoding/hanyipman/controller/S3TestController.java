package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.AwsS3Service;
import com.supercoding.hanyipman.utils.ApiUtils;
import com.supercoding.hanyipman.utils.FilePath;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/api/test")
@RestController
@RequiredArgsConstructor
public class S3TestController {

    private final AwsS3Service awsS3Service;

    @PostMapping("/upload/test")
    @RequestBody(content = @Content(mediaType = "multipart/form-data",
            schema = @Schema(implementation = MultipartFile.class)))
    public Response<String> imageUploadTest(@RequestPart(value = "multipartFile", required = false)MultipartFile multipartFile) throws IOException {
        awsS3Service.uploadImage(multipartFile, FilePath.TEST_DIR.getPath());
        return ApiUtils.success(HttpStatus.CREATED.value(), "이미지 업로드 성공", "success");
    }

}
