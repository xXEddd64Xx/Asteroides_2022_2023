package com.example.asteroides;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class VistaJoc extends View implements SensorEventListener {
//////////// Per control amb sensors /////////////////////////////////
    private boolean hihaValorInicial = false;
    private float valorInicial = 0;
    SensorManager mSensorManager;
//////////////////////////////////////////////////////////////////////

//////////// Per controls tatils /////////////////////////////////////
    float mX = 0;
    float mY = 0;
    boolean dispar = false;
    boolean tactilActiu = false;
//////////////////////////////////////////////////////////////////////

//////////// Per control teclat //////////////////////////////////////
    boolean teclatActiu = false;
    boolean processada = false;
//////////////////////////////////////////////////////////////////////

//////////// NAU /////////////////////////////////////////////////////
    private Grafic nau; // Gràfic de la nau
    /*private Grafic nau2;*/
    private int girNau; // Angle de gir de la nau
    private float acceleracioNau; // Augment de velocitat

    // Increment estàndar de gir i acceleració //
    private static final int PAS_GIR_NAU = 5;
    private static final float PAS_ACCELERACIO_NAU = 0.5f;
    private static final double MAX_VELOCITAT_NAU = 50;
//////////////////////////////////////////////////////////////////////

//////////// ASTEROIDES //////////////////////////////////////////////
    private List<Grafic> Asteroides; // Vector amb els Asteroides
    private int numAsteroides = 5; // Número inicial d'asteroides
    private int numFragments = 3; // Fragments en que es divideix
//////////////////////////////////////////////////////////////////////

//////////// MISIL ///////////////////////////////////////////////////
    /*private Grafic missil;*/
    private List<Grafic> Missils;
    private static int PAS_VELOCITAT_MISSIL = 12;
    /*private boolean missilActiu = false;*/
    /*private int tempsMissil;*/
    private List <Integer> tempsMissils = new ArrayList<>();
    private Drawable drawableMissil;
//////////////////////////////////////////////////////////////////////

//////////// THREAD I TEMPS //////////////////////////////////////////
    // Thread encarregat de processar el joc
    private ThreadJoc thread = new ThreadJoc();
    // Cada quan volem processar canvis (ms)
    private static int PERIODE_PROCES = 50;
    // Quan es va realitzar el darrer procés
    private long darrerProces = 0;
//////////////////////////////////////////////////////////////////////
    
    public VistaJoc(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawableNave, drawableAsteroide;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Control per sensors //
        if (pref.getString("controls", "1").equals("2")) {
            tactilActiu = false;
            teclatActiu = false;
            activarSensors(context);
        } else if (pref.getString("controls", "1").equals("1")) {
            tactilActiu = true;
            teclatActiu = false;
            desactivarSensors();
        } else if (pref.getString("controls", "1").equals("0")) {
            tactilActiu = false;
            teclatActiu = true;
            desactivarSensors();
        }

        // Instanciam la variable drawableAsteroide segons les preferencies//
        if (pref.getString("grafics", "1").equals("0")) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            // Path Missil Vectorial //
            ShapeDrawable dMissil = new ShapeDrawable(new RectShape());
            dMissil.getPaint().setColor(Color.WHITE);
            dMissil.getPaint().setStyle(Paint.Style.STROKE);
            dMissil.setIntrinsicWidth(15);
            dMissil.setIntrinsicHeight(3);
            // Instanciam la variable drawableMissil amb el dibuix vectorial //
            drawableMissil = dMissil;

            // Path Nau Vectorial //
            Path pathNau = new Path();
            pathNau.moveTo((float)0.0, (float)0.0);
            pathNau.lineTo((float) 1.0, (float) 0.5);
            pathNau.lineTo((float) 0.0, (float) 1.0);
            pathNau.lineTo((float) 0.0, (float) 0.0);
            ShapeDrawable drawableNau = new ShapeDrawable(new PathShape(pathNau, 1, 1));
            drawableNau.getPaint().setColor(Color.WHITE);
            drawableNau.getPaint().setStyle(Paint.Style.STROKE);
            drawableNau.setIntrinsicWidth(100);
            drawableNau.setIntrinsicHeight(75);
            // Instanciam la variable drawableNave amb el dibuix vectorial //
            drawableNave = drawableNau;

            // Path Asteroide Vectorial //
            Path pathAsteroide = new Path();
            pathAsteroide.moveTo((float) 0.3, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.3);
            pathAsteroide.lineTo((float) 0.8, (float) 0.2);
            pathAsteroide.lineTo((float) 1.0, (float) 0.4);
            pathAsteroide.lineTo((float) 0.8, (float) 0.6);
            pathAsteroide.lineTo((float) 0.9, (float) 0.9);
            pathAsteroide.lineTo((float) 0.8, (float) 1.0);
            pathAsteroide.lineTo((float) 0.4, (float) 1.0);
            pathAsteroide.lineTo((float) 0.0, (float) 0.6);
            pathAsteroide.lineTo((float) 0.0, (float) 0.2);
            pathAsteroide.lineTo((float) 0.3, (float) 0.0);
            ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 1, 1));
            dAsteroide.getPaint().setColor(Color.WHITE);
            dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
            dAsteroide.setIntrinsicWidth(50);
            dAsteroide.setIntrinsicHeight(50);
            setBackgroundColor(Color.BLACK);
            // Instanciam la variable drawableAsteroide amb el dibuix vectorial //
            drawableAsteroide = dAsteroide;
        } else {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);

            // Instanciam la variable drawableAsteroide amb una imatge jpg//
            drawableAsteroide = ContextCompat.getDrawable(context, R.drawable.asteroide1);

            // Instanciam la variable drawableNave amb una imatge jpg//
            drawableNave = context.getResources().getDrawable(R.drawable.nau);

            // Instanciam la variable drawableMissil amb una imatge jpg//
            drawableMissil = context.getResources().getDrawable(R.drawable.animacio_missil);
        }

        // Inicialitzam una serie d'asteroides emmagatzemats a un arraylist //
        Asteroides = new ArrayList<Grafic>();
        for (int i = 0; i < numAsteroides; i++) {
            Grafic asteroide = new Grafic(this, drawableAsteroide);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngle((int) (Math.random() * 360));
            asteroide.setRotacio((int) (Math.random() * 8 - 4));
            Asteroides.add(asteroide);
        }

        // Inicialitzam la variable nau //
        nau = new Grafic(this, drawableNave);
        /*nau2 = new Grafic(this, drawableNave);*/

        // Inicialitzam la llista de missils //
        Missils = new ArrayList<Grafic>();

        // Inicialitzam la variable missil //
        /*missil = new Grafic(this, drawableMissil);*/

    }

