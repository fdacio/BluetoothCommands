package br.com.daciosoftware.bluetoothcommands.alertdialog;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import br.com.daciosoftware.bluetoothcommands.R;

public class AlertDialogAppPlus {
    private final AlertDialog dialog;

    public AlertDialogAppPlus(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.alert_dialog_app_plus,null);
        TextView textMessageAppPlus = convertView.findViewById(R.id.textViewMessageAppPlus);
        textMessageAppPlus.setMovementMethod(LinkMovementMethod.getInstance());
        textMessageAppPlus.setText(Html.fromHtml(context.getResources().getString(R.string.text_app_plus)));
        builder.setView(convertView);
        builder.setCancelable(true);
        builder.setNeutralButton(R.string.dialog_neutral_button, (d, w) -> d.dismiss());
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }
}
