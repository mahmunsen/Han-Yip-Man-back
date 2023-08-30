package com.supercoding.hanyipman.utils;

public class FilePathUtils {
    public static String convertImageUrlToFilePath(String imageUrl){
        return imageUrl.substring(
                imageUrl.indexOf(
                        FilePath.SEPARATE_POINT.getPath()) + 5,
                        imageUrl.length()
        );
    }
}
