package br.com.daciosoftware.bluetoothcommands.ui.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
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
import java.util.List;

import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogProgress;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;

public class BluetoothFragment extends Fragment implements AdapterView.OnItemClickListener, BluetoothManagerControl.DiscoveryDevices, BluetoothManagerControl.ConnectionDevice {

    private List<BluetoothDevice> listDevices = new ArrayList<>();
    private ListView listViewDevices;
    private Context appContext;
    private Toolbar toolbar;
    private BluetoothManagerControl bluetoothManagerControl;

    private AlertDialogProgress alertDialogProgressStartDiscovery;

    private AlertDialogProgress alertDialogProgressPairDevice;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;

        bluetoothManagerControl = BluetoothManagerControl.getInstance(context);
        bluetoothManagerControl.setListenerDiscoveryDevices(BluetoothFragment.this);
        bluetoothManagerControl.setListenerConnectionDevice(BluetoothFragment.this);

        alertDialogProgressStartDiscovery = new AlertDialogProgress(context, AlertDialogProgress.TypeDialog.SEARCH_DEVICE);
        alertDialogProgressPairDevice = new AlertDialogProgress(context, AlertDialogProgress.TypeDialog.PAIR_DEVICE);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        toolbar = root.findViewById(R.id.toolbarBluetooth);
        toolbar.inflateMenu(R.menu.menu_bluetooth);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_bluetooth_discovery: {
                    bluetoothManagerControl.initDiscovery();
                    return true;
                }
                case R.id.action_bluetooth_disconnect: {
                    bluetoothManagerControl.disconnect();
                    return true;
                }
                default:
                    return false;
            }
        });

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
        listDevices = bluetoothManagerControl.getBoundedDevices();
        DevicesBluetoothAdapter devicesBluetoothAdapter = new DevicesBluetoothAdapter(appContext, listDevices);
        listViewDevices.setAdapter(devicesBluetoothAdapter);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Pareamento de dispositivos no click do item da listview
        if (bluetoothManagerControl.getDevicePaired() != null) {
            Toast.makeText(appContext, R.string.message_paired_device_already_exists, Toast.LENGTH_SHORT).show();
            return;
        }
        BluetoothDevice newDevicePaired = listDevices.get(position);
        bluetoothManagerControl.connect(newDevicePaired);

    }

    @Override
    public void initDiscoveryDevices() {
        alertDialogProgressStartDiscovery.show();
    }

    @Override
    public void finishDiscoveryDevices() {
        DevicesBluetoothAdapter adapter = new DevicesBluetoothAdapter(appContext, listDevices);
        listViewDevices.setAdapter(adapter);
        alertDialogProgressStartDiscovery.dismiss();
    }

    @Override
    public void foundDevice(BluetoothDevice device) {
        if (!listDevices.contains(device)) {
            listDevices.add(device);
        }
    }

    @Override
    public void initConnection() {
        alertDialogProgressPairDevice.show();
    }

    @Override
    public void postDeviceConnect() {
        updateMenuBluetooth();
        alertDialogProgressPairDevice.dismiss();
    }

    @Override
    public void postDeviceDisconnect() {
        Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
        updateMenuBluetooth();
    }

    @Override
    public void postDataReceive(String dataReceive) {
    }

}