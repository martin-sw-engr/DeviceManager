package com.martin.devicemanager.service;

import com.martin.devicemanager.persistence.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
//@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepo;
    private final OperatorRepository operatorRepo;
    private final SIMCardRepository simCardRepo;

    public void removeAllEntities() {
        deviceRepo.deleteAll();
        simCardRepo.deleteAll();
        operatorRepo.deleteAll();
    }

    public Optional<Operator> findOperatorById(String opCode) {
        return operatorRepo.findById(opCode);
    }

    public void deleteOperatorByCode(String code) {
        operatorRepo.deleteById(code);
    }

    public List<Operator> findAllOperators() {
        return operatorRepo.findAll();
    }

    public Optional<SIMCard> findSIMCardById(String simId) {
        return simCardRepo.findById(simId);
    }

    public void deleteSIMCardById(String id) {
        simCardRepo.deleteById(id);
    }

    public List<SIMCard> findAllSIMCards() {
        return simCardRepo.findAll();
    }

    public Optional<Device> findDeviceById(Integer id) {
        return deviceRepo.findById(id);
    }

    public void deleteDeviceById(Integer id) {
        deviceRepo.deleteById(id);
    }

    public List<Device> findAllDevices() {
        return deviceRepo.findAll();
    }

    public Result removeConfigurationForDeviceWithId(Integer id) {
        Optional<Device> optionalDevice = deviceRepo.findById(id);
        if (optionalDevice.isEmpty()) {
            return new DeviceNotFoundResult(id);
        }

        Device device = optionalDevice.get();
        SIMCard removedSimCard = device.getSimCard();
        if (removedSimCard == null) {
            return new DeviceNotConfiguredResult(id);
        }

        DeviceConfigRemovedResult deviceConfigRemovedResult =
                new DeviceConfigRemovedResult(id,
                        removedSimCard.getId(),
                        removedSimCard.getOperator().getCode(),
                        removedSimCard.getCountryCode(),
                        removedSimCard.getCountryName(),
                        removedSimCard.getStatus());
        device.setSimCard(null);
        device.setStatus(DeviceStatus.NOT_READY);
        try {
            save(device);
        } catch (StaleStateException e) {
            log.error("Stale state encountered", e);
            return new DeviceConfigUpdateConflictResult();
        }

        return deviceConfigRemovedResult;
    }

    public Result updateConfigurationForDevice(Integer deviceId, DeviceConfigUpdateRequest request) {
        Optional<Device> optionalDevice = deviceRepo.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            return new DeviceNotFoundResult(deviceId);
        }

        Device device = optionalDevice.get();
        if (request.getSim_id() == null) {
            return new DeviceNotConfiguredResult(deviceId);
        }
        SIMCard existingSimCard = device.getSimCard();
        if (existingSimCard != null) {
            existingSimCard.setStatus(SIMStatus.valueOf(request.getSim_status()));
        } else {
            Operator operator = null;
            Optional<Operator> operatorOptional = operatorRepo.findById(request.getOperator_code());
            if (operatorOptional.isPresent()) {
                operator = operatorOptional.get();
            } else {
                operator = new Operator(request.getOperator_code(), request.getOperator_name());
            }
            SIMCard newSimCard = new SIMCard(request.getSim_id(),
                    operator,
                    request.getCountry_code(),
                    SIMStatus.valueOf(request.getSim_status()));
            device.setSimCard(newSimCard);
            device.setStatus(request.getDeviceStatus());
        }

        DeviceConfigUpdatedResult deviceConfigUpdatedResult =
                new DeviceConfigUpdatedResult(deviceId, request.getSim_id());
        try {
            save(device);
        } catch (StaleStateException e) {
            log.error("Stale state encountered", e);
            return new DeviceConfigUpdateConflictResult();
        }

        return deviceConfigUpdatedResult;
    }

    public Page<Device> findAllDevicesForSale(int page, int size) {
        return deviceRepo.findAllForSale(PageRequest.of(page, size));
    }

    public Page<Device> findAllDevicesWaitingForActivation(int page, int size) {
        return deviceRepo.findAllWaitingForActivation(PageRequest.of(page, size));
    }

    public Device save(Device device) {
        try {
            SIMCard simCard = device.getSimCard();
            if (simCard != null) {
                Operator operator = simCard.getOperator();
                if (operator != null) {
                    operatorRepo.save(operator);
                }
            }
            return deviceRepo.save(device);
        } catch (OptimisticLockingFailureException ex) {
            log.error("OptimisticLockingFailureException while saving Device with Id = {}",
                    device.getId(), ex);
            throw new StaleStateException();
        }
    }
}
