package com.supercoding.hanyipman.enums;

import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.PaymentErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentProvidor {
    KAKAO("KakaoPay", "카카오페이"),
    IAMPORT("html5_inicis", "아임포트");
    private final String providerEn;
    private final String providerKo;

    public static String convertEnToKo(String provider) {
        for(PaymentProvidor paymentProvidor : PaymentProvidor.values()){
            if(paymentProvidor.getProviderEn().equals(provider)) return paymentProvidor.getProviderKo();
        }
        throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_NOT_FOUND_PROVIDER);
    }

}
