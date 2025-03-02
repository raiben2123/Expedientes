package com.ruben.Expedientes.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PeticionarioDNIDTO extends PeticionarioDTO {

    private String dni;

    @Override
    public String getTipoDocumento() {
        return "DNI";
    }

    @Override
    public String getNumeroDocumento() {
        return dni;
    }

    // Constructor de conveniencia
    public PeticionarioDNIDTO(String name, String surname, String dni) {
        super();
        setName(name);
        setSurname(surname);
        this.dni = dni;
    }
}