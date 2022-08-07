package com.martin.devicemanager.service;

import com.martin.devicemanager.persistence.SIMStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class DeviceConfigRemovedResult implements Result {

    @Getter
    private final Integer deviceId;

    @Getter
    private final String simCardId;

    @Getter
    private final String operatorCode;

    @Getter
    private final String countryCode;

    @Getter
    private final String countryName;

    @Getter
    private final SIMStatus simStatus;
}
