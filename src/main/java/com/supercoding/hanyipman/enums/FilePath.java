package com.supercoding.hanyipman.enums;

public enum FilePath {
    TEST_DIR("user/image/"),
    SEPARATE_POINT(".com/"),
    SHOP_DIR("/shop/"),
    REVIEW_DIR("/review/");

    FilePath(String path) {
        this.path = path;
    }

    private final String path;

    public String getPath() {
        return path;
    }
}
