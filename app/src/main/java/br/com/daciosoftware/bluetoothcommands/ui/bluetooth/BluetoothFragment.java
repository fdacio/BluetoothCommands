package br.com.daciosoftware.bluetoothcommands.ui.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Set;

import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothBroadcastReceive;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothConnectionExecutor;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothConnectionListener;
import br.com.daciosoftware.bluetoothcommands.MainActivity;
import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManager;

public class BluetoothFragment extends Fragment implements AdapterView.OnItemClickListener, BluetoothConnectionListener {

    private ArrayList<BluetoothDevice> listDevices;
    private ListView listViewDevices;
    private Context appContext;
    private Toolbar toolbar;
    private BluetoothConnectionExecutor bluetoothConnection;

    private BluetoothManager bluetoothManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bluetoothManager = new BluetoothManager(context);
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
                    bluetoothManager.requestPermissionBluetooth();
                    bluetoothManager.requestPermissionAccessLocation();
                    bluetoothManager.checkAndEnableBluetoothAdapter();
                    if (bluetoothManager.checkBluetoothPermissionScan()) {
                        BluetoothBroadcastReceive bluetoothBroadcastReceive = mainActivity.getBluetoothBroadcastReceive();
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

        listDevices = new ArrayList<>();
        listViewDevices = root.findViewById(R.id.listViewDevices);
        listViewDevices.setOnItemClickListener(this);

        bluetoothConnection = BluetoothConnectionExecutor.getInstance();
        if (bluetoothConnection != null) bluetoothConnection.setListener(BluetoothFragment.this);

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
        if (mainActivity.checkBlutoothPermissionConnect()) {
            BluetoothAdapter bluetoothAdapter = mainActivity.getBluetoothAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                listDevices.clear();
                Set<BluetoothDevice> bondedDevice = bluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : bondedDevice) {
                    listDevices.add(device);
                }
                DevicesBluetoothAdapter devicesBluetoothAdapter = new DevicesBluetoothAdapter(appContext);
                devicesBluetoothAdapter.setData(listDevices);
                listViewDevices.setAdapter(devicesBluetoothAdapter);
            }
        }
    }

    @SuppressLint({"MissingPermission"})
    private void bluetoothAdapterDisconnect() {
        BluetoothConnectionExecutor bluetoothConnection = BluetoothConnectionExecutor.getInstance();
        if (bluetoothConnection != null) {
            bluetoothConnection.disconnect();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
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
        if (mainActivity.getBluetoothAdapter().isEnabled()) {
            if (mainActivity.getDevicePaired() != null) {
                Toast.makeText(appContext, R.string.message_paired_device_already_exists, Toast.LENGTH_SHORT).show();
                return;
            }
            BluetoothDevice newDevicePaired = listDevices.get(position);
            BluetoothConnectionExecutor bluetoothConnection = BluetoothConnectionExecutor.builder(newDevicePaired, this, appContext);
            bluetoothConnection.execute();
        } else {
            Toast.makeText(appContext, R.string.message_bluetooth_adapter_dont_active, Toast.LENGTH_LONG).show();
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
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
            updateMenuBluetooth();
        });
    }

    @Override
    public void readFromDevicePaired(String dataReceiver) {
    }

}