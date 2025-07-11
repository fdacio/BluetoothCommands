package br.com.daciosoftware.bluetoothcommands.ui.onoff;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogInformationOnOff;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogProgress;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;
import br.com.daciosoftware.bluetoothcommands.database.AppDatabase;
import br.com.daciosoftware.bluetoothcommands.database.BluetoothCommandDatabase;
import br.com.daciosoftware.bluetoothcommands.database.dao.PortDao;
import br.com.daciosoftware.bluetoothcommands.database.entity.PortEntity;

public class OnOffFragment extends Fragment implements BluetoothManagerControl.ConnectionDevice {
    private Context appContext;
    private BluetoothManagerControl bluetoothManagerControl;
    private PortDao portDao;
    private Toolbar toolbar;
    private TextView textViewLabelPort1;
    private TextView textViewLabelPort2;
    private TextView textViewLabelPort3;
    private TextView textViewLabelPort4;
    private EditText editTextPort1;
    private EditText editTextPort2;
    private EditText editTextPort3;
    private EditText editTextPort4;
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
        AppDatabase db = BluetoothCommandDatabase.getInstance(appContext);
        portDao = db.portDao();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_on_off, container, false);
        toolbar = root.findViewById(R.id.toolbarOnOff);
        toolbar.inflateMenu(R.menu.menu_on_off);
        textViewLabelPort1 = root.findViewById(R.id.textViewLabelPort1);
        textViewLabelPort2 = root.findViewById(R.id.textViewLabelPort2);
        textViewLabelPort3 = root.findViewById(R.id.textViewLabelPort3);
        textViewLabelPort4 = root.findViewById(R.id.textViewLabelPort4);
        editTextPort1 = root.findViewById(R.id.editTextPort1);
        editTextPort2 = root.findViewById(R.id.editTextPort2);
        editTextPort3 = root.findViewById(R.id.editTextPort3);
        editTextPort4 = root.findViewById(R.id.editTextPort4);
        toggleButton1 = root.findViewById(R.id.toggleButton1);
        toggleButton2 = root.findViewById(R.id.toggleButton2);
        toggleButton3 = root.findViewById(R.id.toggleButton3);
        toggleButton4 = root.findViewById(R.id.toggleButton4);

        toolbar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_reload_stats) {
                if (bluetoothManagerControl.getDevicePaired() == null) {
                    Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                    return true;
                }
                AlertDialogProgress dialog = new AlertDialogProgress(appContext, AlertDialogProgress.TypeDialog.WAIT);
                dialog.show(6);
                bluetoothManagerControl.write(String.format("%s\n", "tab:3").getBytes());
                return true;
            }

            if (item.getItemId() == R.id.action_edit_label) {
                updatePortsToDatabase();
                PortsEditLabelDialog portsEditLabelDialog = new PortsEditLabelDialog(appContext, OnOffFragment.this);
                portsEditLabelDialog.show();
                return true;
            }

            if (item.getItemId() == R.id.action_information_command) {
                AlertDialogInformationOnOff dialogInformation = new AlertDialogInformationOnOff(appContext);
                dialogInformation.show();
                return true;
            }
            return false;
        });

        editTextPort1.setOnClickListener(v -> {
            PortsListDialog dialogPorts = new PortsListDialog(appContext, editTextPort1, textViewLabelPort1.getText().toString());
            dialogPorts.show();
        });
        editTextPort2.setOnClickListener(v -> {
            PortsListDialog dialogPorts = new PortsListDialog(appContext, editTextPort2, textViewLabelPort2.getText().toString());
            dialogPorts.show();
        });
        editTextPort3.setOnClickListener(v -> {
            PortsListDialog dialogPorts = new PortsListDialog(appContext, editTextPort3, textViewLabelPort3.getText().toString());
            dialogPorts.show();
        });
        editTextPort4.setOnClickListener(v -> {
            PortsListDialog dialogPorts = new PortsListDialog(appContext, editTextPort4, textViewLabelPort4.getText().toString());
            dialogPorts.show();
        });

        toggleButton1.setOnClickListener(v -> {
            if (bluetoothManagerControl.getDevicePaired() == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                toggleButton1.setChecked(true);
                return;
            }
            String port = editTextPort1.getText().toString();
            int signal = !toggleButton1.isChecked() ? 1 : 0;
            byte[] dado = (port + ":" + signal + "\n").getBytes();
            bluetoothManagerControl.write(dado);
        });

        toggleButton2.setOnClickListener(v -> {
            if (bluetoothManagerControl.getDevicePaired() == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                toggleButton2.setChecked(true);
                return;
            }
            String port = editTextPort2.getText().toString();
            int signal = !toggleButton2.isChecked() ? 1 : 0;
            byte[] dado = (port + ":" + signal + "\n").getBytes();
            bluetoothManagerControl.write(dado);
        });

        toggleButton3.setOnClickListener(v -> {
            if (bluetoothManagerControl.getDevicePaired() == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                toggleButton3.setChecked(true);
                return;
            }
            String port = editTextPort3.getText().toString();
            int signal = !toggleButton3.isChecked() ? 1 : 0;
            byte[] dado = (port + ":" + signal + "\n").getBytes();
            bluetoothManagerControl.write(dado);
        });

        toggleButton4.setOnClickListener(v -> {
            if (bluetoothManagerControl.getDevicePaired() == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                toggleButton4.setChecked(true);
                return;
            }
            String port = editTextPort4.getText().toString();
            int signal = !toggleButton4.isChecked() ? 1 : 0;
            byte[] dado = (port + ":" + signal + "\n").getBytes();
            bluetoothManagerControl.write(dado);
        });

        updateStatusDevicePaired();
        insertPortsToDatabase();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        bluetoothManagerControl.setListenerConnectionDevice(OnOffFragment.this);
    }

    @SuppressLint({"MissingPermission"})
    private void updateStatusDevicePaired() {
        BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
    }

    private void updatePortsToDatabase() {

        List<PortEntity> listPorts = portDao.getAll();

        if (!listPorts.isEmpty()) {
            if (listPorts.get(0) != null) {
                PortEntity port1 = listPorts.get(0);
                port1.pin = Integer.parseInt(editTextPort1.getText().toString());
                port1.signal = toggleButton1.isChecked();
                port1.label = textViewLabelPort1.getText().toString();
                portDao.update(port1);
            }
            if (listPorts.get(1) != null) {
                PortEntity port2 = listPorts.get(1);
                port2.pin = Integer.parseInt(editTextPort2.getText().toString());
                port2.signal = toggleButton2.isChecked();
                port2.label = textViewLabelPort2.getText().toString();
                portDao.update(port2);
            }
            if (listPorts.get(2) != null) {
                PortEntity port3 = listPorts.get(2);
                port3.pin = Integer.parseInt(editTextPort3.getText().toString());
                port3.signal = toggleButton3.isChecked();
                port3.label = textViewLabelPort3.getText().toString();
                portDao.update(port3);
            }
            if (listPorts.get(3) != null) {
                PortEntity port4 = listPorts.get(3);
                port4.pin = Integer.parseInt(editTextPort4.getText().toString());
                port4.signal = toggleButton4.isChecked();
                port4.label = textViewLabelPort4.getText().toString();
                portDao.update(port4);
            }
        }
    }

    private void updatePortsToView() {

        List<PortEntity> listPorts = portDao.getAll();

        if (!listPorts.isEmpty()) {
            if (listPorts.get(0) != null) {
                PortEntity port1 = listPorts.get(0);
                editTextPort1.setText(String.valueOf(port1.pin));
                toggleButton1.setChecked(port1.signal);
                textViewLabelPort1.setText(port1.label);
            }
            if (listPorts.get(1) != null) {
                PortEntity port2 = listPorts.get(1);
                editTextPort2.setText(String.valueOf(port2.pin));
                toggleButton2.setChecked(port2.signal);
                textViewLabelPort2.setText(port2.label);
            }
            if (listPorts.get(2) != null) {
                PortEntity port3 = listPorts.get(2);
                editTextPort3.setText(String.valueOf(port3.pin));
                toggleButton3.setChecked(port3.signal);
                textViewLabelPort3.setText(port3.label);
            }
            if (listPorts.get(3) != null) {
                PortEntity port4 = listPorts.get(3);
                editTextPort4.setText(String.valueOf(port4.pin));
                toggleButton4.setChecked(port4.signal);
                textViewLabelPort4.setText(port4.label);
            }
        }
    }

    private void insertPortsToDatabase() {

        List<PortEntity> listPorts = portDao.getAll();

        if (listPorts.isEmpty()) {
            PortEntity port1 = new PortEntity();
            String pinPort1 = editTextPort1.getText().toString();
            try {
                port1.pin = Integer.parseInt(pinPort1);
            } catch (Exception e) {
                port1.pin = 0;
            }
            port1.signal = toggleButton1.isChecked();
            port1.label = textViewLabelPort1.getText().toString();

            PortEntity port2 = new PortEntity();
            String pinPort2 = editTextPort1.getText().toString();
            try {
                port2.pin = Integer.parseInt(pinPort2);
            } catch (Exception e) {
                port2.pin = 0;
            }
            port2.signal = toggleButton2.isChecked();
            port2.label = textViewLabelPort2.getText().toString();

            PortEntity port3 = new PortEntity();
            String pinPort3 = editTextPort3.getText().toString();
            try {
                port3.pin = Integer.parseInt(pinPort3);
            } catch (Exception e) {
                port3.pin = 0;
            }
            port3.signal = toggleButton3.isChecked();
            port3.label = textViewLabelPort3.getText().toString();

            PortEntity port4 = new PortEntity();
            String pinPort4 = editTextPort4.getText().toString();
            try {
                port4.pin = Integer.parseInt(pinPort4);
            } catch (Exception e) {
                port4.pin = 0;
            }
            port4.signal = toggleButton4.isChecked();
            port4.label = textViewLabelPort4.getText().toString();

            portDao.insertAll(port1, port2, port3, port4);

        }
    }

    @Override
    public void initConnection(BluetoothDevice device) {

    }

    @Override
    public void postDeviceConnection(BluetoothDevice device) {

    }

    @Override
    public void postDeviceDisconnection() {
        Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
        updateStatusDevicePaired();
    }

    @Override
    public void postFailConnection(BluetoothDevice device) {

    }

    @Override
    public void postDataReceived(String dataReceived) {
        String[] data = dataReceived.split(":");
        String port = data[0];
        String signal = data[1];
        List<PortEntity> listPorts = portDao.getAll();
        if (!listPorts.isEmpty()) {
            if (listPorts.get(0) != null) {
                PortEntity port1 = listPorts.get(0);
                if (port1.pin == Integer.parseInt(port))
                    toggleButton1.setChecked(signal.equals("0"));
            }
            if (listPorts.get(1) != null) {
                PortEntity port2 = listPorts.get(1);
                if (port2.pin == Integer.parseInt(port))
                    toggleButton2.setChecked(signal.equals("0"));
            }
            if (listPorts.get(2) != null) {
                PortEntity port3 = listPorts.get(2);
                if (port3.pin == Integer.parseInt(port))
                    toggleButton3.setChecked(signal.equals("0"));
            }
            if (listPorts.get(3) != null) {
                PortEntity port4 = listPorts.get(3);
                if (port4.pin == Integer.parseInt(port))
                    toggleButton4.setChecked(signal.equals("0"));
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        updatePortsToView();
    }

    @Override
    public void onStop() {
        super.onStop();
        updatePortsToDatabase();
    }

}