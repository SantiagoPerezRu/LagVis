package com.example.lagvis_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;

import FiniquitosPackage.ActivityDatosGeneralesDespido;
import VisualizadorPaginas.ActivityPaginaVidaLaboral;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FrameLayout frameLayout;

    FirstFragment firstFragment = new FirstFragment();     // Inicio
    SecondFragment secondFragment = new SecondFragment();  // Noticias
    ThirdFragment thirdFragment = new ThirdFragment();     // Finiquitos
    FourthFragment fourthFragment = new FourthFragment();  // Tú perfil

    NoticiasGuardadasFragment fragmentNoticiasGuaradas = new NoticiasGuardadasFragment(); // Noticias guardadas fragment
    ActivityPaginaVidaLaboral activityNavegador = new ActivityPaginaVidaLaboral(); // Vida Laboral

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        frameLayout = findViewById(R.id.frame_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Cargar FirstFragment automáticamente al iniciar
        if (savedInstanceState == null) {
            loadFragment(firstFragment);
            navigationView.setCheckedItem(R.id.home); // marcar como seleccionado en el menú lateral
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                loadFragment(firstFragment);
                break;
            case R.id.notice:
                loadFragment(secondFragment);
                break;
            case R.id.calc:
                loadFragment(thirdFragment);
                break;
            case R.id.settings:
                loadFragment(fourthFragment);
                break;
            case R.id.nav_vida_laboral:
                Intent intentNavegador = new Intent(this, ActivityPaginaVidaLaboral.class);
                startActivity(intentNavegador);
                break;
            case R.id.nav_calculadoraDespidos:
                Intent intentCalcDespidos = new Intent(this, ActivityDatosGeneralesDespido.class);
                startActivity(intentCalcDespidos);
                break;
            case R.id.noticiasGuardadas:
                loadFragment(fragmentNoticiasGuaradas);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}