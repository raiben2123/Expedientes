package com.ruben.Expedientes.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PeticionarioDNIDTO extends PeticionarioDTO {
    private String dni;

    public PeticionarioDNIDTO(Long id, String name, String surname, String address, String tlf, String email, Long aLong, Object o, Object o1, String dni) {
    }
}
