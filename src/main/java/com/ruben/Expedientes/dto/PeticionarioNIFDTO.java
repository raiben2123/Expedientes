package com.ruben.Expedientes.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PeticionarioNIFDTO extends PeticionarioDTO {

    private String nif;

    @Override
    public String getTipoDocumento() {
        return "NIF";
    }

    @Override
    public String getNumeroDocumento() {
        return nif;
    }

    // Constructor de conveniencia
    public PeticionarioNIFDTO(String name, String surname, String nif) {
        super();
        setName(name);
        setSurname(surname);
        this.nif = nif;
    }
}