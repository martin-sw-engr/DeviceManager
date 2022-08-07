package com.martin.devicemanager;

import com.martin.devicemanager.rest.DeviceController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DeviceManagerAppTest {

    @Autowired
    private DeviceController deviceController;

    @Test
    public void contextLoads() {
        assertNotNull(deviceController);
    }
}
