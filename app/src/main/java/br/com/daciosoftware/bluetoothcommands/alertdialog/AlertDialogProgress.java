package br.com.daciosoftware.bluetoothcommands.alertdialog;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import br.com.daciosoftware.bluetoothcommands.R;

public class AlertDialogProgress  {
    public enum TypeDialog {
        SEARCH_DEVICE, PAIR_DEVICE
    }
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    @SuppressLint("MissingPermission")
    public AlertDialogProgress(Context context, TypeDialog typeDialog) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_bluetooth);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.alert_dialog_progress,null);
        TextView message = convertView.findViewById(R.id.textViewMessage);
        builder.setView(convertView);

        switch (typeDialog) {
            case SEARCH_DEVICE: {
                builder.setCancelable(true);
                message.setText(R.string.message_search_device);
                builder.setNegativeButton(R.string.dialog_negatica_button, (d, w) -> {
                    BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                    bluetoothAdapter.cancelDiscovery();
                    d.dismiss();
                });
                break;
            }
            case PAIR_DEVICE: {
                builder.setCancelable(false);
                message.setText(R.string.message_pair_device);
                break;
            }
        }
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }


}
