package com.example.bluetoothcommands;

import android.bluetooth.BluetoothDevice;

public interface BluetoothConnectionListener {
    void setConnected(BluetoothDevice device);
    void setDisconnectedInView();
    void readFromDevicePaired(String dataReceiver);
}
