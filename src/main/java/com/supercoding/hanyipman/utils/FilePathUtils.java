package com.supercoding.hanyipman.utils;

import com.supercoding.hanyipman.enums.FilePath;

public class FilePathUtils {
    public static String convertImageUrlToFilePath(String imageUrl){
        return imageUrl.substring(
                imageUrl.indexOf(
                        FilePath.SEPARATE_POINT.getPath()) + 5,
                        imageUrl.length()
        );
    }
}
