package br.com.daciosoftware.bluetoothcommands;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ListView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import br.com.daciosoftware.bluetoothcommands.ui.bluetooth.DevicesBluetoothAdapter;

public class BluetoothBroadcastReceive extends BroadcastReceiver {

    private Context context;
    private ProgressDialog progressDialog;
    private ArrayList<BluetoothDevice> listDevices;
    private ListView listViewDevices;
    @SuppressLint("MissingPermission")
    public BluetoothBroadcastReceive(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Bluetooth");
        progressDialog.setMessage("Aguarde, procurando por dispositivos.");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", (dialog, which) -> {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            bluetoothAdapter.cancelDiscovery();
            dialog.dismiss();
        });

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            if (listDevices != null) listDevices.clear();
            progressDialog.show();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            DevicesBluetoothAdapter devicesBluetoothAdapter = new DevicesBluetoothAdapter(context);
            if (listDevices != null) {
                devicesBluetoothAdapter.setData(listDevices);
                listViewDevices.setAdapter(devicesBluetoothAdapter);
            }
            progressDialog.dismiss();
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (listDevices != null && !listDevices.contains(device)) {
                listDevices.add(device);
            }
        }
    }

    public void actionDiscoveryStarted(@NonNull ArrayList<BluetoothDevice> listDevices, @NonNull ListView listViewDevices){
        this.listDevices = listDevices;
        this.listViewDevices = listViewDevices;
    }
}
