package com.ruben.Expedientes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("DNI")
@Getter
@Setter
@NoArgsConstructor
public class PeticionarioDNI extends Peticionario {

    @Override
    public String getTipoPeticionario() {
        return "DNI";
    }

    // Constructor específico para DNI
    public PeticionarioDNI(String name, String surname, String dni) {
        super();
        setName(name);
        setSurname(surname);
        setDni(dni);
        setActive(true);
    }

    // Validación específica para DNI español
    @Override
    public void setDni(String dni) {
        // Limpiar el NIF ya que este es un DNI
        setNif(null);
        super.setDni(dni);
    }

    // Método de validación de DNI (se puede llamar antes de guardar)
    public boolean isValidDni() {
        String dni = getDni();
        if (dni == null || dni.length() != 9) {
            return false;
        }

        try {
            String numbers = dni.substring(0, 8);
            char letter = dni.charAt(8);
            String validLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
            int position = Integer.parseInt(numbers) % 23;
            return validLetters.charAt(position) == Character.toUpperCase(letter);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}