package com.martin.devicemanager.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class DeviceConfigUpdatedResult implements Result {

    @Getter
    private final Integer deviceId;

    @Getter
    private final String simCardId;
}
