data class UserResponseKt(
    val exito: String?,                 // "1" en éxito
    val mensaje: String?,               // mensaje opcional
    val datos: List<UserDataDto?>? = null
)

data class UserDataDto(
    val uid: String? = null
    // añade campos si te los devuelve
)