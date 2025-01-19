package com.ruben.Expedientes.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PeticionarioNIFDTO extends PeticionarioDTO {
    private String nif;

    public PeticionarioNIFDTO(Long id, String name, String surname, String address, String tlf, String email, Long aLong, Object o, Object o1, String nif) {
    }
}
