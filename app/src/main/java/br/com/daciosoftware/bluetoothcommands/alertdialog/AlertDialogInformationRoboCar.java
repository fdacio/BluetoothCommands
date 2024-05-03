package br.com.daciosoftware.bluetoothcommands.alertdialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import br.com.daciosoftware.bluetoothcommands.R;

public class AlertDialogInformationRoboCar {
    private final AlertDialog dialog;

    public AlertDialogInformationRoboCar(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.alert_dialog_information_roboarm,null);
        builder.setView(convertView);
        builder.setCancelable(true);
        builder.setNeutralButton(R.string.dialog_neutral_button, (d, w) -> d.dismiss());
        dialog = builder.create();

    }
    public void show() {
        dialog.show();
    }

}
