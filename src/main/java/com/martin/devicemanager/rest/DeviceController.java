package com.martin.devicemanager.rest;

import com.martin.devicemanager.persistence.Device;
import com.martin.devicemanager.persistence.SIMCard;
import com.martin.devicemanager.service.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/devices")
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping(value = "/forSale", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllDevicesForSale(@RequestParam(name = "page", required= false, defaultValue = "0") int page,
                                                   @RequestParam(name = "size", required= false, defaultValue = "25") int size) {
        Page<Device> devices = deviceService.findAllDevicesForSale(page, size);
        int totalPages = devices.getTotalPages();
        if (totalPages == 0) {
            return ResponseEntity.notFound().build();
        }
        if (page >= totalPages) {
            return ResponseEntity.badRequest()
                    .body("total pages = " + totalPages + " but requested page zero-based index is " + page);
        }
        return ResponseEntity.ok().body(devices);
    }

    @GetMapping(path="/waitingForActivation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllDevicesWaitingForActivation(@RequestParam(name = "page", required= false, defaultValue = "0") int page,
                                                                @RequestParam(name = "size", required= false, defaultValue = "25") int size) {
        Page<Device> devices = deviceService.findAllDevicesWaitingForActivation(page, size);
        int totalPages = devices.getTotalPages();
        if (totalPages == 0) {
            return ResponseEntity.notFound().build();
        }
        if (page >= totalPages) {
            return ResponseEntity.badRequest()
                    .body("total pages = " + totalPages + " but requested page zero-based index is " + page);
        }
        return ResponseEntity.ok().body(devices);
    }

    @GetMapping(path="/{deviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findDeviceWith(@PathVariable("deviceId") Integer deviceId) {
        return deviceService.findDeviceById(deviceId)
                .map(device -> ResponseEntity.ok().body(device))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping(path = "/removeConfig/{deviceId}", produces = MediaType.APPLICATION_JSON_VALUE) //TODO use etag header for conditional updates
    ResponseEntity<?> removeDeviceConfig(@PathVariable("deviceId") Integer deviceId) {

        Result result = deviceService.removeConfigurationForDeviceWithId(deviceId);
        return buildResponseFrom(result);
    }

    @PatchMapping(path = "/updateConfig/{deviceId}", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE) //TODO use an etag header for conditional updates
    ResponseEntity<?> updateDeviceConfig(@PathVariable("deviceId") Integer deviceId,
                                         @RequestBody DeviceConfigUpdateRequest request) {
        Result result = deviceService.updateConfigurationForDevice(deviceId, request);
        return buildResponseFrom(result);
    }

    private ResponseEntity<?> buildResponseFrom(Result result) {
        if (result instanceof DeviceConfigRemovedResult) {
            return ResponseEntity.ok().build();
        } else if (result instanceof DeviceNotFoundResult) {
            return ResponseEntity.notFound().build();
        } else if (result instanceof DeviceConfigUpdatedResult) {
            return ResponseEntity.ok().build();
        } else if (result instanceof DeviceNotConfiguredResult) {
            return ResponseEntity.badRequest().build();
        } else if (result instanceof DeviceConfigUpdateConflictResult) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
