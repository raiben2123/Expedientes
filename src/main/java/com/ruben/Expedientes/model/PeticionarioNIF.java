package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("NIF")
@Getter
@Setter
@NoArgsConstructor
public class PeticionarioNIF extends Peticionario {

    @Override
    public String getTipoPeticionario() {
        return "NIF";
    }

    // Constructor específico para NIF
    public PeticionarioNIF(String name, String surname, String nif) {
        super();
        setName(name);
        setSurname(surname);
        setNif(nif);
        setActive(true);
    }

    // Validación específica para NIF
    @Override
    public void setNif(String nif) {
        // Limpiar el DNI ya que este es un NIF
        setDni(null);
        super.setNif(nif);
    }

    // Método de validación de NIF (para extranjeros)
    public boolean isValidNif() {
        String nif = getNif();
        if (nif == null || nif.length() != 9) {
            return false;
        }

        // NIF debe empezar con una letra específica para extranjeros
        char firstChar = Character.toUpperCase(nif.charAt(0));
        String validFirstLetters = "XYZKLM";

        if (!validFirstLetters.contains(String.valueOf(firstChar))) {
            return false;
        }

        try {
            // Extraer números y última letra
            String numbers = nif.substring(1, 8);
            char letter = nif.charAt(8);

            // Validar que los números son números
            Integer.parseInt(numbers);

            // Para una validación completa del NIF se necesitarían más reglas específicas
            // Este es un ejemplo básico
            return Character.isLetter(letter);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}