package br.com.daciosoftware.bluetoothcommands.alertdialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import br.com.daciosoftware.bluetoothcommands.R;

public class AlertDialogInformation {
    private final AlertDialog.Builder builder;
    private final AlertDialog dialog;

    public AlertDialogInformation(Context context) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.alert_dialog_information,null);
        builder.setView(convertView);
        builder.setCancelable(true);
        builder.setNeutralButton(R.string.dialog_neutral_button, (d, w) -> {
            d.dismiss();
        });
        dialog = builder.create();

    }
    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
