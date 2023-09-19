package com.supercoding.hanyipman.utils;

import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.UtilErrorCode;


public class PhoneUtils {

    public static String formattedPhoneNumber(String phoneNumber) {
        String formattedPhoneNumber;
        if (phoneNumber.length() == 11) {
            formattedPhoneNumber = String.format("%s-%s-%s", phoneNumber.substring(0, 3), phoneNumber.substring(3, 7), phoneNumber.substring(7));
        } else if (phoneNumber.length() == 9) {
            formattedPhoneNumber = String.format("%s-%s-%s", phoneNumber.substring(0, 2), phoneNumber.substring(2, 5), phoneNumber.substring(5));
        } else if (phoneNumber.contains("-")) {
            return phoneNumber;
        } else throw new CustomException(UtilErrorCode.WRONG_PHONENUMBER);
        return formattedPhoneNumber;
    }
}
