package br.com.daciosoftware.bluetoothcommands.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

import androidx.annotation.NonNull;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogProgress;
import br.com.daciosoftware.bluetoothcommands.ui.bluetooth.DevicesBluetoothAdapter;

public abstract class BluetoothBroadcastReceive extends BroadcastReceiver {

    private AlertDialogProgress dialog;
    private List<BluetoothDevice> listDevices;
    private ListView listViewDevices;

    @SuppressLint("MissingPermission")
    public BluetoothBroadcastReceive(Context context) {
        dialog = new AlertDialogProgress(context, AlertDialogProgress.TypeDialog.SEARCH_DEVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            actionDiscoveryStarted();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            actionDiscoveryFinished();
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            actionFoundDevice(device);
        }
    }

    public void actionDiscoveryStarted(@NonNull List<BluetoothDevice> listDevices, @NonNull ListView listViewDevices){
        this.listDevices = listDevices;
        this.listViewDevices = listViewDevices;
    }


    public abstract void actionDiscoveryStarted();
    public abstract void actionDiscoveryFinished();
    public abstract void actionFoundDevice(BluetoothDevice device);
}
