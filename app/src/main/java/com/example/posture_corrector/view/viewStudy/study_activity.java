package com.example.posture_corrector.view.viewStudy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posture_corrector.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class study_activity extends AppCompatActivity {


    Handler h;
    private static final String TAG = "bluetooth";
    final int RECEIVE_MESSAGE = 1;
    private TextView getTXT;
    private BluetoothSocket btSocket = null;
    private BluetoothAdapter btAdapter = null;
    private final StringBuilder sb = new StringBuilder();

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static final String address = "98:D3:32:30:84:16";

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_activity);

        Button initT = findViewById(R.id.init);
        // Button stop = findViewById(R.id.stop);
        getTXT = findViewById(R.id.getTXT);

        initT.setVisibility(View.VISIBLE);
        // stop.setVisibility(View.GONE);
        getTXT.setVisibility(View.GONE);

        initT.setOnClickListener(v -> {
            initT.setVisibility(View.GONE);
            getTXT.setVisibility(View.VISIBLE);
            //stop.setVisibility(View.VISIBLE);
        });

        /*stop.setOnClickListener(v -> {
            stop.setVisibility(View.GONE);
            getTXT.setVisibility(View.GONE);
            init.setVisibility(View.VISIBLE);
        });*/

        h = new Handler() {
            @SuppressWarnings("SwitchStatementWithoutDefaultBranch")
            public void handleMessage(android.os.Message msg) {
                if (msg.what == RECEIVE_MESSAGE) {                                        // si recibe masaje
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, msg.arg1);           // crea una cadena a partir de una matriz de bytes
                    sb.append(strIncom);                                               // añadir cadena
                    int endOfLineIndex = sb.indexOf("\r\n");                          // determina el final de la línea
                    if (endOfLineIndex > 0) {                                        // si es al final de la línea,
                        String fact = sb.substring(0, endOfLineIndex);              // extraer cadena
                        sb.delete(0, sb.length());                                 // y clean
                        getTXT.setText(fact);                                     //actualizar TextView
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get el adaptador Bluetooth
        checkBTState();


    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, MY_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        btAdapter.cancelDiscovery();

        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    private void checkBTState() {
        // Verificamos la compatibilidad con Bluetooth y luego verificamos para asegurarse de que esté encendido
        // El emulador no es compatible con Bluetooth y devolverá un valor nulo
        if (btAdapter == null) {
            errorExit("Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //noinspection deprecation
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String message) {
        Toast.makeText(getBaseContext(), "Fatal Error" + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Obtener los flujos de entrada y salida, usando objetos temporales porque
            // las transmisiones de miembros son definitivas
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
            while (true) {
                try {
                    //Leer del InputStream
                    bytes = mmInStream.read(buffer); // Obtener el número de bytes y el mensaje en el "búfer"
                    h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget(); // Enviar a controlador de cola de mensajes
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}