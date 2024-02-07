package br.com.daciosoftware.bluetoothcommands.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

import androidx.annotation.NonNull;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogProgress;
import br.com.daciosoftware.bluetoothcommands.ui.bluetooth.DevicesBluetoothAdapter;

public class BluetoothBroadcastReceive extends BroadcastReceiver {

    private Context appContext;
    private BluetoothManagerControl.DiscoveryDevices listenerDiscoveryDevices;

    public BluetoothBroadcastReceive (Context context, BluetoothManagerControl.DiscoveryDevices listenerDiscoveryDevices ) {
        this.appContext = context;
        this.listenerDiscoveryDevices = listenerDiscoveryDevices;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            listenerDiscoveryDevices.initDiscoveryDevices();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            listenerDiscoveryDevices.finishDiscoveryDevices();
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            listenerDiscoveryDevices.foundDevice(device);

        }
    }

}
