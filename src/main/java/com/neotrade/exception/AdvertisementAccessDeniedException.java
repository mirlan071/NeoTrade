package com.neotrade.exception;

public class AdvertisementAccessDeniedException extends BaseException {
    public AdvertisementAccessDeniedException() {
        super("ad.access_denied");
    }
}