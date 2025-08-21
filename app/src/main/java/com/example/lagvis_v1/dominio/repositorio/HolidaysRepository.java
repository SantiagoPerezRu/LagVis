package com.example.lagvis_v1.dominio.repositorio;

import com.example.lagvis_v1.dominio.PublicHoliday;
import java.util.List;

public interface HolidaysRepository {
    Result<List<PublicHoliday>> getHolidays(int year, String countryCode);

    final class Result<T> {
        public final T data; public final String error;
        private Result(T d, String e){ data=d; error=e; }
        public static <T> Result<T> success(T d){ return new Result<>(d, null); }
        public static <T> Result<T> error(String e){ return new Result<>(null, e); }
        public boolean isSuccess(){ return error == null; }
    }
}
