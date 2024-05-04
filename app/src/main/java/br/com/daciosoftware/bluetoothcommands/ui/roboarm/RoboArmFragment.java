package br.com.daciosoftware.bluetoothcommands.ui.roboarm;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogAppPlus;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogInformationRoboArm;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;

public class RoboArmFragment extends Fragment implements BluetoothManagerControl.ConnectionDevice {

    private Context appContext;
    private SeekBar seekBarServoBase;
    private SeekBar seekBarServoAltura;
    private SeekBar seekBarServoAlcance;
    private SeekBar seekBarServoGarra;
    private Toolbar toolbar;
    private BluetoothManagerControl bluetoothManagerControl;
    private final boolean appPlus = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
        bluetoothManagerControl = BluetoothManagerControl.getInstance(context);
        bluetoothManagerControl.setListenerConnectionDevice(RoboArmFragment.this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_roboarm, container, false);

        toolbar = root.findViewById(R.id.toolbarRoboarm);
        toolbar.inflateMenu(R.menu.menu_roboarm);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_information_command_roboarm) {
                AlertDialogInformationRoboArm dialogInformation = new AlertDialogInformationRoboArm(appContext);
                dialogInformation.show();
                return true;
            }
            return false;
        });

        seekBarServoBase = root.findViewById(R.id.seekBarServoBase);
        seekBarServoAltura = root.findViewById(R.id.seekBarServoAltura);
        seekBarServoAlcance = root.findViewById(R.id.seekBarServoAlcance);
        seekBarServoGarra = root.findViewById(R.id.seekBarSpeed);

        TextView textViewValorBase = root.findViewById(R.id.textViewValorAnguloBase);
        TextView textViewValorAltura = root.findViewById(R.id.textViewValorAnguloAltura);
        TextView textViewValorAlcance = root.findViewById(R.id.textViewValorAnguloAlcance);
        TextView textViewValorGarra = root.findViewById(R.id.textViewValorAnguloGarra);

        seekBarServoBase.setOnSeekBarChangeListener(new SeekBarChange(textViewValorBase));
        seekBarServoAltura.setOnSeekBarChangeListener(new SeekBarChange(textViewValorAltura));
        seekBarServoAlcance.setOnSeekBarChangeListener(new SeekBarChange(textViewValorAlcance));
        seekBarServoGarra.setOnSeekBarChangeListener(new SeekBarChange(textViewValorGarra));

        textViewValorBase.setText(String.format(Locale.getDefault(), "%d°", seekBarServoBase.getProgress()));
        textViewValorAltura.setText(String.format(Locale.getDefault(), "%d°", seekBarServoAltura.getProgress()));
        textViewValorAlcance.setText(String.format(Locale.getDefault(), "%d°", seekBarServoAlcance.getProgress()));
        textViewValorGarra.setText(String.format(Locale.getDefault(), "%d°", seekBarServoGarra.getProgress()));

        updateStatusDevicePaired();

        bluetoothManagerControl.write(String.format("%s\n", "F2").getBytes());

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
    public void onResume() {
        super.onResume();
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
    public void postDataReceived(@NonNull String dataReceived) {
        //ags:100;25;74;98
        if (dataReceived.contains("ags")) {

            String[] aux = dataReceived.split(":");
            String[] arrayAng = aux[1].split("-");
            String angBase = arrayAng[0];
            String andAltura = arrayAng[1];
            String angAlcance = arrayAng[2];
            String angGarra = arrayAng[3];

            int valorAnguloBase = Integer.parseInt(angBase.substring(2));
            int valorAnguloAltura = Integer.parseInt(andAltura.substring(2));
            int valorAnguloAlcance = Integer.parseInt(angAlcance.substring(2));
            int valorAnguloGarra = Integer.parseInt(angGarra.substring(2));

            seekBarServoBase.setProgress(valorAnguloBase);
            seekBarServoAltura.setProgress(valorAnguloAltura);
            seekBarServoAlcance.setProgress(valorAnguloAlcance);
            seekBarServoGarra.setProgress(valorAnguloGarra);
        }

    }

    private class SeekBarChange implements SeekBar.OnSeekBarChangeListener {
        private final TextView text;

        public SeekBarChange(TextView text) {
            this.text = text;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.text.setText(String.format(Locale.getDefault(), "%dº", progress));
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
            if (seekBar.getId() == R.id.seekBarServoBase) {
                String command = String.format(Locale.getDefault(), "bs:%d\n", seekBar.getProgress());
                bluetoothManagerControl.write(command.getBytes());
                return;
            }
            if (seekBar.getId() == R.id.seekBarServoAltura) {
                String command = String.format(Locale.getDefault(), "at:%d\n", seekBar.getProgress());
                bluetoothManagerControl.write(command.getBytes());
                return;
            }
            if (seekBar.getId() == R.id.seekBarServoAlcance) {
                String command = String.format(Locale.getDefault(), "ac:%d\n", seekBar.getProgress());
                bluetoothManagerControl.write(command.getBytes());
                return;
            }
            if (seekBar.getId() == R.id.seekBarSpeed) {
                String command = String.format(Locale.getDefault(), "gr:%d\n", seekBar.getProgress());
                bluetoothManagerControl.write(command.getBytes());
            }

        }
    }
}