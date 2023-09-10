package com.supercoding.hanyipman.dto.Shop.seller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "메뉴 등록 DTO")
public class RegisterMenuRequest {
    private String menuName;
    private Integer price;
    private String description;
    private List<OptionGroupRequest> options;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionGroupRequest {
        private String optionName;
        private Boolean isMultiple;
        private List<OptionItemRequest> optionItems;


        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OptionItemRequest {
            private String itemName;
            private Integer itemPrice;

        }
    }

}
