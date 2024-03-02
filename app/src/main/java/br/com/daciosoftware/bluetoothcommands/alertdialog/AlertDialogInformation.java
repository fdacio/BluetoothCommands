package br.com.daciosoftware.bluetoothcommands.alertdialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import br.com.daciosoftware.bluetoothcommands.R;

public class AlertDialogInformation {
    private final AlertDialog.Builder builder;
    private final AlertDialog dialog;

    public AlertDialogInformation(Context context, String message) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message);
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
