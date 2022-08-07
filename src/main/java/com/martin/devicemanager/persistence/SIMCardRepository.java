package com.martin.devicemanager.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SIMCardRepository extends JpaRepository<SIMCard, String> {

}
