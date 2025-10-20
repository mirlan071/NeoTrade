package com.neotrade.exception;

public class AdvertisementNotFoundException extends BaseException {
    public AdvertisementNotFoundException() {
        super("ad.not_found");
    }
}