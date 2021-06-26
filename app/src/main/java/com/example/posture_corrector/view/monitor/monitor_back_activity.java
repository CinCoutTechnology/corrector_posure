package com.example.posture_corrector.view.monitor;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.posture_corrector.R;
import com.example.posture_corrector.adapter.SliderAdapter;
import com.example.posture_corrector.view.utilities.preferencesU;
import com.example.posture_corrector.view.viewConnect.ActivityHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class monitor_back_activity extends Activity {


    private static final String TAG = "BlueTest5-Controlling";
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private boolean mIsBluetoothConnected = false;
    private BluetoothDevice mDevice;
    final int RECEIVE_MESSAGE = 1;
    final static String on = "92";//on
    final static String off = "79";//off
    private ProgressDialog progressDialog;
    private ReadInput mReadThread = null;
    String print;
    private final StringBuilder sb = new StringBuilder();
    boolean tst = false;
    Handler h;
    Handler mon;
    Runnable my_runnable;
    private Dialog dialogView;
    TextView getTXT;
    SoundPool pool;
    boolean mis = false;
    int playback_sound;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_back_activity);

        ActivityHelper.initialize(this);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(preferencesU.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(preferencesU.DEVICE_UUID));

        getTXT = findViewById(R.id.getTXT);
        getTXT.setVisibility(View.GONE);
        Button init = findViewById(R.id.init);
        Button stop = findViewById(R.id.stop);
        stop.setVisibility(View.GONE);
        pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        playback_sound = pool.load(this, R.raw.soung, 1);

        my_runnable = () -> {
            mis = false;
            getTXT.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            init.setVisibility(View.VISIBLE);
            startActivity(new Intent(monitor_back_activity.this, exercise_activity.class));
        };

        init.setOnClickListener(v -> {
            mis = true;
            start();
            getTXT.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            init.setVisibility(View.GONE);
            try {
                mBTSocket.getOutputStream().write(on.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stop.setOnClickListener(v -> {
            mis = false;
            stop();
            getTXT.setVisibility(View.GONE);
            stop.setVisibility(View.GONE);
            init.setVisibility(View.VISIBLE);

            try {
                mBTSocket.getOutputStream().write(off.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //noinspection deprecation
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == RECEIVE_MESSAGE) {                                                    // if receive massage

                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, msg.arg1);//crear una cadena a partir de una matriz de bytes
                    sb.append(strIncom); // determina el final de la línea
                    int endOfLineIndex = sb.indexOf("\r\n");
                    if (endOfLineIndex > 0) {  // si es al final de la línea,
                        print = sb.substring(0, endOfLineIndex); //extraer cadena
                        tst = true;
                        sb.delete(0, sb.length());      // y limpia

                        getTXT.setText(print);  // actualizar TextView
                        try {
                            float integer = Float.parseFloat(print);
                            setSong(integer < 3.00);
                        } catch (Exception e) {
                            Log.e("error ", e.toString());
                        }
                    }
                }
            }
        };
    }

    private void setSong(boolean b) {

        if (mis && b) {
            Log.e("turn on ", "Sound");
            pool.play(playback_sound, 1, 1, 1, 0, 0);
        } else if (!mis | !b) {
            Log.e("To turn off ", "Sound");
            pool.autoPause();
        }
    }

    private void initDialog() {
        setDialog();
        ImageView imageView = dialogView.findViewById(R.id.image3);
        dialogView.setCancelable(false);
        dialogView.show();

        imageView.setOnClickListener(v -> dialogView.dismiss());
    }

    private void setDialog() {

        dialogView = new Dialog(monitor_back_activity.this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //noinspection deprecation
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialogView.getWindow().getAttributes());

        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        int dialogWindowHeight = (int) (displayHeight * 0.5f);

        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        dialogView.setContentView(R.layout.dialog_view);
        dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogView.getWindow().setAttributes(layoutParams);
    }

    @SuppressWarnings("deprecation")
    public Handler handler = new Handler();

    // to start the handler
    public void start() {
        handler.postDelayed(my_runnable, 20000);
    } //120000

    // to stop the handler
    public void stop() {
        handler.removeCallbacks(my_runnable);
    }

    // to reset the handler
    public void restart() {
        handler.removeCallbacks(my_runnable);
        handler.postDelayed(my_runnable, 20000);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                //noinspection StatementWithEmptyBody
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }
            try {
                mBTSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            boolean mIsUserInitiatedDisconnect = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("getText: ", getTXT.getText().toString().trim());
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            //noinspection deprecation
            new DisConnectBT().execute();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            //noinspection deprecation
            new ConnectBT().execute();
        }
        Log.e(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    private class ConnectBT extends AsyncTask<Void, Void, Void> {

        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(monitor_back_activity.this, "Mantener encendido el dispositivo", "Conectando");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (mBTSocket == null || !mIsBluetoothConnected) {
                try {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    mConnectSuccessful = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!mConnectSuccessful) {
                msg("No se pudo conectar al dispositivo. Por favor, enciendalo");
                finish();
            } else {
                msg("Conectado al dispositivo.");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput();
            }
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void msg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private final Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        public void stop() {
            bStop = true;
        }

        @Override
        public void run() {
            InputStream inputStream;
            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];  // buffer store for the stream
                    if (inputStream.available() > 0) {
                        int bytes = inputStream.read(buffer);
                        int i = 0;
                        // final String strInput = new String(buffer, 0, i);
                        h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                    }
                    //noinspection BusyWait
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.getMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}