package br.com.daciosoftware.bluetoothcommands.ui.onoff;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.MainActivity;
import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;
import br.com.daciosoftware.bluetoothcommands.database.AppDatabase;
import br.com.daciosoftware.bluetoothcommands.database.dao.PortDao;
import br.com.daciosoftware.bluetoothcommands.database.entity.Port;

public class OnOffFragment extends Fragment implements BluetoothManagerControl.ConnectionDevice {
    private Context appContext;
    private BluetoothManagerControl bluetoothManagerControl;
    private Toolbar toolbar;
    private Spinner spinnerPort1;
    private Spinner spinnerPort2;
    private Spinner spinnerPort3;
    private Spinner spinnerPort4;
    private ToggleButton toggleButton1;
    private ToggleButton toggleButton2;
    private ToggleButton toggleButton3;
    private ToggleButton toggleButton4;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
        bluetoothManagerControl = BluetoothManagerControl.getInstance(context);
        bluetoothManagerControl.setListenerConnectionDevice(OnOffFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_on_off, container, false);
        toolbar = root.findViewById(R.id.toolbarOnOff);

        int MAX_PORT = 40;
        Integer[] ports = new Integer[MAX_PORT];
        for (int p = 0; p < MAX_PORT; p++) {
            ports[p]  = Integer.valueOf(p);
        }
        ArrayAdapter<Integer> adapterPorts = new ArrayAdapter<>(appContext, android.R.layout.simple_spinner_item, ports);

        spinnerPort1 = root.findViewById(R.id.spinnerPort1);
        spinnerPort1.setAdapter(adapterPorts);

        spinnerPort2 = root.findViewById(R.id.spinnerPort2);
        spinnerPort2.setAdapter(adapterPorts);

        spinnerPort3 = root.findViewById(R.id.spinnerPort3);
        spinnerPort3.setAdapter(adapterPorts);

        spinnerPort4 = root.findViewById(R.id.spinnerPort4);
        spinnerPort4.setAdapter(adapterPorts);

        toggleButton1 = root.findViewById(R.id.toggleButton1);
        toggleButton2 = root.findViewById(R.id.toggleButton2);
        toggleButton3 = root.findViewById(R.id.toggleButton3);
        toggleButton4 = root.findViewById(R.id.toggleButton4);

        updateStatusDeveiceParead();

        MainActivity mainActivity = (MainActivity) appContext;
        AppDatabase db =  mainActivity.getDatabase();
        PortDao portDao = db.portDao();
        List<Port> listPorts = portDao.getAll();

        if (listPorts.size() > 0) {
            if (listPorts.get(0) != null){
                Port port1 = listPorts.get(0);
                int selectionPosition = adapterPorts.getPosition(port1.pin);
                spinnerPort1.setSelection(selectionPosition);
                toggleButton1.setChecked(port1.signal);
            }
            if (listPorts.get(1) != null) {
                Port port2 = listPorts.get(1);
                int selectionPosition = adapterPorts.getPosition(port2.pin);
                spinnerPort2.setSelection(selectionPosition);
                toggleButton2.setChecked(port2.signal);
            }
            if (listPorts.get(2) != null) {
                Port port3 = listPorts.get(2);
                int selectionPosition = adapterPorts.getPosition(port3.pin);
                spinnerPort3.setSelection(selectionPosition);
                toggleButton3.setChecked(port3.signal);
            }
            if (listPorts.get(3) != null) {
                Port port4 = listPorts.get(3);
                int selectionPosition = adapterPorts.getPosition(port4.pin);
                spinnerPort4.setSelection(selectionPosition);
                toggleButton4.setChecked(port4.signal);
            }
        }

        return root;
    }

    @SuppressLint({"MissingPermission"})
    private void updateStatusDeveiceParead() {
        BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
    }
    @Override
    public void initConnection() {

    }
    @Override
    public void postDeviceConnection() {

    }
    @Override
    public void postDeviceDisconnection() {

    }
    @Override
    public void postFailConnection() {

    }
    @Override
    public void postDataReceived(String dataReceived) {

    }
    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) appContext;
        AppDatabase db =  mainActivity.getDatabase();
        PortDao portDao = db.portDao();
        if (portDao.getAll().size() == 0) {
            Port port1 = new Port();
            port1.pin = (Integer) spinnerPort1.getSelectedItem();
            port1.signal = toggleButton1.isChecked();

            Port port2 = new Port();
            port2.pin = (Integer) spinnerPort2.getSelectedItem();
            port2.signal = toggleButton2.isChecked();

            Port port3 = new Port();
            port3.pin = (Integer) spinnerPort3.getSelectedItem();
            port3.signal = toggleButton3.isChecked();

            Port port4 = new Port();
            port4.pin = (Integer) spinnerPort4.getSelectedItem();
            port4.signal = toggleButton4.isChecked();

            portDao.insertAll(port1, port2, port3, port4);

        } else {
            Port port1 = portDao.getAll().get(0);
            port1.pin = (Integer) spinnerPort1.getSelectedItem();
            port1.signal = toggleButton1.isChecked();

            Port port2 = portDao.getAll().get(1);
            port2.pin = (Integer) spinnerPort2.getSelectedItem();
            port2.signal = toggleButton2.isChecked();

            Port port3 = portDao.getAll().get(2);
            port3.pin = (Integer) spinnerPort3.getSelectedItem();
            port3.signal = toggleButton3.isChecked();

            Port port4 = portDao.getAll().get(3);
            port4.pin = (Integer) spinnerPort4.getSelectedItem();
            port4.signal = toggleButton4.isChecked();
            portDao.updatePorts(port1, port2, port3, port4);
        }
    }

}