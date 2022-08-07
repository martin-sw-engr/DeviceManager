package com.martin.devicemanager.persistence;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Locale;

@ToString
@EqualsAndHashCode
@Entity
@Table(name = "sim")
public class SIMCard {

    public SIMCard() {}

    public SIMCard(String id, Operator operator, String countryCode, SIMStatus status) {
        this.id = id;
        this.operator = operator;
        this.countryCode = countryCode;
        this.status = status;
    }

    @Id
    @Getter
    @Setter
    @Column(name = "ID", unique = true, nullable = false)
    @NotEmpty(message = "id must not be empty")
    @Size(max = 15, message = "id max length is 15 chars")
    private String id;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name="operator_id", nullable=false)
    @NotNull
    @JsonManagedReference
    private Operator operator;

    /**
     * Standardised 2 letter ISO country code
     * rather than storing the country name
     */
    @Getter @Setter
    @Column(name = "COUNTRY_CODE_ISO", nullable = false, length = 2)
    @NotEmpty(message = "Country code must not be empty")
    @Size(max = 2, message = "Country code max length is 2 chars")
    private String countryCode;

    /**
     * Standardised country name for display only
     */
    @Transient
    public String getCountryName() {
        String countryName = "Missing Country Name";
        if (countryCode != null && !countryCode.isEmpty()) {
            Locale locale = new Locale("", countryCode);
            countryName = locale.getDisplayCountry();
        }
        return countryName;
    }

    @Getter @Setter
    @Column(name = "STATUS", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private SIMStatus status;

    @OneToOne(mappedBy = "simCard")
    @JsonBackReference
    private Device device;

    @Version
    @JsonIgnore
    @Getter
    private long version;
}
