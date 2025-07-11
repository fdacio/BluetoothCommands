package br.com.daciosoftware.bluetoothcommands.ui.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogDevicePaired;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogInformation;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogDevicePairing;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogProgress;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;

public class BluetoothFragment extends Fragment implements AdapterView.OnItemClickListener, BluetoothManagerControl.DiscoveryDevices, BluetoothManagerControl.ConnectionDevice {

    private List<BluetoothDevice> listDevices = new ArrayList<>();
    private ListView listViewDevices;
    private Context appContext;
    private Toolbar toolbar;
    private View buttonSearch;
    private View buttonDisconnect;
    private BluetoothManagerControl bluetoothManagerControl;
    private AlertDialogProgress alertDialogProgressStartDiscovery;
    private AlertDialogDevicePairing alertDialogProgressPairDevice;

    public BluetoothFragment(){}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
        bluetoothManagerControl = BluetoothManagerControl.getInstance(context);
        bluetoothManagerControl.setListenerDiscoveryDevices(BluetoothFragment.this);
        bluetoothManagerControl.setListenerConnectionDevice(BluetoothFragment.this);
        alertDialogProgressStartDiscovery = new AlertDialogProgress(context, AlertDialogProgress.TypeDialog.SEARCH_DEVICE);
        alertDialogProgressPairDevice = new AlertDialogDevicePairing(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        toolbar = root.findViewById(R.id.toolbarBluetooth);

        buttonSearch = root.findViewById(R.id.action_button_search);
        buttonDisconnect = root.findViewById(R.id.action_button_disconnect);

        buttonSearch.setOnClickListener((v) -> bluetoothManagerControl.initDiscovery());
        buttonDisconnect.setOnClickListener((v) -> bluetoothManagerControl.disconnect());

        listViewDevices = root.findViewById(R.id.listViewDevices);
        listViewDevices.setOnItemClickListener(this);

        updateMenuBluetooth();
        updateStatusDevicePaired();

        bluetoothManagerControl.write(String.format("%s\n", "tab:1").getBytes());

        return root;
    }

    @SuppressLint({"MissingPermission"})
    private void updateMenuBluetooth() {
        BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
        if (devicePaired != null) {
            buttonDisconnect.setVisibility(View.VISIBLE);
            buttonSearch.setVisibility(View.GONE);
        } else {
            buttonDisconnect.setVisibility(View.GONE);
            buttonSearch.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint({"MissingPermission"})
    private void loadDevicesBonded() {
        listDevices = bluetoothManagerControl.getBoundedDevices();
        BluetoothDevicesAdapter devicesBluetoothAdapter = new BluetoothDevicesAdapter(appContext, listDevices);
        listViewDevices.setAdapter(devicesBluetoothAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        bluetoothManagerControl.setListenerDiscoveryDevices(BluetoothFragment.this);
        bluetoothManagerControl.setListenerConnectionDevice(BluetoothFragment.this);
        loadDevicesBonded();
    }

    @SuppressLint({"MissingPermission"})
    private void updateStatusDevicePaired() {
        BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
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
        BluetoothDevicesAdapter adapter = new BluetoothDevicesAdapter(appContext, listDevices);
        listViewDevices.setAdapter(adapter);
        alertDialogProgressStartDiscovery.dismiss();
    }

    @Override
    public void foundDevice(BluetoothDevice device) {
        if (!listDevices.contains(device)) listDevices.add(device);
    }

    @Override
    public void initConnection(BluetoothDevice device) {
        alertDialogProgressPairDevice.show(device.getName());
    }

    @Override
    public void postDeviceConnection(BluetoothDevice device) {
        alertDialogProgressPairDevice.dismiss();
        AlertDialogDevicePaired alertDialogDevicePaired = new AlertDialogDevicePaired(appContext, AlertDialogDevicePaired.TypeDialog.SUCCESS_PAIR);
        alertDialogDevicePaired.show(device.getName());
        loadDevicesBonded();
        updateMenuBluetooth();
        updateStatusDevicePaired();

    }

    @Override
    public void postDeviceDisconnection() {
        Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
        updateMenuBluetooth();
        updateStatusDevicePaired();
        loadDevicesBonded();
    }

    @Override
    public void postFailConnection(BluetoothDevice device) {
        alertDialogProgressPairDevice.dismiss();
        AlertDialogDevicePaired alertDialogDevicePaired = new AlertDialogDevicePaired(appContext, AlertDialogDevicePaired.TypeDialog.FAIL_PAIR);
        alertDialogDevicePaired.show(device.getName());
    }

    @Override
    public void postDataReceived(String dataReceived) {
    }

}