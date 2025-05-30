package com.example.lagvis_v1;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final Set<String> sectoresEstatales = new HashSet<>(Arrays.asList(
            "Call Center",
            "Conservas de Pescado",
            "Química",
            "Calzado",
            "Textil y Confección",
            "Perfumería",
            "Estaciones de Servicio"
    ));



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Cargamos la vista del fragmento
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        // Instanciar objetos necesarios.
        TextView textViewError = view.findViewById(R.id.textViewError);
        Button btnSiguiente = view.findViewById(R.id.btnEnviar);
        AutoCompleteTextView autoCompleteTextViewComunidades = view.findViewById(R.id.autoCompleteTextViewComunidades);
        AutoCompleteTextView autoCompleteTextViewSectores = view.findViewById(R.id.autoCompleteTextViewSectores);



        /*
        *
        * Cargamos las listas de comunidades y sectores laborales
        *
        * */

        String[] provinicias = getResources().getStringArray(R.array.comunidades_autonomas);
        String[] sectores = getResources().getStringArray(R.array.sectores);

        /*
        *
        * En esta parte del código hacemos que los autoCompleteTextView se llenen con las comunidades autónomas y sectores laborales.
        * Primero crearemos los adaptadores, se los mandaremos a los autoCompleteTexView
        * y cuando hagamos click se desplegaran en forma de lista donde el usuario pueda elegir
        *
        */
        ArrayAdapter<String> arrAdapterComunidades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, provinicias);
        ArrayAdapter<String> arrAdapterSectores = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, sectores);
        autoCompleteTextViewComunidades.setAdapter(arrAdapterComunidades);
        autoCompleteTextViewSectores.setAdapter(arrAdapterSectores);

        autoCompleteTextViewComunidades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });

        autoCompleteTextViewSectores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });

        /*
        *
        * Al pulsar en siguiente consguimos comunidades y sectores y se lo mandamos a la clase Convenio.
        * Esta será la que reproduce los convenios segun los datos que le hemos mandado en el intent.
        * Revisa que si es Estatal no tengas la neceisdad de poner comunidad autonoma
        *
         */
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comunidades = autoCompleteTextViewComunidades.getText().toString();
                String sector = autoCompleteTextViewSectores.getText().toString();

                boolean esEstatal = sectoresEstatales.contains(sector);

                if ((comunidades.isEmpty() && !esEstatal) || sector.isEmpty()) {
                    textViewError.setVisibility(View.VISIBLE);
                    textViewError.setText("Debes seleccionar un sector y, si no es estatal, una comunidad.");
                } else {
                    textViewError.setVisibility(View.INVISIBLE);

                    // Si es estatal, le pasamos una comunidad genérica como "estatal"
                    String comunidadFinal = esEstatal ? "estatal" : comunidades;

                    String nombreArchivo = obtenerNombreArchivoConvenio(comunidadFinal, sector);

                    if (nombreArchivo == null) {
                        textViewError.setText("No se encontró un convenio para esta combinación.");
                        textViewError.setVisibility(View.VISIBLE);
                    } else {
                        Intent i = new Intent(requireActivity(), Convenio.class);
                        i.putExtra("archivo_convenio", nombreArchivo); // <- Lo pasas a la siguiente actividad
                        startActivity(i);
                    }
                }
            }
        });

        return view;
    }


    /*





    /**
     * Consigue el nombre del archivo XML del convenio según la comunidad y el sector.
     * Junta la comunidad y el sector en minúsculas y cambia las letras con acentos o la 'ñ' y los espacios por guiones bajos.
     * Después mira si existe un archivo con ese nombre (.xml) en la carpeta 'raw'.
     *
     * @param comunidades La comunidad que eligió el usuario.
     * @param sector      El sector de trabajo que eligió el usuario.
     * @return El nombre completo del archivo XML
     *
     * Simplificamos el nombre para evitar problemas con Comunidades como Comunidad de Madrid, Valenciana ETC...
     */
    private String obtenerNombreArchivoConvenio(String comunidad, String sector) {
        String comunidadSimplificada = simplificarNombreComunidad(comunidad);
        String nombreArchivo = (comunidadSimplificada + "_" + sector).toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n")
                .replace(" ", "_");;


        // Conseguimos el ID  del recurso osea de el XML con los datos del convenio
        int resId = getResources().getIdentifier(nombreArchivo.replace(".xml", ""), "raw", requireContext().getPackageName());

        if (resId == 0) {
            return null; // No existe el archivo
        } else {
            return nombreArchivo + ".xml"; // Devuelve el nombre del archivo
        }
    }


    /**
     * Coge el nombre de la comunidad autónoma tal cual y lo apaña un poco
     * para que quede más cortito y sin espacios, listo para usarlo en el nombre del archivo.
     * Si es "Comunidad de Madrid" lo deja en "madrid", si es "Comunidad Valenciana" en "valencia",
     * y así con las Islas Baleares. Si no es ninguna de esas, pues lo pone todo en minúsculas
     * y cambia los espacios por guiones bajos.
     *
     * @param comunidad El nombre de la comunidad tal cual lo seleccionó el usuario.
     * @return El nombre de la comunidad más simple y sin espacios (con guiones bajos si hacía falta).
     */
    private String simplificarNombreComunidad(String comunidad) {
        String nombreSimplificado;
        switch (comunidad) {
            case "Comunidad de Madrid":
                nombreSimplificado = "madrid";
                break;
            case "Comunidad Valenciana":
                nombreSimplificado = "valencia";
                break;
            case "Illes Balears":
                nombreSimplificado = "baleares";
                break;
            case "País Vasco":
                nombreSimplificado = "pais_vasco";
                break;
            case "Andalucía":
                nombreSimplificado = "andalucia";
                break;
            case "Aragón":
                nombreSimplificado = "aragon";
                break;
            case "Asturias":
                nombreSimplificado = "asturias";
                break;
            case "Cantabria":
                nombreSimplificado = "cantabria";
                break;
            case "Castilla-La Mancha":
                nombreSimplificado = "castilla_la_mancha";
                break;
            case "Castilla y León":
                nombreSimplificado = "castilla_y_leon";
                break;
            case "Cataluña":
                nombreSimplificado = "cataluna";
                break;
            case "Extremadura":
                nombreSimplificado = "extremadura";
                break;
            case "Galicia":
                nombreSimplificado = "galicia";
                break;
            case "Canarias":
                nombreSimplificado = "canarias";
                break;
            case "La Rioja":
                nombreSimplificado = "la_rioja";
                break;
            case "Región de Murcia":
                nombreSimplificado = "murcia";
                break;
            case "Navarra":
                nombreSimplificado = "navarra";
                break;
            default:
                nombreSimplificado = comunidad.toLowerCase().replace(" ", "_");
                break;
        }
        return nombreSimplificado;
    }

}