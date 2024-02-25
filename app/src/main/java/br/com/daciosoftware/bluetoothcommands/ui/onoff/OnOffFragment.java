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
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogAppPlus;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogInformation;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;
import br.com.daciosoftware.bluetoothcommands.database.AppDatabase;
import br.com.daciosoftware.bluetoothcommands.database.BluetoothCommandDatabase;
import br.com.daciosoftware.bluetoothcommands.database.dao.PortDao;
import br.com.daciosoftware.bluetoothcommands.database.entity.PortEntity;

public class OnOffFragment extends Fragment implements BluetoothManagerControl.ConnectionDevice {
    private Context appContext;
    private BluetoothManagerControl bluetoothManagerControl;
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
    private final boolean appPlus = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
        bluetoothManagerControl = BluetoothManagerControl.getInstance(context);
        bluetoothManagerControl.setListenerConnectionDevice(OnOffFragment.this);
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

        if (!appPlus) {
            new AlertDialogAppPlus(appContext).show();
        }

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit_label) {
                PortsEditLabelDialog portsEditLabelDialog = new PortsEditLabelDialog(appContext);
                portsEditLabelDialog.show();
                return true;
            }

            if (item.getItemId() == R.id.action_information_command) {
                AlertDialogInformation dialogInformation = new AlertDialogInformation(appContext);
                dialogInformation.show();
                return true;
            }

            return false;
        });

        editTextPort1.setOnClickListener(v -> {
            PortsDialog dialogPorts = new PortsDialog(appContext, editTextPort1, textViewLabelPort1.getText().toString());
            dialogPorts.show();
        });
        editTextPort2.setOnClickListener(v -> {
            PortsDialog dialogPorts = new PortsDialog(appContext, editTextPort2, textViewLabelPort2.getText().toString());
            dialogPorts.show();
        });
        editTextPort3.setOnClickListener(v -> {
            PortsDialog dialogPorts = new PortsDialog(appContext, editTextPort3, textViewLabelPort3.getText().toString());
            dialogPorts.show();
        });
        editTextPort4.setOnClickListener(v -> {
            PortsDialog dialogPorts = new PortsDialog(appContext, editTextPort4, textViewLabelPort4.getText().toString());
            dialogPorts.show();
        });

        toggleButton1.setOnClickListener(v -> {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                toggleButton1.setChecked(true);
                return;
            }
            if (bluetoothManagerControl.getDevicePaired() == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                toggleButton1.setChecked(true);
                return;
            }
            String port = editTextPort1.getText().toString();
            int signal = !toggleButton1.isChecked() ? 1 : 0;
            byte[] dado = (port + ";" + signal + "\n").getBytes();
            bluetoothManagerControl.write(dado);
        });

        toggleButton2.setOnClickListener(v -> {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                toggleButton2.setChecked(true);
                return;
            }
            if (bluetoothManagerControl.getDevicePaired() == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                toggleButton2.setChecked(true);
                return;
            }
            String port = editTextPort2.getText().toString();
            int signal = !toggleButton2.isChecked() ? 1 : 0;
            byte[] dado = (port + ";" + signal + "\n").getBytes();
            bluetoothManagerControl.write(dado);
        });

        toggleButton3.setOnClickListener(v -> {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                toggleButton3.setChecked(true);
                return;
            }
            if (bluetoothManagerControl.getDevicePaired() == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                toggleButton3.setChecked(true);
                return;
            }
            String port = editTextPort3.getText().toString();
            int signal = !toggleButton3.isChecked() ? 1 : 0;
            byte[] dado = (port + ";" + signal + "\n").getBytes();
            bluetoothManagerControl.write(dado);
        });

        toggleButton4.setOnClickListener(v -> {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                toggleButton4.setChecked(true);
                return;
            }
            if (bluetoothManagerControl.getDevicePaired() == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                toggleButton4.setChecked(true);
                return;
            }
            String port = editTextPort4.getText().toString();
            int signal = !toggleButton4.isChecked() ? 1 : 0;
            byte[] dado = (port + ";" + signal + "\n").getBytes();
            bluetoothManagerControl.write(dado);
        });

        updateStatusDevicePaired();
        updatePortsFromDatabase();

        return root;
    }

    @SuppressLint({"MissingPermission"})
    private void updateStatusDevicePaired() {
        BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
    }

    private void updatePortsFromDatabase() {
        AppDatabase db = BluetoothCommandDatabase.getInstance(appContext);
        PortDao portDao = db.portDao();
        List<PortEntity> listPorts = portDao.getAll();
        editTextPort1.setText("0");
        editTextPort2.setText("0");
        editTextPort3.setText("0");
        editTextPort4.setText("0");
        if (listPorts.size() > 0) {
            if (listPorts.get(0) != null) {
                PortEntity port1 = listPorts.get(0);
                editTextPort1.setText(String.valueOf(port1.pin));
                toggleButton1.setChecked(port1.signal);
            }
            if (listPorts.get(1) != null) {
                PortEntity port2 = listPorts.get(1);
                editTextPort2.setText(String.valueOf(port2.pin));
                toggleButton2.setChecked(port2.signal);
            }
            if (listPorts.get(2) != null) {
                PortEntity port3 = listPorts.get(2);
                editTextPort3.setText(String.valueOf(port3.pin));
                toggleButton3.setChecked(port3.signal);
            }
            if (listPorts.get(3) != null) {
                PortEntity port4 = listPorts.get(3);
                editTextPort4.setText(String.valueOf(port4.pin));
                toggleButton4.setChecked(port4.signal);
            }
        }
    }

    private void updatePortsToDatabase() {
        AppDatabase db = BluetoothCommandDatabase.getInstance(appContext);
        PortDao portDao = db.portDao();

        portDao.deleteAll();

        PortEntity port1 = new PortEntity();
        String pinPort1 = editTextPort1.getText().toString();
        try {
            port1.pin = Integer.parseInt(pinPort1);
        } catch (Exception e) {
            port1.pin = 0;
        }
        port1.signal = toggleButton1.isChecked();

        PortEntity port2 = new PortEntity();
        String pinPort2 = editTextPort2.getText().toString();
        try {
            port2.pin = Integer.parseInt(pinPort2);
        } catch (Exception e) {
            port2.pin = 0;
        }
        port2.signal = toggleButton2.isChecked();

        PortEntity port3 = new PortEntity();
        String pinPort3 = editTextPort3.getText().toString();
        try {
            port3.pin = Integer.parseInt(pinPort3);
        } catch (Exception e) {
            port3.pin = 0;
        }
        port3.signal = toggleButton3.isChecked();

        PortEntity port4 = new PortEntity();
        String pinPort4 = editTextPort4.getText().toString();
        try {
            port4.pin = Integer.parseInt(pinPort4);

        } catch (Exception e) {
            port4.pin = 0;
        }
        port4.signal = toggleButton4.isChecked();

        portDao.insertAll(port1, port2, port3, port4);

    }

    @Override
    public void initConnection() {

    }

    @Override
    public void postDeviceConnection() {

    }

    @Override
    public void postDeviceDisconnection() {
        Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
        updateStatusDevicePaired();
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
        updatePortsToDatabase();
    }

}