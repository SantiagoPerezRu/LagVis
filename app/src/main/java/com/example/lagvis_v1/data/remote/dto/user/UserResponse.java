package com.example.lagvis_v1.data.remote.dto.user;

import java.util.List;

public class UserResponse {
    public String exito;           // "1" en éxito
    public String mensaje;         // opcional
    public List<UserDataDto> datos;
}
