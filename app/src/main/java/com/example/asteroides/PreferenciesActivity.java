package com.example.asteroides;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class PreferenciesActivity extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenciesFragment()).commit();
        final EditTextPreference fragments = (EditTextPreference) findPreference("fragments");
        fragments.setSummary("En quants trossos es divideix un asteroide (" + pref.getString("fragments", "?") + ")");
        fragments.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        int valor;
                        try {
                            valor = Integer.parseInt(pref.getString("fragments", "?"));
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Ha de ser un número", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        System.out.println(valor);
                        return true;
                    }
                });

        /*int valor = Integer.parseInt(pref.getString("fragments", "?"));

        if (valor >= 0 && valor <= 9) {
            pref.edit().putString("fragments", pref.getString("fragments", "?"));
            pref.edit().apply();
            fragments.setSummary("En quants trossos es divideix un asteroide (" + valor + ")");
        } else {
            Toast.makeText(getApplicationContext(), "Màxim de fragments 9", Toast.LENGTH_SHORT).show();
            pref.edit().putString("fragments", "3");
            pref.edit().apply();
            fragments.setSummary("En quants trossos es divideix un asteroide (" + pref.getString("fragments", "?") + ")");
        }*/

    }

    // Estats de l'aplicacio
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Cicle","Dins onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Cicle","Dins onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Cicle","Dins onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Cicle","Dins onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final EditTextPreference fragments = (EditTextPreference) findPreference("fragments");*/

    }
}

