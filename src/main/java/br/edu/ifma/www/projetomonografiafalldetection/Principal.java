package br.edu.ifma.www.projetomonografiafalldetection;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


public class Principal extends Activity implements SensorEventListener{
    //Views
    TextView freeFall;
    TextView impact;

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

        if (queda)
            Toast.makeText(this,"Queda!",Toast.LENGTH_SHORT).show();
    }

    private boolean fallDetection (SensorEvent event) {
        Float x = event.values[0];
        Float y = event.values[1];
        Float z = event.values[2];

        //Determina o módulo / norma, comprimento do vetor (V³)
        Double resultado = Math.sqrt(Math.abs(x*x) + Math.abs(y*y) + Math.abs(z*z));

        //Lower Threshold  0G (Free Fall)
        if (resultado <= 2) {
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

                return true;
            }
        }

        return  false;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
