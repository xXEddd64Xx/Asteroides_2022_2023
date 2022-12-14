package com.example.asteroides;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class Joc extends AppCompatActivity {
    private VistaJoc vistaJoc;

    // Preferencies //
    SharedPreferences pref;

    // Musica //
    MediaPlayer mp;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joc);
        vistaJoc = (VistaJoc) findViewById(R.id.VistaJoc);
        pref = PreferenceManager.getDefaultSharedPreferences(vistaJoc.getContext());

        if (pref.getBoolean("musica", false) == true) {
            switch (pref.getString("songs", "0")) {
                case "0":
                    mp = MediaPlayer.create(this, R.raw.musica_vistajoc_espacial_0);
                    break;
                case "1":
                    mp = MediaPlayer.create(this, R.raw.musica_vistajoc_espacial_1);
                    break;
                case "2":
                    mp = MediaPlayer.create(this, R.raw.musica_vistajoc_espacial_2);
                    break;
            }
            mp.start();
        }
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
        if (pref.getBoolean("musica", false) == true) {
            mp.start();
        }
        Log.i("Cicle","Dins onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Control de musica //
        if (pref.getBoolean("musica", false) == true) {
            mp.seekTo(pos);
        }

        // Control de sensors //
        if (pref.getString("controls", "1").equals("2")) {
            vistaJoc.activarSensors(vistaJoc.getContext());
        }
        vistaJoc.getThread().reprendre();
        Log.i("Cicle","Dins onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Control de musica //
        if (pref.getBoolean("musica", false) == true) {
            mp.pause();
            pos = mp.getCurrentPosition();
        }

        // Control de sensors //
        if (pref.getString("controls", "1").equals("2")) {
            vistaJoc.desactivarSensors();
        }
        vistaJoc.getThread().pausar();
        Log.i("Cicle","Dins onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Control de musica //
        if (pref.getBoolean("musica", false) == true) {
            pos = mp.getCurrentPosition();
            mp.stop();
        }

        // Control de sensors //
        if (pref.getString("controls", "1").equals("2")) {
            vistaJoc.desactivarSensors();
        }
        Log.i("Cicle","Dins onStop");
    }
    @Override
    protected void onDestroy() {
        if (pref.getString("controls", "1").equals("2")) {
            vistaJoc.desactivarSensors();
        }
        vistaJoc.getThread().detenir();
        super.onDestroy();
        Log.i("Cicle","Dins onDestroy");
    }
}