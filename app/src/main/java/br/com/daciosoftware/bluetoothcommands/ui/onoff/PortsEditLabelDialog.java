package br.com.daciosoftware.bluetoothcommands.ui.onoff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.database.AppDatabase;
import br.com.daciosoftware.bluetoothcommands.database.BluetoothCommandDatabase;
import br.com.daciosoftware.bluetoothcommands.database.dao.PortDao;
import br.com.daciosoftware.bluetoothcommands.database.entity.PortEntity;

public class PortsEditLabelDialog {
    private final AlertDialog dialog;
    private EditText editTextLabelPort1;
    private EditText editTextLabelPort2;
    private EditText editTextLabelPort3;
    private EditText editTextLabelPort4;
    private final PortDao portDao;

    public PortsEditLabelDialog(Context context, OnOffFragment fragment) {

        AppDatabase db = BluetoothCommandDatabase.getInstance(context);
        portDao = db.portDao();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.edit_label);

        builder.setCancelable(false);
        builder.setNegativeButton(R.string.dialog_negative_button, (d, w) -> d.dismiss());
        builder.setPositiveButton(R.string.dialog_save_button, (d, w) -> {

            List<PortEntity> listPorts = portDao.getAll();
            if (listPorts.size() > 0) {
                if (listPorts.get(0) != null) {
                    PortEntity port1 = listPorts.get(0);
                    port1.label =  editTextLabelPort1.getText().toString();
                    portDao.update(port1);
                }
                if (listPorts.get(1) != null) {
                    PortEntity port2 = listPorts.get(1);
                    port2.label =  editTextLabelPort2.getText().toString();
                    portDao.update(port2);
                }
                if (listPorts.get(2) != null) {
                    PortEntity port3 = listPorts.get(2);
                    port3.label =  editTextLabelPort3.getText().toString();
                    portDao.update(port3);
                }
                if (listPorts.get(3) != null) {
                    PortEntity port4 = listPorts.get(3);
                    port4.label =  editTextLabelPort4.getText().toString();
                    portDao.update(port4);
                }
            } else {
                PortEntity port1 = new PortEntity();
                port1.pin = 0;
                port1.signal = true;
                port1.label =  editTextLabelPort1.getText().toString();

                PortEntity port2 = new PortEntity();
                port2.pin = 0;
                port2.signal = true;
                port2.label =  editTextLabelPort2.getText().toString();

                PortEntity port3 = new PortEntity();
                port3.pin = 0;
                port3.signal = true;
                port3.label =  editTextLabelPort3.getText().toString();

                PortEntity port4 = new PortEntity();
                port4.pin = 0;
                port4.signal = true;
                port4.label =  editTextLabelPort4.getText().toString();

                portDao.insertAll(port1, port2, port3, port4);

            }
            fragment.onStart();
            d.dismiss();
        });

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.edit_label_port_dialog,null);

        editTextLabelPort1 = convertView.findViewById(R.id.editTextLabelPort1);
        editTextLabelPort2 = convertView.findViewById(R.id.editTextLabelPort2);
        editTextLabelPort3 = convertView.findViewById(R.id.editTextLabelPort3);
        editTextLabelPort4 = convertView.findViewById(R.id.editTextLabelPort4);

        List<PortEntity> listPorts = portDao.getAll();

        if (listPorts.size() > 0) {
            if (listPorts.get(0) != null) {
                PortEntity port1 = listPorts.get(0);
                editTextLabelPort1.setText(port1.label);
            }
            if (listPorts.get(1) != null) {
                PortEntity port2 = listPorts.get(1);
                editTextLabelPort2.setText(port2.label);
            }
            if (listPorts.get(2) != null) {
                PortEntity port3 = listPorts.get(2);
                editTextLabelPort3.setText(port3.label);
            }
            if (listPorts.get(3) != null) {
                PortEntity port4 = listPorts.get(3);
                editTextLabelPort4.setText(port4.label);
            }
        }

        builder.setView(convertView);
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

}
