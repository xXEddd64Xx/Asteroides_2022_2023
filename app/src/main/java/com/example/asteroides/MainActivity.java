package com.example.asteroides;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Musica
    MediaPlayer mp;
    int pos;
    // Magatzem de puntuacions
    public static MagatzemPuntuacions magatzem = new MagatzemPuntuacionsList();

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(this, R.raw.menu_music);
        mp.start();


        // Declarant i assignant a una variable cada boto del menu
        Button btnJugar = findViewById(R.id.btnJugar);
        Button btnPreferencies = findViewById(R.id.btnPreferencies);
        Button btnSobre = findViewById(R.id.btnSobre);
        Button btnSortir = findViewById(R.id.btnSortir);
        Button btnPuntuacions = findViewById(R.id.btnPuntuacions);

        // Animacio pel boto Jugar del menu //
        Animation botoJugar = AnimationUtils.loadAnimation(this,
                R.anim.sequential);
        btnJugar.startAnimation(botoJugar);

        // Animacio pel boto Preferencies del menu //
        Animation botoPreferencies = AnimationUtils.loadAnimation(this,
                R.anim.sequential_2);
        btnPreferencies.startAnimation(botoPreferencies);

        // Animacio pel boto Sobre del menu //
        Animation botoSobre = AnimationUtils.loadAnimation(this,
                R.anim.sequential);
        btnSobre.startAnimation(botoSobre);

        // Animacio pel boto Puntuacions del menu //
        Animation botoPuntuacions = AnimationUtils.loadAnimation(this,
                R.anim.sequential_2);
        btnPuntuacions.startAnimation(botoPuntuacions);

        // Animacio pel boto Sortir del menu //
        Animation botoSortir = AnimationUtils.loadAnimation(this,
                R.anim.sequential);
        btnSortir.startAnimation(botoSortir);

        // Assignant onClickers als bottons per iniciar els mètodes
        btnJugar.setOnClickListener(this);
            // Boto preferencies inicia preferenices
        btnPreferencies.setOnClickListener(this);
            // Boto sobre mostra informacio de l'aplicacio
        btnSobre.setOnClickListener(this);
            // Boto puntuacions mostra les puntuacions de'ls jugadors
        btnPuntuacions.setOnClickListener(this);
            // Boto sortir tanca l'aplicacio
        btnSortir.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnJugar:
                iniciarJoc(view);
                break;
            case R.id.btnPreferencies:
                iniciarPreferencies(view);
                break;
            case R.id.btnSobre:
                iniciarSobre(view);
                break;
            case R.id.btnPuntuacions:
                iniciarPuntuacions(view);
                break;
            case R.id.btnSortir:
                finish();
                break;
            default:
                break;
                // Default secuencia de sentencias.
        }
    }

    public void iniciarJoc(View view) {
        Intent i = new Intent(this, Joc.class);
        startActivity(i);
    }

    public void iniciarPuntuacions(View view) {
        Intent i = new Intent(this, Puntuacions.class);
        startActivity(i);
    }

    public void iniciarSobre(View view) {
        Intent i = new Intent(this, Sobre.class);
        startActivity(i);
    }

    public void iniciarPreferencies(View view) {
        Intent i = new Intent(this, PreferenciesActivity.class);
        startActivity(i);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.preferencies){
            iniciarPreferencies(null); // arrancar activitat preferències
            return true;
        }
        if (id == R.id.sobre){
            iniciarSobre(null); // arrancar activitat sobre...
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void mostrarPreferencies(View view){
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String s = "Música: " + pref.getBoolean("musica",true) +
                "Grafics: " + pref.getString("grafics","?") +
                "Fragments: " + pref.getString("fragments", "?") +
                "Multijugador: " + pref.getBoolean("multijugador", true) +
                "Max. Jugadors: " + pref.getString("jugadors", "?") +
                "Tipus Connexio: " + pref.getString("connexio", "?");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    // Estats de vida de l'activitat //
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("STATE_MUSIC", pos);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pos = savedInstanceState.getInt("STATE_MUSIC");
        mp.seekTo(pos);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mp.start();
        Log.i("Cicle","Dins onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        mp.seekTo(pos);
        Log.i("Cicle","Dins onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
        pos = mp.getCurrentPosition();
        Log.i("Cicle","Dins onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        pos = mp.getCurrentPosition();
        mp.stop();
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Cicle","Dins onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Cicle","Dins onDestroy");
    }
}