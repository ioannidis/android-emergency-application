package com.papei.instantservice.fall;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.papei.instantservice.R;
import com.papei.instantservice.panic.PanicActivity;
import com.papei.instantservice.util.MyDialog;


public class FallDetection extends Service implements SensorEventListener {


    public FallDetection() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    int count = 1;
    private boolean init;
    private Sensor mySensor;
    private SensorManager SM;
    private float x1, x2, x3;
    private static final float ERROR = (float) 7.0;
    private static final float SHAKE_THRESHOLD = 15.00f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;


    @Override
    public void onCreate() {

    }

    @Override


    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) +
                        Math.pow(y, 2) +
                        Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                Log.d("mySensor", "Acceleration is " + acceleration + "m/s^2");

                if (acceleration > SHAKE_THRESHOLD) {
                    mLastShakeTime = curTime;
                    Toast.makeText(getApplicationContext(), "FALL DETECTED",
                            Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "GIA TON POUTSO EPESE!", Toast.LENGTH_LONG).show();
                    Log.d("mySensor", "GIA TON POUTSO EPESE!");

//                    DialogFragment a = MyDialog.newInstance(1);
//                    a.show(getFragmentManager(), "dialog");



                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Test dialog");
                    builder.setMessage("Content");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Do something
                            dialog.dismiss();
                        }
                            });

                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            AlertDialog alert = builder.create();
                    alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    alert.show();


//                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
//                    alert.setTitle("TIN PANAGIA MOY");
//                    alert.setMessage("BARETHIKA");
////                    alert.setPositiveButton("I NEED HELP", (dialogInterface, i) -> {
////                        Toast.makeText(this,"10 GAMIMENA LEPTA PSAKSIMO STO INTERNET", Toast.LENGTH_LONG).show();
////                    });
////                    alert.setNegativeButton("OXI", (dialogInterface, i) -> {
////                        Toast.makeText(this,"OXI GAMO TIN EYA", Toast.LENGTH_LONG).show();
////                    });
//                    alert.create().show();



//                    new AlertDialog.Builder(this)
//                            .setTitle("Fall detected")
//                            .setMessage("Are you ok?")
//
//                            // Specifying a listener allows you to take an action before dismissing the dialog.
//                            // The dialog is automatically dismissed when a dialog button is clicked.
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // Continue with delete operation
//                                }
//                            })
//
//                            // A null listener allows the button to dismiss the dialog and take no further action.
//                            .setNegativeButton(android.R.string.no, null)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();

                }
            }
        }
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Start Detecting", Toast.LENGTH_LONG).show();
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        //TODO service foreground so it will keep working even if app closed


        return Service.START_STICKY;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
    }
}