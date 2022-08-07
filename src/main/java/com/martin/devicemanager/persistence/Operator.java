package com.martin.devicemanager.persistence;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@ToString
@EqualsAndHashCode
@Entity
@Table(name = "mobile_operator")
public class Operator {

    public Operator() {}
    public Operator(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Id
    @Getter @Setter
    @Column(name = "code", unique = true, nullable = false, length = 10)
    @Size(max = 10, message = "OperatorCode max length is 10 chars")
    private String code;

    @Getter @Setter
    @Column(name = "NAME", unique = true, nullable = false, length = 100)
    @Size(max = 100, message = "OperatorCode max length is 100 chars")
    @NotBlank
    private String name;

    @OneToMany(mappedBy="operator")
    @JsonBackReference
    @Getter @Setter
    private Set<SIMCard> simCards;

    @Version
    @JsonIgnore
    @Getter
    private long version;
}
