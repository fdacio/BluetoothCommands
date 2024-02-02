package br.com.daciosoftware.bluetoothcommands.ui.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import br.com.daciosoftware.bluetoothcommands.BluetoothConnection;
import br.com.daciosoftware.bluetoothcommands.BluetoothConnectionListener;
import br.com.daciosoftware.bluetoothcommands.BluetoothInstance;
import br.com.daciosoftware.bluetoothcommands.DevicesBluetoothAdapter;
import br.com.daciosoftware.bluetoothcommands.MainActivity;
import br.com.daciosoftware.bluetoothcommands.R;


import java.util.ArrayList;
import java.util.Set;

public class BluetoothFragment extends Fragment implements AdapterView.OnItemClickListener, BluetoothConnectionListener {

    private static final int REQUEST_PERMISSION_BLUETOOTH = 2;
    private BluetoothAdapter bluetoothAdapter;
    private final static int REQUEST_ENABLE_BLUETOOTH = 1;
    private final ArrayList<BluetoothDevice> listDevices = new ArrayList<BluetoothDevice>();
    private ListView listViewDevices;
    private BroadcastReceiver receiver;
    private ProgressDialog mProgressDlg;
    private Context mContext;
    private Toolbar toolbar;
    private Handler handlerUpdateStatusDeviceParead;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @SuppressLint({"HandlerLeak", "NonConstantResourceId"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        toolbar = root.findViewById(R.id.toolbarBluetooth);
        toolbar.inflateMenu(R.menu.menu_bluetooth);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_bluetooth_discovery: {
                    bluetoothAdapter.startDiscovery();
                    return true;
                }
                case R.id.action_bluetooth_disconnect: {
                    BluetoothConnection bluetoothConnection = BluetoothInstance.getInstance();
                    if (bluetoothConnection != null) {
                        bluetoothConnection.disconnect();
                    }
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.setDevicePaired(null);
                    updateMenuBluetooth();
                    return true;
                }
                default:
                    return false;
            }
        });

       updateMenuBluetooth();

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    listDevices.clear();
                    mProgressDlg.show();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    mProgressDlg.dismiss();
                    DevicesBluetoothAdapter devicesBluetoothAdapter = new DevicesBluetoothAdapter(mContext);
                    devicesBluetoothAdapter.setData(listDevices);
                    listViewDevices.setAdapter(devicesBluetoothAdapter);
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!listDevices.contains(device)) {
                        listDevices.add(device);
                    }
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    setDisconnected();
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                    setDisconnected();
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        mContext.getApplicationContext().registerReceiver(receiver, filter);

        listViewDevices = root.findViewById(R.id.listViewDevices);
        listViewDevices.setOnItemClickListener(this);

        final BluetoothManager bluetoothManager =  (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        mProgressDlg = new ProgressDialog(mContext);
        mProgressDlg.setTitle("Bluetooth");
        mProgressDlg.setMessage("Aguarde, procurando por dispositivos.");
        mProgressDlg.setCancelable(false);
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                bluetoothAdapter.cancelDiscovery();
            }
        });

        handlerUpdateStatusDeviceParead = new Handler() {
            @Override
            public void handleMessage(@NonNull Message message) {
                super.handleMessage(message);
                String msg = message.getData().getString("message");
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                updateMenuBluetooth();
            }
        };

        enableBluetooth();
        grantAccessLocation();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bluetooth, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void updateMenuBluetooth() {

        MainActivity mainActivity = (MainActivity) mContext;
        BluetoothDevice devicePaired = mainActivity.getDevicePaired();

        toolbar.getMenu().findItem(R.id.action_bluetooth_disconnect).setVisible(devicePaired != null);
        toolbar.getMenu().findItem(R.id.action_bluetooth_discovery).setVisible(devicePaired == null);
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);

    }

    private void grantAccessLocation() {
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionFineLocation = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if ( (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) || (permissionFineLocation != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_BLUETOOTH);
        }
    }

    private void enableBluetooth() {
        if (bluetoothAdapter == null) {
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {

            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    //Dispositivo pareados anteriormente
    private void loadDevicesBonded() {
        Set<BluetoothDevice> bondedDevice = bluetoothAdapter.getBondedDevices();
        listDevices.clear();
        for (BluetoothDevice device : bondedDevice) {
            listDevices.add(device);
        }
        DevicesBluetoothAdapter devicesBluetoothAdapter = new DevicesBluetoothAdapter(mContext);
        devicesBluetoothAdapter.setData(listDevices);
        listViewDevices.setAdapter(devicesBluetoothAdapter);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && requestCode == REQUEST_ENABLE_BLUETOOTH) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String bluetoothDeviceName = bluetoothAdapter.getName();
            String bluetoothDeviceAdrress = bluetoothAdapter.getAddress();
            Toast.makeText(mContext, bluetoothDeviceName + ":" + bluetoothDeviceAdrress, Toast.LENGTH_LONG).show();

            Intent discoverableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivity(discoverableBluetoothIntent);

        }
    }
    @Override
    public void onPause() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
        }
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        loadDevicesBonded();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            mContext.getApplicationContext().unregisterReceiver(receiver);
        }
    }

    //Pareamento de dispositivos no click do item da listview
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        MainActivity mainActivity = (MainActivity) mContext;
        BluetoothDevice devicePaired = mainActivity.getDevicePaired();

        if (devicePaired != null) {
            Toast.makeText(mContext, "Dispositivo já pareado", Toast.LENGTH_LONG).show();
            return;
        }
        BluetoothDevice newDevicePaired = listDevices.get(position);
        BluetoothConnection bluetoothConnection = new BluetoothConnection(newDevicePaired, this, mContext);
        BluetoothInstance.setInstance(bluetoothConnection);
        if (bluetoothConnection.getStatus() == AsyncTask.Status.PENDING || bluetoothConnection.getStatus() == AsyncTask.Status.FINISHED) {
            bluetoothConnection.execute();
        }
    }

    @Override
    public void setConnected(BluetoothDevice device) {
        MainActivity mainActivity = (MainActivity) mContext;
        mainActivity.setDevicePaired(device);
        updateMenuBluetooth();
    }

    @Override
    public void setDisconnected() {
        MainActivity mainActivity = (MainActivity) mContext;
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