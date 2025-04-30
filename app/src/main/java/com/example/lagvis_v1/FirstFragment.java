package com.example.lagvis_v1;

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
        // Inflar la vista del fragmento
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        // Instanciar el TextView
        TextView textViewError = view.findViewById(R.id.textViewError);

        Button btnSiguiente = view.findViewById(R.id.btnEnviar);

        AutoCompleteTextView autoCompleteTextViewComunidadAutonoma = view.findViewById(R.id.autoCompleteTextViewComunidadAutonoma);

        AutoCompleteTextView autoCompleteTextViewSectores = view.findViewById(R.id.autoCompleteTextViewSectores);


        /*
        *
        * Llenar las listas de comunidades autónomas y sectores laborales
        *
        * */

        String[] comunidades = getResources().getStringArray(R.array.comunidades);
        String[] sectores = getResources().getStringArray(R.array.sectores);


        ArrayAdapter<String> arrAdapterComunidadesAuto = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, comunidades);
        ArrayAdapter<String> arrAdapterSectores = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, sectores);


        autoCompleteTextViewComunidadAutonoma.setAdapter(arrAdapterComunidadesAuto);
        autoCompleteTextViewSectores.setAdapter(arrAdapterSectores);

        autoCompleteTextViewComunidadAutonoma.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(autoCompleteTextViewComunidadAutonoma.getText().toString().isEmpty() || autoCompleteTextViewSectores.getText().toString().isEmpty()){
                    textViewError.setVisibility(View.VISIBLE);
                }else {
                    textViewError.setVisibility(View.INVISIBLE);
                }

            }
        });




        return view;
    }

}