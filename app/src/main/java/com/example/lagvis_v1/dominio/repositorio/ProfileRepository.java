package com.example.lagvis_v1.dominio.repositorio;

import com.example.lagvis_v1.dominio.model.UserProfile;

public interface ProfileRepository {
    Result<UserProfile> fetch(String uid);  // obtener perfil por UID


    Result<Void> insert(String uid,
                        String nombre,
                        String apellido1,
                        String apellido2,
                        String comunidadId,
                        String sectorId,
                        String fechaNacimiento);

    final class Result<T> {
        public final T data; public final String error;
        private Result(T d, String e){ data=d; error=e; }
        public static <T> Result<T> success(T d){ return new Result<>(d, null); }
        public static <T> Result<T> error(String e){ return new Result<>(null, e); }
        public boolean isSuccess(){ return error == null; }
    }
}
