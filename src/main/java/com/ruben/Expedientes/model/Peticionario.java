package com.ruben.Expedientes.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_peticionario")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo_peticionario")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PeticionarioDNI.class, name = "DNI"),
        @JsonSubTypes.Type(value = PeticionarioNIF.class, name = "NIF")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Peticionario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String address;
    private String tlf;
    private String email;

    @OneToMany(mappedBy = "peticionario")
    private List<ExpedientePrincipal> expedientePrincipalList;

    @OneToMany(mappedBy = "peticionario")
    private List<ExpedienteSecundario> expedienteSecundarioList;

    @OneToOne
    @JoinColumn(name = "empresa_id")
    private Empresa representa;

    public abstract String getTipoPeticionario();
}
