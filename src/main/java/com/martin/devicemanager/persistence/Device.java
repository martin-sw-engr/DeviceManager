package com.martin.devicemanager.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ToString
@EqualsAndHashCode
@Entity
@Table(name = "iot_device")
public class Device {
    public Device() {}

    public Device(DeviceStatus status, int temperature) {
        this.status = status;
        this.temperature = temperature;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @Getter @Setter
    @Column(name = "STATUS", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    /**
     * Temperature in Celsius
     */
    @Getter @Setter
    @Column(name = "TEMPERATURE")
    private Integer temperature;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true )
    @JoinTable(name = "device_sim",
            joinColumns =
                    { @JoinColumn(name = "device_id", referencedColumnName = "id") },
            inverseJoinColumns =
                    { @JoinColumn(name = "sim_id", referencedColumnName = "id") }
    )
    @Getter @Setter
    @JsonManagedReference
    private SIMCard simCard;

    @Version
    @JsonIgnore
    @Getter
    private long version;
}
