package br.com.daciosoftware.bluetoothcommands.ui.robocar;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogAppPlus;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogInformationRoboCar;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;

public class RoboCarFragment extends Fragment implements BluetoothManagerControl.ConnectionDevice {
    private Context appContext;
    private Toolbar toolbar;
    private TextView textViewDistanceValue;
    private final Boolean appPlus = false;
    private BluetoothManagerControl bluetoothManagerControl;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
        bluetoothManagerControl = BluetoothManagerControl.getInstance(context);
        bluetoothManagerControl.setListenerConnectionDevice(RoboCarFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_robocar, container, false);

        toolbar = root.findViewById(R.id.toolbarRoboCar);
        toolbar.inflateMenu(R.menu.menu_robocar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_information_command_robocar) {
                AlertDialogInformationRoboCar dialogInformation = new AlertDialogInformationRoboCar(appContext);
                dialogInformation.show();
                return true;
            }
            return false;
        });

        textViewDistanceValue = root.findViewById(R.id.textViewDistanceValue);

        ImageButton imageButtonUp = root.findViewById(R.id.imageButtonUp);
        imageButtonUp.setOnClickListener(v -> {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                return;
            }
            bluetoothManagerControl.write(String.format("%s\n", "up").getBytes());
        });

        ImageButton imageButtonRight = root.findViewById(R.id.imageButtonRight);
        imageButtonRight.setOnClickListener(v -> {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                return;
            }
            bluetoothManagerControl.write(String.format("%s\n", "rgt").getBytes());
        });

        ImageButton imageButtonDown = root.findViewById(R.id.imageButtonDown);
        imageButtonDown.setOnClickListener(v -> {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                return;
            }
            bluetoothManagerControl.write(String.format("%s\n", "dwn").getBytes());
        });

        ImageButton imageButtonLeft = root.findViewById(R.id.imageButtonLeft);
        imageButtonLeft.setOnClickListener(v -> {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                return;
            }
            bluetoothManagerControl.write(String.format("%s\n", "lft").getBytes());
        });

        ImageButton imageButtonStop = root.findViewById(R.id.imageButtonStop);
        imageButtonStop.setOnClickListener(v -> {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                return;
            }
            bluetoothManagerControl.write(String.format("%s\n", "stp").getBytes());
        });

        SeekBar seekBarSpeed = root.findViewById(R.id.seekBarSpeed);
        TextView textViewSendSpeed = root.findViewById(R.id.textViewSendSpeed);
        seekBarSpeed.setOnSeekBarChangeListener(new SeekBarChange(textViewSendSpeed));

        if (!appPlus) {
            new AlertDialogAppPlus(appContext).show();
        }

        return root;
    }

    @SuppressLint({"MissingPermission"})
    private void updateStatusDevicePaired() {
        BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
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
        if (dataReceived.contains("dtc")) {
            String[] aux = dataReceived.split(":");
            String distance = aux[1];
            textViewDistanceValue.setText(distance);
        }
    }
    private class SeekBarChange implements SeekBar.OnSeekBarChangeListener {
        private final TextView text;
        public SeekBarChange(TextView text) {
            this.text = text;
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.text.setText(String.format(Locale.getDefault(), "%d", progress));
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendData(seekBar);
        }
        private void sendData(@NonNull SeekBar seekBar) {
            if (!appPlus) {
                new AlertDialogAppPlus(appContext).show();
                return;
            }
            BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
            if (devicePaired == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                return;
            }
            if (seekBar.getId() == R.id.seekBarSpeed) {
                String command = String.format(Locale.getDefault(), "spd:%d\n", seekBar.getProgress());
                bluetoothManagerControl.write(command.getBytes());
            }
        }
    }
}