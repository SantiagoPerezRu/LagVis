// com/example/lagvis_v1/dominio/UserProfile.java
package com.example.lagvis_v1.dominio.model;

public class UserProfile {
    public final String nombre, apellido, apellido2, sectorLaboral, comunidadAutonoma, fechaNacimiento;

    public UserProfile(String nombre, String apellido, String apellido2,
                       String sectorLaboral, String comunidadAutonoma, String fechaNacimiento) {
        this.nombre = nombre != null ? nombre : "";
        this.apellido = apellido != null ? apellido : "";
        this.apellido2 = apellido2 != null ? apellido2 : "";
        this.sectorLaboral = sectorLaboral != null ? sectorLaboral : "";
        this.comunidadAutonoma = comunidadAutonoma != null ? comunidadAutonoma : "";
        this.fechaNacimiento = fechaNacimiento != null ? fechaNacimiento : "";
    }
}
