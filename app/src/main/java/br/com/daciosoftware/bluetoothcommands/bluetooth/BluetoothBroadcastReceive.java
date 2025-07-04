package br.com.daciosoftware.bluetoothcommands.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class BluetoothBroadcastReceive extends BroadcastReceiver {

    private final BluetoothManagerControl.DiscoveryDevices listenerDiscoveryDevices;
private final BluetoothManagerControl.ConnectionDevice listenerConnectionDevice;

    public BluetoothBroadcastReceive (@NonNull BluetoothManagerControl.DiscoveryDevices listenerDiscoveryDevices,  BluetoothManagerControl.ConnectionDevice listenerConnectionDevice) {
        this.listenerDiscoveryDevices = listenerDiscoveryDevices;
        this.listenerConnectionDevice = listenerConnectionDevice;
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
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            listenerConnectionDevice.postDeviceDisconnection();
        } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice device =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            listenerConnectionDevice.postDeviceConnection(device);
        }
    }

}
