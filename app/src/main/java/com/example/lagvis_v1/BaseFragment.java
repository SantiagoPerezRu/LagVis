package com.example.lagvis_v1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.Gravity;
import androidx.fragment.app.Fragment;
import android.graphics.drawable.Drawable;
import android.content.Context; 

public class BaseFragment extends Fragment {

    private Context context; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  inicialización común a todos los fragments.
        context = getContext(); 
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Muestra un Toast personalizado.
     * @param message El mensaje a mostrar.
     * @param icon El Drawable para el icono.
     */
    public void mostrarToastPersonalizado(String message, Drawable icon) {
        if (context == null) return; // Comprueba si el contexto es nulo
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) getView().findViewById(R.id.custom_toast_container)); // Asegúrate de que el ID es correcto
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);
        ImageView toastIcon = (ImageView) layout.findViewById(R.id.toast_icon);

        if (icon != null) {
            toastIcon.setImageDrawable(icon);
            toastIcon.setVisibility(View.VISIBLE);
        } else {
            toastIcon.setVisibility(View.GONE);
        }
        Toast toast = new Toast(context); 
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context; 
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }
}