//////////// Metode onSizeChanged ////////////////////////////////////
    @Override
    protected void onSizeChanged(int ample, int alt, int ample_anter, int alt_anter) {
        super.onSizeChanged(ample, alt, ample_anter, alt_anter);
        nau.setPosX((ample-nau.getAmple()) / 2);
        nau.setPosY((alt-nau.getAlt()) / 2);
        /*nau2.setPosX((ample-nau.getAmple()) / 2 + 100);
        nau2.setPosY((alt-nau.getAlt()) / 2 + 100);*/
        // Un cop coneixem el nostre ample i alt.
        for (Grafic asteroide: Asteroides) {
            asteroide.setPosX(Math.random() * (ample-asteroide.getAmple()));
            asteroide.setPosY(Math.random() * (alt-asteroide.getAlt()));
        }
        darrerProces = System.currentTimeMillis();
        thread.start();
    }
//////////////////////////////////////////////////////////////////////

//////////// Metode onDraw ///////////////////////////////////////////
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Dibuixam la nau //
        nau.dibuixaGrafic(canvas);
        /*nau2.dibuixaGrafic(canvas);*/

        // Dibuixam els asteroides //
        for (Grafic asteroide: Asteroides) {
            asteroide.dibuixaGrafic(canvas);
        }

        // Dibuixam els missils en cas de que hagin estat disparats //
        if(!Missils.isEmpty()) {
            //missil.dibuixaGrafic(canvas);
            for(int i = 0 ; i < Missils.size() ; i++){
                Missils.get(i).dibuixaGrafic(canvas);
            }
        }
    }
