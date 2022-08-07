package com.martin.devicemanager.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
    @Query("SELECT d FROM Device d " +
                " WHERE d.status = com.martin.devicemanager.persistence.DeviceStatus.READY AND " +
                " d.temperature >= -25 AND d.temperature <= 85"
    )
    Page<Device> findAllForSale(Pageable pageable);

    @Query("SELECT d FROM Device d " +
            " WHERE d.simCard.status = com.martin.devicemanager.persistence.SIMStatus.WAITING_FOR_ACTIVATION")
    Page<Device> findAllWaitingForActivation(Pageable pageable);
}
