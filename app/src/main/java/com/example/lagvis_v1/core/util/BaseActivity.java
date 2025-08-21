package com.example.lagvis_v1.core.util;

import android.os.Bundle;
import android.widget.Toast;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;

import com.example.lagvis_v1.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  inicialización común a todas las actividades.
    }

    /**
     * Muestra un Toast personalizado.
     * @param message El mensaje a mostrar.
     * @param icon El Drawable para el icono.
     */
    public void showCustomToast(String message, Drawable icon) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);
        ImageView toastIcon = (ImageView) layout.findViewById(R.id.toast_icon);

        if (icon != null) {
            toastIcon.setImageDrawable(icon);
            toastIcon.setVisibility(View.VISIBLE);
        } else {
            toastIcon.setVisibility(View.GONE);
        }
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100); 
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}

