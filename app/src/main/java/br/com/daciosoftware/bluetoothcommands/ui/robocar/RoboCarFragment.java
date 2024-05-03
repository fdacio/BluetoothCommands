package br.com.daciosoftware.bluetoothcommands.ui.robocar;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogInformationRoboArm;
import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogInformationRoboCar;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;
import br.com.daciosoftware.bluetoothcommands.ui.roboarm.RoboArmFragment;

public class RoboCarFragment extends Fragment implements BluetoothManagerControl.ConnectionDevice {
    private Context appContext;
    private Toolbar toolbar;
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

    }
}