package com.example.lagvis_v1.ui.convenio;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lagvis_v1.core.ui.UiState;
import com.example.lagvis_v1.core.util.LagVisConstantes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
public class ConvenioSelectorViewModel extends ViewModel {

    public static class NavData {
        public final String archivo;
        public final int comunidadId;
        public final int sectorId;
        public NavData(String a, int cId, int sId){ archivo=a; comunidadId=cId; sectorId=sId; }
    }

    private final Set<String> sectoresEstatales = new HashSet<>(Arrays.asList(
            "Call Center","Centros Enseñanza Privada","Seguridad Privada",
            "Textil y Confección","Perfumería","Estaciones de Servicio"
    ));

    private final MutableLiveData<UiState<NavData>> _nav = new MutableLiveData<>();
    public LiveData<UiState<NavData>> nav = _nav;

    public void onSiguiente(String comunidad, String sector) {
        boolean esEstatal = sectoresEstatales.contains(sector);

        if ((isEmpty(sector)) || (!esEstatal && isEmpty(comunidad))) {
            _nav.postValue(new UiState.Error<>("Debes seleccionar un sector y, si no es estatal, una comunidad."));
            return;
        }

        int comunidadId = LagVisConstantes.getComunidadId(comunidad);
        int sectorId    = LagVisConstantes.getSectorId(sector);

        String comunidadFinal = esEstatal ? "estatal" : comunidad;
        String archivo = buildNombreArchivo(contenidoSafe(comunidadFinal), contenidoSafe(sector));
        if (archivo == null) {
            _nav.postValue(new UiState.Error<>("No se encontró un convenio para esta combinación."));
        } else {
            _nav.postValue(new UiState.Success<>(new NavData(archivo, comunidadId, sectorId)));
        }
    }

    private boolean isEmpty(String s){ return s == null || s.trim().isEmpty(); }
    private String contenidoSafe(String s){ return s == null ? "" : s.trim(); }

    private String buildNombreArchivo(String comunidad, String sector) {
        String comunidadSimplificada = simplificarNombreComunidad(comunidad);
        String nombreArchivo = (comunidadSimplificada + "_" + sector).toLowerCase()
                .replace("á","a").replace("é","e").replace("í","i")
                .replace("ó","o").replace("ú","u").replace("ñ","n")
                .replace(" ", "_");

        // El Fragment validará si existe el recurso; si quieres, puedes pasar un checker por ctor
        return nombreArchivo + ".xml";
    }

    private String simplificarNombreComunidad(String comunidad) {
        switch (comunidad) {
            case "Comunidad de Madrid": return "madrid";
            case "Comunidad Valenciana": return "valencia";
            case "Illes Balears": return "baleares";
            case "País Vasco": return "pais_vasco";
            case "Andalucía": return "andalucia";
            case "Aragón": return "aragon";
            case "Asturias": return "asturias";
            case "Cantabria": return "cantabria";
            case "Castilla-La Mancha": return "castilla_la_mancha";
            case "Castilla y León": return "castilla_y_leon";
            case "Cataluña": return "cataluna";
            case "Extremadura": return "extremadura";
            case "Galicia": return "galicia";
            case "Canarias": return "canarias";
            case "La Rioja": return "la_rioja";
            case "Región de Murcia": return "murcia";
            case "Navarra": return "navarra";
            default: return comunidad.toLowerCase().replace(" ", "_");
        }
    }
}

