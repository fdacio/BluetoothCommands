package br.com.daciosoftware.bluetoothcommands.ui.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Set;

import br.com.daciosoftware.bluetoothcommands.BluetoothBroadcastReceive;
import br.com.daciosoftware.bluetoothcommands.BluetoothConnection;
import br.com.daciosoftware.bluetoothcommands.BluetoothConnectionListener;
import br.com.daciosoftware.bluetoothcommands.BluetoothInstance;
import br.com.daciosoftware.bluetoothcommands.MainActivity;
import br.com.daciosoftware.bluetoothcommands.R;

public class BluetoothFragment extends Fragment implements AdapterView.OnItemClickListener, BluetoothConnectionListener {

    private ArrayList<BluetoothDevice> listDevices;
    private ListView listViewDevices;
    private Context appContext;
    private Toolbar toolbar;
    private Handler handlerUpdateStatusDeviceParead;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
    }

    @SuppressLint({"HandlerLeak", "NonConstantResourceId", "MissingPermission"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        toolbar = root.findViewById(R.id.toolbarBluetooth);
        toolbar.inflateMenu(R.menu.menu_bluetooth);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_bluetooth_discovery: {
                    MainActivity mainActivity = (MainActivity) appContext;
                    mainActivity.requestPermissionBluetooth();
                    mainActivity.requestPermissionAccessLocation();
                    mainActivity.checkAndEnableBluetoothAdapter();
                    int permissionBluetoothScan = ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_SCAN);
                    if (permissionBluetoothScan == PackageManager.PERMISSION_GRANTED) {
                        BluetoothBroadcastReceive bluetoothBroadcastReceive = mainActivity.getBluetoothBroadcastReceive();
                        listDevices = new ArrayList<>();
                        bluetoothBroadcastReceive.actionDiscoveryStarted(listDevices, listViewDevices);
                        mainActivity.getBluetoothAdapter().startDiscovery();
                    }
                    return true;
                }
                case R.id.action_bluetooth_disconnect: {
                    MainActivity mainActivity = (MainActivity) appContext;
                    mainActivity.checkAndEnableBluetoothAdapter();
                    BluetoothAdapter bluetoothAdapter = mainActivity.getBluetoothAdapter();
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    mainActivity.setDevicePaired(null);
                    updateMenuBluetooth();
                    bluetoothAdapterDisconnect();
                    return true;
                }
                default:
                    return false;
            }
        });

        listViewDevices = root.findViewById(R.id.listViewDevices);
        listViewDevices.setOnItemClickListener(this);

        handlerUpdateStatusDeviceParead = new Handler() {
            @Override
            public void handleMessage(@NonNull Message message) {
                super.handleMessage(message);
                String msg = message.getData().getString("message");
                Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
                updateMenuBluetooth();
            }
        };

        return root;
    }

    @SuppressLint({"MissingPermission"})
    private void updateMenuBluetooth() {
        MainActivity mainActivity = (MainActivity) appContext;
        BluetoothDevice devicePaired = mainActivity.getDevicePaired();
        toolbar.getMenu().findItem(R.id.action_bluetooth_disconnect).setVisible(devicePaired != null);
        toolbar.getMenu().findItem(R.id.action_bluetooth_discovery).setVisible(devicePaired == null);
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
    }

    @SuppressLint({"MissingPermission"})
    private void loadDevicesBonded() {
        //Dispositivo pareados anteriormente
        MainActivity mainActivity = (MainActivity) appContext;
        BluetoothAdapter bluetoothAdapter = mainActivity.getBluetoothAdapter();
        if (bluetoothAdapter == null) return;
        Set<BluetoothDevice> bondedDevice = bluetoothAdapter.getBondedDevices();
        listDevices = new ArrayList<>();
        for (BluetoothDevice device : bondedDevice) {
            listDevices.add(device);
        }
        DevicesBluetoothAdapter devicesBluetoothAdapter = new DevicesBluetoothAdapter(appContext);
        devicesBluetoothAdapter.setData(listDevices);
        listViewDevices.setAdapter(devicesBluetoothAdapter);
    }

    @SuppressLint({"MissingPermission"})
    private void bluetoothAdapterDisconnect() {
        BluetoothConnection bluetoothConnection = BluetoothInstance.getInstance();
        if (bluetoothConnection != null) {
            bluetoothConnection.disconnect();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bluetooth, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDevicesBonded();
        updateMenuBluetooth();
    }

    //Pareamento de dispositivos no click do item da listview
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        MainActivity mainActivity = (MainActivity) appContext;
        mainActivity.checkAndEnableBluetoothAdapter();
        BluetoothDevice devicePaired = mainActivity.getDevicePaired();
        if (devicePaired != null) {
            return;
        }
        BluetoothDevice newDevicePaired = listDevices.get(position);
        BluetoothConnection bluetoothConnection = new BluetoothConnection(newDevicePaired, this, appContext);
        BluetoothInstance.setInstance(bluetoothConnection);
        if (bluetoothConnection.getStatus() == AsyncTask.Status.PENDING || bluetoothConnection.getStatus() == AsyncTask.Status.FINISHED) {
            bluetoothConnection.execute();
        }
    }

    @Override
    public void setConnected(BluetoothDevice device) {
        MainActivity mainActivity = (MainActivity) appContext;
        mainActivity.setDevicePaired(device);
        updateMenuBluetooth();
    }

    @Override
    public void setDisconnected() {
        MainActivity mainActivity = (MainActivity) appContext;
        mainActivity.setDevicePaired(null);
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("message", "Dispositivo despareado.");
        msg.setData(bundle);
        handlerUpdateStatusDeviceParead.sendMessage(msg);
    }

    @Override
    public void readFromDevicePaired(String dataReceiver) {
    }

}