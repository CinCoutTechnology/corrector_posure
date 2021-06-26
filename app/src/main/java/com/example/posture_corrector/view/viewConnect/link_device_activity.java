package com.example.posture_corrector.view.viewConnect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.posture_corrector.R;
import com.example.posture_corrector.adapter.MyAdapter;
import com.example.posture_corrector.view.monitor.monitor_back_activity;
import com.example.posture_corrector.view.utilities.preferencesU;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class link_device_activity extends Activity {

    private Button connect;
    private ListView search_list;
    private BluetoothAdapter mBTAdapter;
    private static final int BT_ENABLE_REQUEST = 10;
    private static final int SETTINGS = 20;
    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int mBufferSize = 50000;
    private AlertDialog.Builder builder;
    private AlertDialog dialogBu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_device_activity);

        search_list = findViewById(R.id.search_list);
        connect = findViewById(R.id.connect_d);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Importante");
        builder.setMessage("Enciende tu bluetooth y vincular el dispositivo");
        builder.setPositiveButton("Confirmar", null);
        dialogBu = builder.create();
        dialogBu.show();
        Button positiveButton = dialogBu.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor(getString(R.string.color)));

        if (savedInstanceState != null) {
            ArrayList<BluetoothDevice> list = savedInstanceState.getParcelableArrayList(preferencesU.DEVICE_LIST);
            if (list != null) {
                initList(list);
                MyAdapter adapter = (MyAdapter) search_list.getAdapter();
                int selectedIndex = savedInstanceState.getInt(preferencesU.DEVICE_LIST_SELECTED);
                if (selectedIndex != -1) {
                    adapter.setSelectedIndex(selectedIndex);
                    connect.setEnabled(true);
                }
            } else {
                initList(new ArrayList<>());
            }
        } else {
            initList(new ArrayList<>());
        }

        findViewById(R.id.search_d).setOnClickListener(v -> {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBTAdapter == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.Bluetooth_not_found), Toast.LENGTH_SHORT).show();
            } else if (!mBTAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, BT_ENABLE_REQUEST);
            } else {
                //noinspection deprecation
                new SearchDevices().execute();
            }
        });
        connect.setOnClickListener(v -> {
            BluetoothDevice device = ((MyAdapter) (search_list.getAdapter())).getSelectedItem();
            Intent intent = new Intent(getApplicationContext(), monitor_back_activity.class);
            intent.putExtra(preferencesU.DEVICE_EXTRA, device);
            intent.putExtra(preferencesU.DEVICE_UUID, mDeviceUUID.toString());
            intent.putExtra(preferencesU.BUFFER_SIZE, mBufferSize);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_ENABLE_REQUEST:
                if (resultCode == RESULT_OK) {
                    msg(getString(R.string.Bluetooth_enabled_correctly));
                    //noinspection deprecation
                    new SearchDevices().execute();
                } else {
                    msg(getString(R.string.Bluetooth_not_enabled));
                }
                break;
            case SETTINGS:
                //noinspection deprecation
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String uuid = prefs.getString("prefUuid", "null");
                mDeviceUUID = UUID.fromString(uuid);
                Log.d(preferencesU.TAG, "UUID: " + uuid);

                String bufSize = prefs.getString("prefTextBuffer", "Null");
                mBufferSize = Integer.parseInt(bufSize);

                String orientation = prefs.getString("prefOrientation", "Null");
                Log.d(preferencesU.TAG, "Orientation: " + orientation);

                switch (orientation) {
                    case "Landscape":
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case "Portrait":
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    case "Auto":
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                        break;
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initList(List<BluetoothDevice> objects) {
        final MyAdapter adapter = new MyAdapter(getApplicationContext(), R.layout.list_item, R.id.lstContent, objects);
        search_list.setAdapter(adapter);
        search_list.setOnItemClickListener((parent, view, position, id) -> {
            adapter.setSelectedIndex(position);
            connect.setEnabled(true);
        });
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {

        @Override
        protected List<BluetoothDevice> doInBackground(Void... params) {
            Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
            List<BluetoothDevice> listDevices = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) {
                //noinspection UseBulkOperation
                listDevices.add(device);
            }
            return listDevices;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(List<BluetoothDevice> listDevices) {
            super.onPostExecute(listDevices);
            if (listDevices.size() > 0) {
                MyAdapter adapter = (MyAdapter) search_list.getAdapter();
                adapter.replaceItems(listDevices);
            } else {
                msg(getString(R.string.No_paired));
            }
        }

    }

    private void msg(String ms) {
        Toast.makeText(getApplicationContext(), ms, Toast.LENGTH_SHORT).show();
    }
}