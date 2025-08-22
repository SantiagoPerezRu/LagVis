package com.example.lagvis_v1.data.repository;

import androidx.annotation.NonNull;

import com.example.lagvis_v1.data.remote.HolidaysApi;
import com.example.lagvis_v1.data.remote.HolidaysResponse;
import com.example.lagvis_v1.dominio.PublicHoliday;
import com.example.lagvis_v1.dominio.repositorio.HolidaysRepository;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import retrofit2.Response;

public class HolidaysRepositoryImpl implements HolidaysRepository {

    private final HolidaysApi api;

    public HolidaysRepositoryImpl(@NonNull HolidaysApi api) {
        this.api = api;
    }

    @Override
    public Result<List<PublicHoliday>> getHolidaysByProvince(int year, String provinciaSlug) {
        if (provinciaSlug == null || provinciaSlug.trim().isEmpty()) {
            return Result.fail("slug/provincia vacío");
        }
        try {
            // scope = null → servidor devuelve TODOS y ya coloreas en cliente por h.scope
            Response<HolidaysResponse> resp = api.getHolidays(provinciaSlug, year).execute();

            if (resp.isSuccessful() && resp.body() != null) {
                List<PublicHoliday> items = resp.body().getHolidays();
                if (items == null) items = Collections.emptyList();
                return Result.ok(items);
            } else {
                int code = resp != null ? resp.code() : -1;
                String msg = resp != null && resp.errorBody() != null ? resp.errorBody().string() : "sin cuerpo";
                return Result.fail("HTTP " + code + ": " + msg);
            }
        } catch (UnknownHostException e) {
            return Result.fail("Sin conexión: " + e.getMessage());
        } catch (SocketTimeoutException e) {
            return Result.fail("Timeout: " + e.getMessage());
        } catch (IOException e) {
            return Result.fail("IO: " + e.getMessage());
        } catch (Exception e) {
            return Result.fail("Error: " + e.getMessage());
        }
    }
}
