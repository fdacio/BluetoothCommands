package br.com.daciosoftware.bluetoothcommands.ui.onoff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import br.com.daciosoftware.bluetoothcommands.R;

public class PortsListDialog implements AdapterView.OnItemClickListener {

    private final AlertDialog dialog;

    private final EditText editText;
    private final PortsAdapter adapterPorts;

    public PortsListDialog(Context context, EditText editText, String label) {

        this.editText = editText;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(label);
        builder.setNegativeButton(R.string.dialog_negative_button, (d, w) ->d.dismiss());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.ports_dialog,null);
        ListView listView = convertView.findViewById(R.id.listViewPorts);

        int MAX_PORT = 40;
        List<Integer> ports = new ArrayList<>();
        for (int p = 0; p <= MAX_PORT; p++) {
            ports.add(p);
        }
        adapterPorts = new PortsAdapter(context, ports);

        listView.setAdapter(adapterPorts);
        listView.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        editText.setText(String.valueOf(adapterPorts.getPosition(position)));
        dismiss();
    }
}
