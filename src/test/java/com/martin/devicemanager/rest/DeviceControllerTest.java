package com.martin.devicemanager.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.martin.devicemanager.persistence.*;
import com.martin.devicemanager.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URL;
import java.util.List;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DeviceControllerTest {

    @Autowired(required = false)
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Autowired
    private DeviceService deviceService;

    @BeforeEach
    public void removeAllEntities() {
        deviceService.removeAllEntities();
    }

    @Test
    @DisplayName("devices ready for sale when none available")
    public void testGetDevicesForSaleReturns404WhenNoneExists() throws Exception {
        //When
        ResultActions resultActions = mockMvc.perform(get("/devices/forSale"));

        //Then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("devices ready for sale when available")
    public void testGetDevicesForSaleReturnsAvailableDevices() throws Exception {
        //Given
        Device d = new Device(DeviceStatus.READY, 20);
        Operator op = new Operator("Buzz", "Buzz Mobiles");
        SIMCard sim = new SIMCard("MyNewSim", op, "GB", SIMStatus.BLOCKED);
        d.setSimCard(sim);
        deviceService.save(d);

        //When
        ResultActions resultActions = mockMvc.perform(get("/devices/forSale"));

        //Then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("devices waiting for activation when none available")
    public void testGetDevicesWaitingForActivationReturns404WhenNoneExists() throws Exception {
        //When
        ResultActions resultActions = mockMvc.perform(get("/devices/waitingForActivation"));

        //Then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("devices waiting for Activation when available")
    public void testGetDevicesWaitingForActivationReturnsAvailableDevices() throws Exception {
        //Given
        Device d = new Device(DeviceStatus.READY, 20);
        Operator op = new Operator("Buzz", "Buzz Mobiles");
        SIMCard sim = new SIMCard("MyNewSim", op, "GB", SIMStatus.WAITING_FOR_ACTIVATION);
        d.setSimCard(sim);
        deviceService.save(d);

        //When
        ResultActions resultActions = mockMvc.perform(get("/devices/waitingForActivation"));

        //Then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }
}
