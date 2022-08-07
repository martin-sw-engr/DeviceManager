package com.martin.devicemanager.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private SIMCardRepository simRepo;

    @Autowired
    private OperatorRepository operatorRepo;

    @Override
    public void run(String... args) throws Exception {
        loadData(deviceRepo, simRepo, operatorRepo);
/*
        if (1 == 1) return;

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("---------------Configured Devices begin------------");
        Slice<Device> allConfigured = deviceRepo.fetchAllForSale(PageRequest.of(0, 1));
        while (allConfigured.hasContent()) {
            System.out.println("---Slice begins ----");
            allConfigured.forEach(d -> printDeviceAsJSON(mapper, d));
            System.out.println("---Slice ends ----");
            if (!allConfigured.hasNext()) {
                break;
            } else {
                allConfigured = deviceRepo.fetchAllForSale(allConfigured.nextPageable());
            }
        }
        System.out.println("---------------Configured Devices end  ------------");

        System.out.println("---------------Devices awaiting activation begin------------");
        List<Device> allWaitingForActivation = deviceRepo.findAllWaitingForActivation(PageRequest.of(0, 1));
        allWaitingForActivation.forEach(d -> printDeviceAsJSON(mapper, d));
        System.out.println("---------------Devices awaiting activation end  ------------");

        Device deviceToDelete = allWaitingForActivation.get(0);
        Integer deleteId = deviceToDelete.getId();
        Optional<Device> optionalDevice = deviceRepo.findById(deleteId);
        if (optionalDevice.isPresent()) {
            System.out.println("Deleting device with id " + deleteId);
            deviceRepo.deleteById(deleteId);
            System.out.println("Deleted device with id " + deleteId);
            Optional<Device> optionalDeviceAfterDelete = deviceRepo.findById(deleteId);
            if (optionalDeviceAfterDelete.isEmpty()) {
                System.out.println("Device with id " + deleteId + " no longer found");
            } else {
                System.out.println("Device with id " + deleteId + " still remains");
            }
        }

 */
    }

    private void printDeviceAsJSON(ObjectMapper mapper, Device d)  {
        try {
            String value = mapper.writeValueAsString(d);
            System.out.println(value);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadData(DeviceRepository deviceRepo,
                          SIMCardRepository simRepo,
                          OperatorRepository operatorRepo) {
        log.info("Starting initialisation of DB");
        List<Operator> operatorsList = new ArrayList<>();

        Operator vodaphone = new Operator("Vodaphone", "Vodaphone Inc");
        operatorsList.add(vodaphone);

        Operator sprint = new Operator("Sprint", "Sprint Inc");
        operatorsList.add(sprint);

        operatorsList = operatorRepo.saveAll(operatorsList);
        log.info("Added {} Operators", operatorsList.size());

        List<SIMCard> simCardList = new ArrayList<>();

        SIMCard simCard = new SIMCard("S1", operatorsList.get(0), "GB", SIMStatus.ACTIVE);
        simCardList.add(simCard);

        simCard = new SIMCard("S2", operatorsList.get(1), "IN", SIMStatus.WAITING_FOR_ACTIVATION);
        simCardList.add(simCard);

        simCard = new SIMCard("S3", operatorsList.get(1), "IN", SIMStatus.BLOCKED);
        simCardList.add(simCard);

        List<Device> deviceList = new ArrayList<>();

        Device device = new Device(DeviceStatus.NOT_READY, -30);
        deviceList.add(device);

        device = new Device(DeviceStatus.READY, -30);
        device.setSimCard(simCardList.get(0));
        deviceList.add(device);

        device = new Device(DeviceStatus.NOT_READY, 10);
        deviceList.add(device);

        device = new Device(DeviceStatus.READY, 10);
        device.setSimCard(simCardList.get(1));
        deviceList.add(device);

        device = new Device(DeviceStatus.READY, 20);
        device.setSimCard(simCardList.get(2));
        deviceList.add(device);

        deviceRepo.saveAll(deviceList);
        log.info("Added {} Devices", deviceList.size());

        log.info("Completed initialisation of DB", deviceList.size());
    }
}
