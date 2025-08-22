// com/example/lagvis_v1/data/mapper/ProfileMappers.java
package com.example.lagvis_v1.data.mapper;

import com.example.lagvis_v1.data.remote.UserDataDto;
import com.example.lagvis_v1.data.remote.UserResponseDto;
import com.example.lagvis_v1.dominio.UserProfile;

public final class ProfileMappers {
    private ProfileMappers(){}

    public static UserProfile toDomain(UserResponseDto dto){
        if (dto == null || dto.datos == null || dto.datos.isEmpty()) return null;
        UserDataDto d = dto.datos.get(0);
        return new UserProfile(
            d.nombre, d.apellido, d.apellido2,
            d.sector_laboral, d.comunidad_autonoma, d.fecha_nacimiento
        );
    }
}
