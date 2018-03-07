package com.example.adrianmontes.aplicacionseleccionpersonal.ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.SystemClock;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by adrian.montes on 1/3/18.
 */

//En esta clase vamos a poner nuevos componentes de Android que salieron en 2017
    //lo que hacemos es un eschucador de la varibale mContador cada vez que se actualice
//En el hilo se encagarga de ir metiendo valores en la variable para poder ir comprobando que en el metodo mutableLiveData
    //me retorna el resultado
public class CityViewModel extends ViewModel {

    private int contador;
    private long mInitialTime;
    private MutableLiveData<Integer> mContador;
    TextView CampoTextomContador;

    public CityViewModel() {
        mInitialTime = SystemClock.elapsedRealtime();
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                final long newValor=(SystemClock.elapsedRealtime()-mInitialTime)/1000;
                mContador.postValue((int)newValor);

            }

        },1000,1000);

    }
    public MutableLiveData<Integer> getmContador(){

        //si no existe el valor lo creo
     if(getmContador()==null){
         mContador=new MutableLiveData<Integer>();

     }
        return mContador;
    }







}
