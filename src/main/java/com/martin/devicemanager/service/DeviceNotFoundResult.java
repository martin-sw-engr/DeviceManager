package com.martin.devicemanager.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class DeviceNotFoundResult implements Result {

    @Getter
    private final Integer deviceId;
}
