// com/example/lagvis_v1/dominio/repositorio/HolidaysRepository.java
package com.example.lagvis_v1.dominio.repositorio;

import com.example.lagvis_v1.dominio.PublicHoliday;
import java.util.List;

public interface HolidaysRepository {
    class Result<T> {
        public final T data;
        public final String error;
        public Result(T d, String e){ data=d; error=e; }
        public boolean isSuccess(){ return error==null; }
        public static <T> Result<T> ok(T d){ return new Result<>(d,null); }
        public static <T> Result<T> fail(String e){ return new Result<>(null,e); }
    }

    // NUEVA FIRMA:
    Result<List<PublicHoliday>> getHolidaysByProvince(int year, String provinciaSlug);
}
