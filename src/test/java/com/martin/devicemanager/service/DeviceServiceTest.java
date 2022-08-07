package com.martin.devicemanager.service;

import com.martin.devicemanager.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static com.martin.devicemanager.persistence.DeviceStatus.READY;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
public class DeviceServiceTest {

    @Autowired
    private DeviceService deviceService;

    @BeforeEach
    public void clearAllRepos() {
        deviceService.removeAllEntities();
    }

    @Test
    @DisplayName("save persists device")
    public void testSaveDeviceWithSIM() {
        //Given
        Device d = new Device(DeviceStatus.NOT_READY, 20);
        Operator op = new Operator("Buzz", "Buzz Mobiles");
        SIMCard sim = new SIMCard("MyNewSim", op, "GB", SIMStatus.BLOCKED);
        d.setSimCard(sim);

        //When
        Device savedDevice = deviceService.save(d);

        //Then
        assertTrue(deviceService.findAllDevices().size() == 1);
        assertTrue(deviceService.findAllSIMCards().size() == 1);
        assertTrue(deviceService.findAllOperators().size() == 1);

        assertNotNull(savedDevice.getId(), "device id is null");
        assertNotNull(savedDevice.getSimCard().getId(), "sim card id is null");
        assertNotNull(savedDevice.getSimCard().getOperator().getCode(), "operator code is null");
    }

    @Test
    @DisplayName("save persists device")
    public void testDeleteDeviceWithSIMCleansUpRepos() {
        //Given
        Device d = new Device(DeviceStatus.NOT_READY, 20);
        Operator op = new Operator("Buzz", "Buzz Mobiles");
        SIMCard sim = new SIMCard("MyNewSim", op, "GB", SIMStatus.BLOCKED);
        d.setSimCard(sim);

        //When
        Device savedDevice = deviceService.save(d);

        //Then
        assertTrue(deviceService.findAllDevices().size() == 1);
        assertTrue(deviceService.findAllSIMCards().size() == 1);
        assertTrue(deviceService.findAllOperators().size() == 1);

        //and when
        deviceService.deleteDeviceById(savedDevice.getId());

        //Then
        assertTrue(deviceService.findAllDevices().size() == 0);
        assertTrue(deviceService.findAllSIMCards().size() == 0);
        assertTrue(deviceService.findAllOperators().size() == 1);
    }

    @Test
    @DisplayName("save persists device without SIM configured")
    public void testSaveDeviceWithoutSIM() {
        //Given
        Device d = new Device(DeviceStatus.NOT_READY, 20);

        //When
        Integer idBeforeSave = d.getId();
        Device savedDevice = deviceService.save(d);

        //Then
        Integer idAfterSave = savedDevice.getId();
        assertNotEquals(idBeforeSave, idAfterSave);
    }

    @Test
    @DisplayName("save throws exception when more than one device is configured with the same SIM")
    public void testSaveMultipleDevicesWithSameSIM() {
        //Given
        Device d1 = new Device(DeviceStatus.NOT_READY, 20);
        Operator op = new Operator("Sky", "Sky Mobiles");
        SIMCard sim = new SIMCard("MySim", op, "GB", SIMStatus.BLOCKED);
        d1.setSimCard(sim);

        Device d2 = new Device(READY, 30);
        SIMCard sim2 = new SIMCard("MySim", op, "GB", SIMStatus.WAITING_FOR_ACTIVATION);
        d2.setSimCard(sim2);

        //When
        deviceService.save(d1);

        //Then
        assertThrows(DataIntegrityViolationException.class, ()-> deviceService.save(d2));
    }

    @Test
    @DisplayName("save persists devices with different SIMs from same operator")
    public void testSaveMultipleDeviceWithSIMCardsFromSameOperator() {
        //Given
        Device d1 = new Device(DeviceStatus.NOT_READY, 20);
        Operator op = new Operator("Talk", "Talk Mobiles2");
        SIMCard sim1 = new SIMCard("MySim1", op, "GB", SIMStatus.BLOCKED);
        d1.setSimCard(sim1);

        Device d2 = new Device(READY, 30);
        SIMCard sim2 = new SIMCard("MySim2", op, "GB", SIMStatus.BLOCKED);
        d2.setSimCard(sim2);

        //When
        Device savedDevice1 = deviceService.save(d1);
        Device savedDevice2 = deviceService.save(d2);

        //Then
        assertNotNull(savedDevice1.getId(), "device id is null");
        assertNotNull(savedDevice1.getSimCard().getId(), "sim card id is null");
        assertNotNull(savedDevice1.getSimCard().getOperator().getCode(), "operator code is null");

        assertNotNull(savedDevice2.getId(), "device id is null");
        assertNotNull(savedDevice2.getSimCard().getId(), "sim card id is null");
        assertNotNull(savedDevice2.getSimCard().getOperator().getCode(), "operator code is null");
    }

    @Test
    @DisplayName("save persists devices with different SIMs from different operators in different countries")
    public void testSaveMultipleDevicesWithSIMCardsFromOperatorsOfDifferentCountries() {
        //Given
        Device d1 = new Device(DeviceStatus.NOT_READY, 20);
        Operator op = new Operator("Talk", "Talk Mobiles2");
        SIMCard sim1 = new SIMCard("MySim1", op, "GB", SIMStatus.BLOCKED);
        d1.setSimCard(sim1);

        Device d2 = new Device(READY, 30);
        Operator op2 = new Operator("VOIP", "Voice Over");
        SIMCard sim2 = new SIMCard("MySim2", op2, "IN", SIMStatus.BLOCKED);
        d2.setSimCard(sim2);

        //When
        Device savedDevice1 = deviceService.save(d1);
        Device savedDevice2 = deviceService.save(d2);

        //Then
        assertNotNull(savedDevice1.getId(), "device id is null");
        assertNotNull(savedDevice1.getSimCard().getId(), "sim card id is null");
        assertNotNull(savedDevice1.getSimCard().getOperator().getCode(), "operator code is null");

        assertNotNull(savedDevice2.getId(), "device id is null");
        assertNotNull(savedDevice2.getSimCard().getId(), "sim card id is null");
        assertNotNull(savedDevice2.getSimCard().getOperator().getCode(), "operator code is null");
    }

    @Test
    @DisplayName("save throws exception when concurrent modification is done on same device")
    public void testSaveDeviceConcurrently() {
        //Given
        Device device = new Device(DeviceStatus.NOT_READY, 20);
        Operator op = new Operator("Reach", "Reach Mobiles");

        SIMCard sim = new SIMCard("MyNewSim", op, "GB", SIMStatus.BLOCKED);
        device.setSimCard(sim);
        Device savedDevice = deviceService.save(device);

        Optional<Device> optionalFoundDevice = deviceService.findDeviceById(savedDevice.getId());
        Device foundDevice = optionalFoundDevice.get();
        foundDevice.setStatus(READY);

        //When
        savedDevice.setTemperature(100);
        deviceService.save(savedDevice);

        //Then
        assertThrows(StaleStateException.class, ()-> deviceService.save(foundDevice));
    }
}
