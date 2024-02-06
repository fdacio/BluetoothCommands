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

import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothBroadcastReceive;

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

        requestPermissionBluetooth();
        setBluetoothAdapter();

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

    public void setDevicePaired(BluetoothDevice devicePaired) {
        this.devicePaired = devicePaired;
    }

    public void setBluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) appContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public BluetoothBroadcastReceive getBluetoothBroadcastReceive() {
        return bluetoothBroadcastReceiver;
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

    public boolean checkPermissaoScan() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int permissionBluetoothScan = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_SCAN);
            return (permissionBluetoothScan == PackageManager.PERMISSION_GRANTED);
        }
        return true;

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
    public boolean checkBlutoothPermissionConnect() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int permissionBluetoothConnect= ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT);
            return (permissionBluetoothConnect == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }
    @SuppressLint("MissingPermission")
    public void checkAndEnableBluetoothAdapter() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int permissionBluetoothConnect = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT);
            if (permissionBluetoothConnect != PackageManager.PERMISSION_GRANTED) {
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

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BLUETOOTH) {
            Toast.makeText(appContext, R.string.message_bluetooth_activate_success, Toast.LENGTH_SHORT).show();
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(appContext, R.string.message_bluetooth_activate_important, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}