//////////////////////////////////////////////////////////////////////

//////////// Clase ThreadJoc /////////////////////////////////////////
class ThreadJoc extends Thread {
    private boolean pausa, corrent;
    public synchronized void pausar() {
        pausa = true;
    }
    public synchronized void reprendre() {
        pausa = false;
        notify();
    }
    public void detenir() {
        corrent = false;
        if (pausa) reprendre();
    }
    @Override public void run() {
        corrent = true;
        while (corrent) {
            actualitzaFisica();
            synchronized (this) {
                while (pausa)
                    try { wait(); }
                    catch (Exception e) { Log.i("Cicle","Ha petat el thread.\n ERROR: " + e); }
            }
        }
    }

    // Getters i setters de ThreadJoc //
    public boolean isPausa() { return pausa; }

    public boolean isCorrent() { return corrent; }

    public void setPausa(boolean pausa) { this.pausa = pausa; }

    public void setCorrent(boolean corrent) { this.corrent = corrent; }
}
    // Getter de la clase ThreadJoc //
    public ThreadJoc getThread() { return thread; }
//////////////////////////////////////////////////////////////////////

//////////// Metode actualitzaFisica() ///////////////////////////////
    protected synchronized void actualitzaFisica(){
        long ara = System.currentTimeMillis();
        // No fer res fins a final del període
        if (darrerProces + PERIODE_PROCES > ara){
            return;
        }
        // Per a una execució en temps real calculam retard
        double retard = (ara - darrerProces) / PERIODE_PROCES;

        darrerProces = ara;

        // Actualitzam velocitat i direcció de la nau a partir de girNau i acceleracioNau
        nau.setAngle((int) (nau.getAngle() + girNau * retard));
        double nIncX = nau.getIncX() + acceleracioNau *
                Math.cos(Math.toRadians(nau.getAngle())) * retard;
        double nIncY = nau.getIncY() + acceleracioNau *
                Math.sin(Math.toRadians(nau.getAngle())) * retard;
        // Actualitzam si el mòdul de la velocitat no excedeix el màxim
        if (Math.hypot(nIncX,nIncY) <= MAX_VELOCITAT_NAU){
            nau.setIncX(nIncX);
            nau.setIncY(nIncY);
        }
        // SEGONA NAU PROVA //
        /*nau2.setAngle((int) (nau.getAngle() + girNau * retard));
        double xIncX = nau2.getIncX() + acceleracioNau *
                Math.cos(Math.toRadians(nau.getAngle())) * retard;
        double xIncY = nau2.getIncY() + acceleracioNau *
                Math.sin(Math.toRadians(nau.getAngle())) * retard;
        // Actualitzam si el mòdul de la velocitat no excedeix el màxim
        if (Math.hypot(xIncX,xIncY) <= MAX_VELOCITAT_NAU){
            nau2.setIncX(xIncX);
            nau2.setIncY(xIncY);
        }
        nau2.incrementaPos(retard);*/
        // Actualitzam posicions X i Y
        nau.incrementaPos(retard);

        for (Grafic asteroide : Asteroides) {
            asteroide.incrementaPos(retard);
        }

        for (int i = 0; i < Missils.size(); i++) {
            Missils.get(i).incrementaPos(retard);
            int tempsMissil = tempsMissils.get(i).intValue();
            tempsMissil -= retard;
            tempsMissils.set(i, Integer.valueOf(tempsMissil));
            if (tempsMissils.get(i).intValue() < 0) {
                Missils.remove(i);
                tempsMissils.remove(i);
            } else {
                for (int m = 0; m < Asteroides.size(); m++)
                    if (Missils.get(i).verificaColisio(Asteroides.get(m))) {
                        destrueixAsteroide(m);
                        Missils.remove(i);
                        tempsMissils.remove(i);
                        break;
                    }
            }
        }

        // Aquest codi era per disparar 1 sol missil //
        /*if (missilActiu) {
            missil.incrementaPos(retard);
            tempsMissil-=retard;
            if (tempsMissil < 0) {
                missilActiu = false;
            } else {
                for (int i = 0; i < Asteroides.size(); i++)
                if (missil.verificaColisio(Asteroides.get(i))) {
                    destrueixAsteroide(i);
                    break;
                }
            }
        }*/

    }

