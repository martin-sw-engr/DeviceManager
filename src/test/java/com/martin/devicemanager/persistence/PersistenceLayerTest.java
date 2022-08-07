package com.martin.devicemanager.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class PersistenceLayerTest {

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private OperatorRepository operatorRepo;

    @Autowired
    private SIMCardRepository simCardRepo;

    @BeforeEach
    public void clearRepo() {
        deviceRepo.deleteAll();
        simCardRepo.deleteAll();
        operatorRepo.deleteAll();
    }

    @Test
    @DisplayName("findById returns none when repo does not contain the device")
    public void testFindByIdDoesNotReturnDeviceWhenNoneExist() {

        //When
        Optional<Device> deviceOptional = deviceRepo.findById(12345678);

        //Then
        assertTrue(deviceOptional.isEmpty());
    }

    @Test
    @DisplayName("saving operators with same operator code updates the same record")
    public void testSaveOperatorsWithSamePrimaryKey() {
        //Given
        Operator op1 = new Operator("Reach", "Reach Mobiles");
        operatorRepo.save(op1);

        Operator op2 = new Operator("Reach", "OutReach Mobiles");

        //When
        operatorRepo.save(op2);

        //Then
        assertTrue(operatorRepo.findAll().size() == 1);
    }

    //TODO Move validation tests to Controller layer test
    @Disabled
    @Test
    @DisplayName("saving operator that fails validation")
    public void testSaveInvalidOperator() {
        //Given
        Operator op1 = new Operator("Reach123456789012345678901234567890", "Reach Mobiles");

        //Then
        assertThrows(javax.validation.ConstraintViolationException.class,
                () -> operatorRepo.save(op1));

        //Then
        assertTrue(operatorRepo.findAll().size() == 0);
    }

}
