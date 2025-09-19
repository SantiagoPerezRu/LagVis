package com.example.lagvis_v1.dominio

import com.google.gson.annotations.SerializedName
import java.text.Normalizer
import java.util.Locale

data class PublicHolidayKt(
    @SerializedName("date") var date: String? = null,
    @SerializedName("name") var name: String? = null
) {

    @SerializedName("scope", alternate = ["tyoe"])
    var scope: String? = null

    @SerializedName("province")
    var province: String? = null

    @SerializedName("autonomy", alternate = ["ccaa", "autonomia", "community"])
    var autonomy: String? = null

    // Si trae nulo que devuelva name
    @SerializedName(value = "localName", alternate = ["local_name", "title"])
    var localName: String? = null
        get() = field ?: name
        set(value) {
            field = value
        }

    val scopeNormalized: String
        get() {
            val t = scope?.let {
                Normalizer.normalize(it, Normalizer.Form.NFD)
                    .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                    .lowercase(Locale.ROOT)
                    .trim()
            } ?: return "otros"
            return when {
                t == "estatal" -> "nacional"
                t.startsWith("autonom") -> "autonomico"
                t.startsWith("municip") -> "municipal"
                t.startsWith("local") || t.startsWith("provinc") -> "local"
                t.startsWith("info") -> "info"
                t in setOf("nacional", "autonomico", "municipal", "local", "info") -> t
                else -> "otros"
            }
        }

    override fun toString(): String {
        return "PublicHolidayKt(date=$date, name=$name, scope=$scope, province=$province, autonomy=$autonomy, localName=$localName, scopeNormalized='$scopeNormalized')"
    }


}