package br.edu.ifma.www.projetomonografiafalldetection;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class Principal extends Activity implements SensorEventListener{
    //Views
    TextView freeFall;
    TextView impact;
    TextView gravidade;

    //Variáveis para controle do Acelerômetro
    private SensorManager sensorManager;
    private Sensor sensor;

    private boolean min = false;
    private boolean max = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_principal);
        //Criando os objetos para controle do sensor

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //Setando o Listener do sensor
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    protected void onPause() {
        super.onPause();
        //Libera o Listener evitando consumo de processamento e bateria.
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean queda = fallDetection(event);

        //Ação que deve ser realizada após a detecção da queda
        if (queda)
            Toast.makeText(this,"Queda!",Toast.LENGTH_SHORT).show();
    }

    private boolean fallDetection (SensorEvent event) {
        Float x = event.values[0];
        Float y = event.values[1];
        Float z = event.values[2];

        float [] posicao = event.values;

        //Determina o módulo / norma, comprimento do vetor (V³)
        Double resultado = Math.sqrt(Math.abs(x*x) + Math.abs(y*y) + Math.abs(z*z));
        gravidade = (TextView) findViewById(R.id.gravidade);
        gravidade.setText("Gravidade: "+resultado);

        //Lower Threshold  0G (Free Fall)
        if (resultado <= 1) {
            min = true;
            freeFall = (TextView) findViewById(R.id.freeFall);
            freeFall.setText("Queda Livre! comprimento: " +resultado);
        }
        //Upper Threshold 3G, Impact!
        if (resultado >= 15) {
            max = true;
            impact = (TextView) findViewById(R.id.impact);
            impact.setText("Impacto! comprimento: " +resultado);
        }

        //Lower and Upper Detected!
        if (min && max) {
            //Inactivity 1G
            if (resultado >= 9 && resultado <= 9.8) {
                min = false;
                max = false;
                //Verifica se o device está em posisao de queda
                if (posicaoDevice(posicao)) {
                    return true;
                }
            }
        }

        return  false;
    }

    private boolean posicaoDevice(float [] vetor) {

        Float y = vetor[1];
        Float z = vetor[2];

        //Deixa passar alguns segundos para verificar a posição do sensor
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Log.i("aviso","Tempo"+(System.currentTimeMillis() - t0));

        //Device com o visor para cima ou para baixo
        if (Math.abs(y) <= 2 ) {
            if (z > 0) {
                Log.i("aviso","Visor para cima");
                return true;
            }
            else {
                Log.i("aviso","Visor para baixo");
                return true;
            }
        }

        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
