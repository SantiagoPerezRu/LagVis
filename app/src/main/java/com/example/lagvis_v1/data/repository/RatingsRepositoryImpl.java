package com.example.lagvis_v1.data.repository;

import com.example.lagvis_v1.core.util.LagVisConstantes;
import com.example.lagvis_v1.data.remote.RatingsApi;
import com.example.lagvis_v1.dominio.repositorio.RatingsRepository;

import java.io.IOException;

import retrofit2.Response;

public class RatingsRepositoryImpl implements RatingsRepository {
    private final RatingsApi api;

    public RatingsRepositoryImpl(RatingsApi api) { this.api = api; }

    @Override
    public Result<Void> rate(int convenioId, String userId, int puntuacion) {
        try {
            Response<Void> r = api.submit(
                    LagVisConstantes.ENDPOINT_INSERTAR_VALORACION,
                    convenioId, userId, puntuacion
            ).execute();
            return r.isSuccessful() ? Result.success(null) : Result.error("HTTP " + r.code());
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
    }
}
