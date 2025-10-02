/*package com.example.lagvis_v1.ui.main;

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

import com.example.lagvis_v1.R;

import com.example.lagvis_v1.ui.calendario.CalendarioLaboralKt;
import com.example.lagvis_v1.ui.convenio.ConvenioSelectorOnCompose;
import com.example.lagvis_v1.ui.finiquitos.CalculadoraFiniquitosFragment;
import com.example.lagvis_v1.ui.convenio.ConvenioSelectorFrag;
import com.example.lagvis_v1.ui.news.NewsViewFragKt;
import com.example.lagvis_v1.ui.news.NoticiasGuardadasFragmentKt;
import com.example.lagvis_v1.ui.profile.ProfileOnCompose;
import com.google.android.material.navigation.NavigationView;

import com.example.lagvis_v1.ui.despidos.DatosGeneralesDespidoFragment;
import VisualizadorPaginas.PaginaVidaLaboralFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FrameLayout frameLayout;

    ConvenioSelectorFrag convenioSelectorFrag = new ConvenioSelectorFrag();     // Inicio
    NewsViewFragKt newsViewFrag = new NewsViewFragKt();  // Noticias
    CalculadoraFiniquitosFragment calculadoraFiniquitosFragment = new CalculadoraFiniquitosFragment();     // Finiquitos
    ProfileOnCompose profileViewFrag = new ProfileOnCompose();  // Tú perfil
    DatosGeneralesDespidoFragment fifhtFragment = new DatosGeneralesDespidoFragment(); // Despidos
    NoticiasGuardadasFragmentKt fragmentNoticiasGuaradas = new NoticiasGuardadasFragmentKt(); // Noticias guardadas fragment
    PaginaVidaLaboralFragment fragmentVidaLaboral = new PaginaVidaLaboralFragment(); // Vida Laboral Fragment
    CalendarioLaboralKt fragmentCalendarioLaboral = new CalendarioLaboralKt(); // Calendario laboral fragment


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
            loadFragment(convenioSelectorFrag);
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
                startActivity(new Intent(this, ConvenioSelectorOnCompose.class));
                break;
            case R.id.notice:
                loadFragment(newsViewFrag);
                break;
            case R.id.calc:
                loadFragment(calculadoraFiniquitosFragment);
                break;
            case R.id.settings:
                startActivity(new Intent(this, ProfileOnCompose.class));
                break;
            case R.id.nav_vida_laboral:
                loadFragment(fragmentVidaLaboral);
                break;
            case R.id.nav_calculadoraDespidos:
                loadFragment(fifhtFragment);
                break;
            case R.id.noticiasGuardadas:
                loadFragment(fragmentNoticiasGuaradas);
                break;
            case R.id.calendarioLaboral:
                loadFragment(fragmentCalendarioLaboral);
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
}*/