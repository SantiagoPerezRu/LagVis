package com.example.lagvis_v1.data.repository;

import com.example.lagvis_v1.dominio.finiquitos.CalcularFiniquitoUseCase;
import com.example.lagvis_v1.dominio.finiquitos.FiniquitoParams;
import com.example.lagvis_v1.dominio.finiquitos.FiniquitoResult;
import com.example.lagvis_v1.dominio.repositorio.FiniquitosRepository;


public class FiniquitosRepositoryImpl implements FiniquitosRepository {

    private final CalcularFiniquitoUseCase useCase;

    public FiniquitosRepositoryImpl(CalcularFiniquitoUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public Result<FiniquitoResult> calcular(FiniquitoParams params) {
        try {
            FiniquitoResult r = useCase.execute(params);
            return Result.success(r);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

