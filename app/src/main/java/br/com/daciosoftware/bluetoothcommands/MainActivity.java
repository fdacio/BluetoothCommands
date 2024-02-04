package br.com.daciosoftware.bluetoothcommands;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_BLUETOOTH = 2;
    private final static int REQUEST_ENABLE_BLUETOOTH = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice devicePaired;
    private BluetoothBroadcastReceive bluetoothBroadcastReceiver;
    private IntentFilter filter;
    private Context appContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_bluetooth, R.id.navigation_commands).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public BluetoothDevice getDevicePaired() {
        return devicePaired;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public BluetoothBroadcastReceive getBluetoothBroadcastReceive() {
        return bluetoothBroadcastReceiver;
    }

    public void setDevicePaired(BluetoothDevice devicePaired) {
        this.devicePaired = devicePaired;
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

    public void requestPermissionBluetooth() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

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
            if ((permissionBluetooth != PackageManager.PERMISSION_GRANTED) || (permissionBluetoothAdmin != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions((Activity) appContext, new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_PERMISSION_BLUETOOTH);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void checkAndEnableBluetoothAdapter() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int permissionBluetoothConnect = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT);
            if (permissionBluetoothConnect != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            int permissionBluetoothScan = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_SCAN);
            if (permissionBluetoothScan != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        BluetoothManager bluetoothManager = (BluetoothManager) appContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_BLUETOOTH:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    if (permissions[0].equals(Manifest.permission.BLUETOOTH_CONNECT)) {
                        checkAndEnableBluetoothAdapter();
                    }

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BLUETOOTH) {
            Toast.makeText(appContext, "Bluetooth ativado com successo.", Toast.LENGTH_LONG).show();
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(appContext, "Ativar o Bluetooth é fundamental para o uso do aplicativo", Toast.LENGTH_LONG).show();
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
        registerReceiver(bluetoothBroadcastReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        createBluetoothRegisterReceive();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(bluetoothBroadcastReceiver);
    }

}