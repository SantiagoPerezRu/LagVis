package com.example.lagvis_v1.data.repository;

import com.example.lagvis_v1.data.mapper.HolidaysMappers;
import com.example.lagvis_v1.data.remote.HolidaysApi;
import com.example.lagvis_v1.data.remote.PublicHolidayDto;
import com.example.lagvis_v1.dominio.PublicHoliday;
import com.example.lagvis_v1.dominio.repositorio.HolidaysRepository;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class HolidaysRepositoryImpl implements HolidaysRepository {
    private final HolidaysApi api;

    public HolidaysRepositoryImpl(HolidaysApi api){ this.api = api; }

    @Override
    public Result<List<PublicHoliday>> getHolidays(int year, String countryCode) {
        try {
            Response<List<PublicHolidayDto>> r = api.getHolidays(year, countryCode).execute();
            if (r.isSuccessful() && r.body()!=null) {
                return Result.success(HolidaysMappers.toDomain(r.body()));
            } else {
                return Result.error("HTTP " + r.code());
            }
        } catch (IOException e){
            return Result.error(e.getMessage());
        }
    }
}
