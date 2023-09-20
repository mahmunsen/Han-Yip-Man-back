package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuRequest;
import com.supercoding.hanyipman.dto.Shop.seller.response.MenuResponse;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.enums.FilePath;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.*;
import com.supercoding.hanyipman.repository.MenuGroupRepository;
import com.supercoding.hanyipman.repository.menu.MenuRepository;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.shop.ShopCustomRepositoryImpl;
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
    private final ShopCustomRepositoryImpl shopCustomRepository;


    @Transactional
    public void createMenu(RegisterMenuRequest registerMenuRequest, MultipartFile menuThumbnailImage, Long menuGroupId) {
        MenuGroup menuGroup = validMenuGroup(menuGroupId);
        Seller seller = validSellerUser(JwtToken.user());
        if (!Objects.equals(menuGroup.getShop().getSeller().getId(), seller.getId())) {
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

    public void changeThumbnail(MultipartFile thumbnailImage, Long menuId) {
        Seller seller = JwtToken.user().validSeller();
        Menu menu = validMenu(menuId);
        shopCustomRepository.checkRegisterShopSellerByMenu(menuId, seller.getId());

        String newThumbnailUrl = updateImageFile(thumbnailImage, menu, menu.getImageUrl());
        menu.setImageUrl(newThumbnailUrl);
        menuRepository.save(menu);
    }

    public void changeName(String name, Long menuId) {
        Seller seller = JwtToken.user().validSeller();
        Menu menu = validMenu(menuId);
        shopCustomRepository.checkRegisterShopSellerByMenu(menuId, seller.getId());

        menu.setName(name);
        menuRepository.save(menu);
    }

    public void changePrice(Integer price, Long menuId) {
        Seller seller = JwtToken.user().validSeller();
        Menu menu = validMenu(menuId);
        shopCustomRepository.checkRegisterShopSellerByMenu(menuId, seller.getId());

        menu.setPrice(price);
        menuRepository.save(menu);
    }

    public void changeDescription(String description, Long menuId) {
        Seller seller = JwtToken.user().validSeller();
        Menu menu = validMenu(menuId);
        shopCustomRepository.checkRegisterShopSellerByMenu(menuId, seller.getId());

        menu.setDescription(description);
        menuRepository.save(menu);
    }

    public void changeMenuGroup(Long menuGroupId, Long menuId) {

        Seller seller = JwtToken.user().validSeller();
        Menu menu = validMenu(menuId);
        MenuGroup menuGroup = validMenuGroup(menuGroupId);
        shopCustomRepository.checkRegisterShopSellerByMenu(menuId, seller.getId());
        menu.setMenuGroup(menuGroup);
        menuRepository.save(menu);
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        Seller seller = JwtToken.user().validSeller();
        Menu menu = validMenu(menuId);
        shopCustomRepository.checkRegisterShopSellerByMenu(menuId, seller.getId());
        menu.setDeleted(true);
    }


    private MenuGroup validMenuGroup(Long menuGroupId) {
        return menuGroupRepository.findByIdAndIsDeletedFalse(menuGroupId).orElseThrow(() -> new CustomException(MenuGroupErrorCode.NOT_FOUND_MENU_GROUP));
    }

    private Seller validSellerUser(User user) {
        return sellerRepository.findByUser(user).orElseThrow(() -> new CustomException(SellerErrorCode.NOT_SELLER));
    }

    private Menu validMenu(Long menuId) {
        return menuRepository.findByMenuIsDeletedFalse(menuId).orElseThrow(() -> new CustomException(MenuErrorCode.NOT_FOUND_MENU));
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

    private String updateImageFile(MultipartFile multipartFile, Menu menu, String originUrl) {
        String uniqueIdentifier = UUID.randomUUID().toString();
        try {
            if (multipartFile != null) {
                awsS3Service.removeFile(originUrl);
                return awsS3Service.uploadImage(multipartFile, FilePath.MENU_DIR.getPath() + menu.getId() + "/" + uniqueIdentifier);
            }
        }catch (IOException e) {
            throw new CustomException(FileErrorCode.FILE_UPLOAD_FAILED);
        }
        return null;
    }
}
