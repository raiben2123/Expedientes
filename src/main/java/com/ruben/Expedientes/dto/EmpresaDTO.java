package com.ruben.Expedientes.dto;

import com.ruben.Expedientes.model.Peticionario;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmpresaDTO {
    private Long id;
    private String cif;
    private String name;
    private String address;
    private String tlf;
    private String email;
    private Long representanteId; // Solo el ID, para evitar sobrecarga de datos
    private List<Long> expedientePrincipalList; // Solo el ID, para evitar sobrecarga de datos
    private List<Long> expedienteSecundarioList; // Solo el ID, para evitar sobrecarga de datos

    public EmpresaDTO(Long id, String cif, String name, String address, String tlf, String email, Long aLong) {
    }
}
