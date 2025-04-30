package com.example.lagvis_v1;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    FirstFragment firstFragment = new FirstFragment();     // Inicio
    SecondFragment secondFragment = new SecondFragment();  // Noticias
    ThirdFragment thirdFragment = new ThirdFragment();     // Finiquitos
    FourthFragment fourthFragment = new FourthFragment();  // Tú perfil

    BottomNavigationView menNavegacion;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menNavegacion = findViewById(R.id.menuNavegacion);
        frameLayout = findViewById(R.id.frame_layout);

        // ✅ Cargar FirstFragment automáticamente al iniciar
        if (savedInstanceState == null) {
            loadFragment(firstFragment);
            menNavegacion.setSelectedItemId(R.id.home); // marcar como seleccionado
        }

        menNavegacion.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            loadFragment(firstFragment);
                            return true;
                        case R.id.notice:
                            loadFragment(secondFragment);
                            return true;
                        case R.id.calc:
                            loadFragment(thirdFragment);
                            return true;
                        case R.id.settings:
                            loadFragment(fourthFragment);
                            return true;
                    }
                    return false;
                }
            };

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}
