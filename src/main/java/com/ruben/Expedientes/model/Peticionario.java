package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_peticionario")
@Data
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
}
