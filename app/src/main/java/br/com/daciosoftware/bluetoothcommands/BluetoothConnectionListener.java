package br.com.daciosoftware.bluetoothcommands;

import android.bluetooth.BluetoothDevice;

public interface BluetoothConnectionListener {
    void setConnected(BluetoothDevice device);
    void setDisconnected();
    void readFromDevicePaired(String dataReceiver);
}
