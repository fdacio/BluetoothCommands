package br.com.daciosoftware.bluetoothcommands;

public class BluetoothInstance {

    private static BluetoothConnection mmInstance;

    public static BluetoothConnection getInstance() {
        return mmInstance;
    }

    public static void setInstance(BluetoothConnection instance) {
        mmInstance = instance;
    }

}
