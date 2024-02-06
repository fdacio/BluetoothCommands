package br.com.daciosoftware.bluetoothcommands.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.R;

public class BluetoothManagerControl {

    private static final int REQUEST_PERMISSION_BLUETOOTH = 2;
    private final static int REQUEST_ENABLE_BLUETOOTH = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice devicePaired;
    private BluetoothBroadcastReceive bluetoothBroadcastReceiver;
    private IntentFilter filter;
    private Context appContext;
    private Activity activity;

    public BluetoothManagerControl(Context context) {
        appContext = context;
        activity = (Activity) context;
        bluetoothBroadcastReceiver = new BluetoothBroadcastReceive(context);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public BluetoothDevice getDevicePaired() {
        return devicePaired;
    }

    public void setDevicePaired(BluetoothDevice devicePaired) {
        this.devicePaired = devicePaired;
    }

    public void requestPermissionBluetooth() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int permissionBluetoothScan = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_SCAN);
            int permissionBluetoothConnect = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT);
            if ((permissionBluetoothScan != PackageManager.PERMISSION_GRANTED) ||
                    (permissionBluetoothConnect != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions((Activity) appContext, new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN}, REQUEST_PERMISSION_BLUETOOTH);
            }
        } else {
            checkAndEnableBluetoothAdapter();
            int permissionBluetooth = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH);
            int permissionBluetoothAdmin = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_ADMIN);
            int permissionBluetoothAdvertise = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_ADVERTISE);
            int permissionCoarseLocation = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionFineLocation = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION);
            if ((permissionBluetooth != PackageManager.PERMISSION_GRANTED) ||
                    (permissionBluetoothAdmin != PackageManager.PERMISSION_GRANTED) ||
                    (permissionBluetoothAdvertise != PackageManager.PERMISSION_GRANTED) ||
                    (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) ||
                    (permissionFineLocation != PackageManager.PERMISSION_GRANTED)
            ) {
                ActivityCompat.requestPermissions((Activity) appContext, new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_BLUETOOTH);
            }
        }
    }

    public void requestPermissionAccessLocation() {

        int permissionCoarseLocation = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionFineLocation = ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if ((permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) ||
                (permissionFineLocation != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions((Activity) appContext, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_BLUETOOTH);

        }
    }
    @SuppressLint({"MissingPermission"})
    private void createBluetoothRegisterReceive() {
        bluetoothBroadcastReceiver = new BluetoothBroadcastReceive(appContext);
        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(bluetoothBroadcastReceiver, filter);
    }

    @SuppressLint("MissingPermission")
    public void checkAndEnableBluetoothAdapter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int permissionBluetoothConnect = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT);
            if (permissionBluetoothConnect != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) appContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    @SuppressLint("MissingPermission")
    public List<BluetoothDevice> startDiscovery() {
        requestPermissionBluetooth();
        requestPermissionAccessLocation();
        checkAndEnableBluetoothAdapter();
        if (checkBluetoothPermissionScan()) {
           getBluetoothAdapter().startDiscovery();
        }
        return null;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_BLUETOOTH) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissions[0].equals(Manifest.permission.BLUETOOTH_CONNECT)) {
                    checkAndEnableBluetoothAdapter();
                }
            } else {
                Toast.makeText(appContext, R.string.message_permission_important, Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean checkBluetoothPermissionScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int permissionBluetoothScan = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_SCAN);
            return (permissionBluetoothScan == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    public boolean checkBlutoothPermissionConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int permissionBluetoothConnect= ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT);
            return (permissionBluetoothConnect == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    public interface BluetoothManagerControlDiscoveryDevices {
        void initDiscoveryDevices();
        void finishDiscoveryDevices(List<BluetoothDevice> listDevices);
        void foundDevice(BluetoothDevice device);
    }

    public interface BluetoothManagerControlConnection {
        void postDeviceConnect(BluetoothDevice device);
        void postDeviceDisconnect();
        void postDataReceive(String dataReceive);

    }

}
