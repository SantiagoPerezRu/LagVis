package com.example.lagvis_v1.data.repository;

import com.example.lagvis_v1.core.util.LagVisConstantes;
import com.example.lagvis_v1.data.mapper.ProfileMappers;
import com.example.lagvis_v1.data.remote.ProfileApi;
import com.example.lagvis_v1.data.remote.UserResponseDto;
import com.example.lagvis_v1.dominio.UserProfile;
import com.example.lagvis_v1.dominio.repositorio.ProfileRepository;

import java.io.IOException;

import retrofit2.Response;

public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfileApi api;

    public ProfileRepositoryImpl(ProfileApi api) {
        this.api = api;
    }

    @Override
    public Result<Void> insert(String uid,
                               String nombre,
                               String apellido1,
                               String apellido2,
                               String comunidadId,
                               String sectorId,
                               String fechaNacimiento) {
        try {
            Response<Void> r = api.insert(
                    LagVisConstantes.ENDPOINT_INSERTAR, // URL completa que ya usabas con Volley
                    uid, nombre, apellido1, apellido2, comunidadId, sectorId, fechaNacimiento
            ).execute();

            return r.isSuccessful() ? Result.success(null)
                    : Result.error("HTTP " + r.code());
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result<UserProfile> fetch(String uid) {
        try {
            Response<UserResponseDto> r = api.show(LagVisConstantes.ENDPOINT_MOSTRAR, uid).execute();
            if (r.isSuccessful() && r.body()!=null && "1".equals(r.body().exito)) {
                UserProfile p = ProfileMappers.toDomain(r.body());
                if (p != null) return Result.success(p);
                return Result.error("Sin datos");
            } else {
                return Result.error("HTTP " + r.code());
            }
        } catch (IOException e){
            return Result.error(e.getMessage());
        }
    }
}
