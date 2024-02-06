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

import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogProgress;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothConnectionExecutor;
import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;

public class BluetoothFragment extends Fragment implements AdapterView.OnItemClickListener, BluetoothManagerControl.DiscoveryDevices, BluetoothManagerControl.Connection {

    private ArrayList<BluetoothDevice> listDevices;
    private ListView listViewDevices;
    private Context appContext;
    private Toolbar toolbar;
    private BluetoothManagerControl bluetoothManagerControl;

    private AlertDialogProgress alertDialogProgressStartDiscovery;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
        bluetoothManagerControl = BluetoothManagerControl.getInstance(context);
        alertDialogProgressStartDiscovery = new AlertDialogProgress(context, AlertDialogProgress.TypeDialog.SEARCH_DEVICE);
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
                    initDiscoveryDevices();
                    return true;
                }
                case R.id.action_bluetooth_disconnect: {
                    //aqui
                    return true;
                }
                default:
                    return false;
            }
        });

        listDevices = new ArrayList<>();
        listViewDevices = root.findViewById(R.id.listViewDevices);
        listViewDevices.setOnItemClickListener(this);

        return root;
    }

    @SuppressLint({"MissingPermission"})
    private void updateMenuBluetooth() {
        BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
        toolbar.getMenu().findItem(R.id.action_bluetooth_disconnect).setVisible(devicePaired != null);
        toolbar.getMenu().findItem(R.id.action_bluetooth_discovery).setVisible(devicePaired == null);
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
    }

    @SuppressLint({"MissingPermission"})
    private void loadDevicesBonded() {
        //Dispositivo pareados anteriormente
        if (bluetoothManagerControl.checkBlutoothPermissionConnect()) {
            BluetoothAdapter bluetoothAdapter = bluetoothManagerControl.getBluetoothAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                listDevices.clear();
                Set<BluetoothDevice> bondedDevice = bluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : bondedDevice) {
                    listDevices.add(device);
                }
                DevicesBluetoothAdapter devicesBluetoothAdapter = new DevicesBluetoothAdapter(appContext, listDevices);
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

    @Override
    public void onStop() {
        super.onStop();
    }

    //Pareamento de dispositivos no click do item da listview
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (bluetoothManagerControl.getBluetoothAdapter().isEnabled()) {
            if (bluetoothManagerControl.getDevicePaired() != null) {
                Toast.makeText(appContext, R.string.message_paired_device_already_exists, Toast.LENGTH_SHORT).show();
                return;
            }
            //BluetoothDevice newDevicePaired = listDevices.get(position);
            //bluetoothManagerControl.setBluetoothAdapter(newDevicePaired);
            //BluetoothConnectionExecutor bluetoothConnection = BluetoothConnectionExecutor.builder(newDevicePaired, this, appContext);
            //bluetoothConnection.execute();
        } else {
            Toast.makeText(appContext, R.string.message_bluetooth_adapter_dont_active, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void initDiscoveryDevices() {

        alertDialogProgressStartDiscovery.show();
    }

    @Override
    public void finishDiscoveryDevices() {
        DevicesBluetoothAdapter  adapter = new DevicesBluetoothAdapter(appContext, listDevices);
        listViewDevices.setAdapter(adapter);
        alertDialogProgressStartDiscovery.dismiss();
    }

    @Override
    public void foundDevice(BluetoothDevice device) {
        listDevices.add(device);
    }

    @Override
    public void postDeviceConnect(BluetoothDevice device) {
        bluetoothManagerControl.setDevicePaired(device);
        updateMenuBluetooth();
    }

    @Override
    public void postDeviceDisconnect() {
        bluetoothManagerControl.setDevicePaired(null);
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
            updateMenuBluetooth();
        });
    }

    @Override
    public void postDataReceive(String dataReceive) {

    }
}