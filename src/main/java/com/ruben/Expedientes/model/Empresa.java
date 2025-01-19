package com.ruben.Expedientes.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cif;
    private String name;
    private String address;
    private String tlf;
    private String email;

    @OneToOne(mappedBy = "representa", cascade = CascadeType.ALL)
    private Peticionario representante;

    @OneToMany(mappedBy = "empresa")
    private List<ExpedientePrincipal> expedientePrincipalList;

    @OneToMany(mappedBy = "empresa")
    private List<ExpedienteSecundario> expedienteSecundarioList;

    public Empresa(Long id, String cif, String name, String address, String tlf, String email) {
    }

    public Empresa(Long empresaId) {
    }
}