//////////// Metodes activaMissil() i destrueixAsteroide() ///////////
    private void destrueixAsteroide(int i) {
        Asteroides.remove(i);

        // Aquesta variable l'empravem quan nomes podia haver un missil actiu al mateix temps a la pantalla
        /*missilActiu = false;*/
    }

    private void activaMissil() {
        // Mode un missil per dispar //
        /*Grafic missil = new Grafic(this, drawableMissil);
        missil.setPosX(nau.getPosX());
        missil.setPosY(nau.getPosY()+36);
        missil.setAngle(nau.getAngle());
        missil.setIncX(Math.cos(Math.toRadians(missil.getAngle())) * PAS_VELOCITAT_MISSIL);
        missil.setIncY(Math.sin(Math.toRadians(missil.getAngle())) * PAS_VELOCITAT_MISSIL);
        tempsMissils.add((int) Math.min(this.getWidth() / Math.abs(missil.getIncX()), this.getHeight() / Math.abs(missil.getIncY())) - 2);
        Missils.add(missil);*/

        // Mode 2 missils per dispar //
        Grafic missil1 = new Grafic(this, drawableMissil);
        Grafic missil2 = new Grafic(this, drawableMissil);
        // Angle X dels missils //
        missil1.setPosX(nau.getPosX());
        missil2.setPosX(nau.getPosX());
        // Angle Y dels missils //
        missil1.setPosY(nau.getPosY()+5);

        missil2.setPosY(nau.getPosY()+65);

        //  Inclinacio dels missils //
        missil1.setAngle(nau.getAngle());
        missil2.setAngle(nau.getAngle());

        missil1.setIncX(Math.cos(Math.toRadians(missil1.getAngle())) * PAS_VELOCITAT_MISSIL);
        missil2.setIncX(Math.cos(Math.toRadians(missil2.getAngle())) * PAS_VELOCITAT_MISSIL);

        missil1.setIncY(Math.sin(Math.toRadians(missil1.getAngle())) * PAS_VELOCITAT_MISSIL);
        missil2.setIncY(Math.sin(Math.toRadians(missil2.getAngle())) * PAS_VELOCITAT_MISSIL);

        tempsMissils.add((int) Math.min(this.getWidth() / Math.abs(missil1.getIncX()), this.getHeight() / Math.abs(missil1.getIncY())) - 2);
        tempsMissils.add((int) Math.min(this.getWidth() / Math.abs(missil2.getIncX()), this.getHeight() / Math.abs(missil2.getIncY())) - 2);

        Missils.add(missil1);
        Missils.add(missil2);
    }
//////////////////////////////////////////////////////////////////////

