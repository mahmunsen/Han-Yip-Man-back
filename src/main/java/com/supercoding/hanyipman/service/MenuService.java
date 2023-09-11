package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuRequest;
import com.supercoding.hanyipman.dto.Shop.seller.response.MenuResponse;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.enums.FilePath;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.FileErrorCode;
import com.supercoding.hanyipman.error.domain.MenuGroupErrorCode;
import com.supercoding.hanyipman.error.domain.SellerErrorCode;
import com.supercoding.hanyipman.error.domain.ShopErrorCode;
import com.supercoding.hanyipman.repository.MenuGroupRepository;
import com.supercoding.hanyipman.repository.MenuRepository;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.security.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuGroupRepository menuGroupRepository;
    private final MenuRepository menuRepository;
    private final AwsS3Service awsS3Service;
    private final SellerRepository sellerRepository;


    @Transactional
    public void createMenu(RegisterMenuRequest registerMenuRequest, MultipartFile menuThumbnailImage, Long menuGroupId) {
        MenuGroup menuGroup = validMenuGroup(menuGroupId);
        Seller seller = validSellerUser(JwtToken.user());
        if (Objects.equals(menuGroup.getShop().getSeller().getId(), seller.getId())) {
            throw new CustomException(ShopErrorCode.DIFFERENT_SELLER);
        }

        Integer sequence = menuRepository.findMaxSequenceByShop(menuGroup);
        Menu menu = Menu.from(registerMenuRequest, menuGroup, sequence);
        List<Option> options = registerMenuRequest.getOptions()
                .stream()
                .map(optionGroupRequest -> {
                    Option newOption = Option.from(optionGroupRequest);
                    List<OptionItem> optionItems = optionGroupRequest.getOptionItems()
                            .stream()
                            .map(OptionItem::from).collect(Collectors.toList());
                    newOption.addOptionItemList(optionItems);
                    return newOption;
                })
                .collect(Collectors.toList());

        menu.addOptionList(options);
        Menu savedMenu = menuRepository.save(menu);
        savedMenu.setImageUrl(uploadImageFile(menuThumbnailImage, savedMenu));
    }

    @Transactional
    public List<MenuResponse> findMenuListByMenuGroupId(Long menuGroupId) {

        MenuGroup menuGroup = validMenuGroup(menuGroupId);
        Seller seller = validSellerUser(JwtToken.user());
        if (!Objects.equals(menuGroup.getShop().getSeller().getId(), seller.getId())) {
            throw new CustomException(ShopErrorCode.DIFFERENT_SELLER);
        }

        List<Menu> menus = menuRepository.findAllByMenuGroupAndIsDeletedFalse(menuGroup);
        return menus.stream().map(MenuResponse::from).collect(Collectors.toList());
    }

    private MenuGroup validMenuGroup(Long menuGroupId) {
        return menuGroupRepository.findByIdAndIsDeletedFalse(menuGroupId).orElseThrow(() -> new CustomException(MenuGroupErrorCode.NOT_FOUND_MENU_GROUP));
    }

    private Seller validSellerUser(User user) {
        return sellerRepository.findByUser(user).orElseThrow(() -> new CustomException(SellerErrorCode.NOT_SELLER));
    }

    private String uploadImageFile(MultipartFile multipartFile, Menu menu) {
        String uniqueIdentifier = UUID.randomUUID().toString();
        try {
            if (multipartFile != null) {
                return awsS3Service.uploadImage(multipartFile, FilePath.MENU_DIR.getPath() + menu.getId() + "/" + uniqueIdentifier);
            }
        }catch (IOException e) {
            throw new CustomException(FileErrorCode.FILE_UPLOAD_FAILED);
        }
        return null;
    }
}
