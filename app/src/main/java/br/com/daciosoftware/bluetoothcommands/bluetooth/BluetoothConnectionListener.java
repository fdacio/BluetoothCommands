package br.com.daciosoftware.bluetoothcommands.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface BluetoothConnectionListener {
    void setConnected(BluetoothDevice device);
    void setDisconnected();
    void readFromDevicePaired(String dataReceiver);
}
