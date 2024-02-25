package br.com.daciosoftware.bluetoothcommands.ui.onoff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.R;

public class PortsAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final List<Integer> listPorts;

    public PortsAdapter(Context context, List<Integer> listPorts) {
        this.mInflater = LayoutInflater.from(context);
        this.listPorts = listPorts;
    }

    @Override
    public int getCount() {
        return listPorts.size();
    }

    @Override
    public Integer getItem(int position) {
        return listPorts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPosition(Integer pin) {
        return listPorts.indexOf(pin);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.ports_adapter, null);
        TextView textViewPorts = convertView.findViewById(R.id.textViewPortLabel);
        Integer port = listPorts.get(position);
        textViewPorts.setText(port.toString());
        return convertView;
    }
}
