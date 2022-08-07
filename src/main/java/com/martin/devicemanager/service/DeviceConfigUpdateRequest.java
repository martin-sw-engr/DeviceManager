package com.martin.devicemanager.service;

import com.martin.devicemanager.persistence.DeviceStatus;
import lombok.Data;

@Data
public class DeviceConfigUpdateRequest {
    private DeviceStatus deviceStatus;
    private String sim_id;
    private String sim_status;
    private String country_code;
    private String operator_code;
    private String operator_name;
}