package br.com.daciosoftware.bluetoothcommands.ui.onoff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import br.com.daciosoftware.bluetoothcommands.R;

public class PortsEditLabelDialog {
    private final AlertDialog.Builder builder;
    private final AlertDialog dialog;
    private EditText editTextLabelPort1;
    private EditText editTextLabelPort2;
    private EditText editTextLabelPort3;
    private EditText editTextLabelPort4;

    public PortsEditLabelDialog(Context context) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_label);
        builder.setNegativeButton(R.string.dialog_negative_button, (d, w) -> {
            d.dismiss();
        });
        builder.setPositiveButton(R.string.dialog_save_button, (d, w) -> {
            d.dismiss();
        });

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.edit_label_port_dialog,null);

        editTextLabelPort1 = convertView.findViewById(R.id.editTextLabelPort1);
        editTextLabelPort2 = convertView.findViewById(R.id.editTextLabelPort2);
        editTextLabelPort3 = convertView.findViewById(R.id.editTextLabelPort3);
        editTextLabelPort4 = convertView.findViewById(R.id.editTextLabelPort4);
        builder.setView(convertView);
        builder.setCancelable(true);
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