//////////// Control per teclat //////////////////////////////////////
    // Pulsació de tecla //
    public boolean onKeyDown(int codiTecla, KeyEvent event) {
        super.onKeyDown(codiTecla, event);
        // Processam la pulsació //
        if (teclatActiu) {
            processada = true;
            switch (codiTecla) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    acceleracioNau = +PAS_ACCELERACIO_NAU;
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    girNau = -PAS_GIR_NAU;
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    girNau = +PAS_GIR_NAU;
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    // Aqui nomes he afegit aquesta linea de codi perque faixi la nau marxa enrere //
                    /*acceleracioNau = -PAS_ACCELERACIO_NAU;*/
                    break;

                case KeyEvent.KEYCODE_DPAD_CENTER:
                    break;

                case KeyEvent.KEYCODE_ENTER:
                    activaMissil();
                    break;

                default:
                    // Si estem aquí, no hi ha pulsació que ens interessi //
                    processada = false;
                    break;
            }
        }
        return processada;
    }

    // Lliurament de tecla //
    public boolean onKeyUp(int codiTecla, KeyEvent event) {
        super.onKeyUp(codiTecla, event);
        // Processam la pulsació //
        if (teclatActiu) {
            processada = true;
            switch (codiTecla) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    acceleracioNau = 0;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    // Aqui nomes he afegit aquesta linea de codi perque faixi la nau marxa enrere //
                    /*acceleracioNau = -PAS_ACCELERACIO_NAU;*/
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    // Atura la rotació de la nau //
                    girNau = 0;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    // Atura la rotació de la nau //
                    girNau = 0;
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    break;
                case KeyEvent.KEYCODE_ENTER:
                    activaMissil();
                    break;
                default:
                    // Si estem aquí, no hi ha pulsació que ens interessi
                    processada = false;
                    break;
            }
        }
        return processada;
    }
//////////////////////////////////////////////////////////////////////

//////////// Control tàctil //////////////////////////////////////////
    public boolean onTouchEvent (MotionEvent event) {
        super.onTouchEvent(event);
        if (tactilActiu) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dispar = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Aquesta linia de codi canvia la imatge de la nau a la que te el foc de propulsió //
                    nau.setDrawable(getContext().getDrawable(R.drawable.nau_foc));

                    // Aqui medim la distancia recorreguda pel dit //
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);

                    // Aquesta condicio mira si el moviment del dit es en horitzontal (mes o menys) //
                    if (dy < 6 && dx > 6) {
                        // Amb aquesta linia de codi rota la nau segons la distancia que hem recorregut amb el dit //
                        girNau = Math.round((x - mX) / 2); // Amb la divisió controlam que la nau roti a una velocitat adequada //
                        dispar = false;
                    // Aquesta altra condicio mira si el moviment del dit es en vertical (mes o menys) //
                    } else if (dx < 6 && dy > 6) {
                        // Amb aquesta linia de accelera la nau segons la distancia que hem recorregut amb el dit //
                        acceleracioNau = Math.round((mY - y) / 25); // Amb la divisió controlam que la nau no acceleri a una velocitat massa gran //
                        dispar = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // Quan s'aixeca el dit tornam a posar la imatge de la nau sense foc //
                    nau.setDrawable(getContext().getDrawable(R.drawable.nau));

                    // Aturam la rotció i acceleració de la nau //
                    girNau = 0;
                    acceleracioNau = 0;

                    // Si la variable 'dispar' és true (no s'ha fet cap moviment amb el dit) es disparará un missil //
                    if (dispar) { activaMissil(); }
                    break;
            }
            mX = x;
            mY = y;
            return true;
        }
        return false;
    }
//////////////////////////////////////////////////////////////////////

//////////// Control per sensors /////////////////////////////////////
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float valorGir = sensorEvent.values[1];
        float valorAcc = sensorEvent.values[2];
        if (!hihaValorInicial){
            valorInicial = valorAcc;
            hihaValorInicial = true;
        }
        if (!hihaValorInicial){
            valorInicial = valorGir;
            hihaValorInicial = true;
        }
        girNau = (int)(valorGir-valorInicial)/2 ;
        acceleracioNau = (int)(valorAcc-valorInicial)/3 ;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    public void desactivarSensors() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    public void activarSensors(Context context) {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!listSensors.isEmpty()) {
            Sensor accelerometerSensor = listSensors.get(0);
            mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }
//////////////////////////////////////////////////////////////////////
}
