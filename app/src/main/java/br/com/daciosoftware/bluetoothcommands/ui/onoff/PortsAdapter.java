package br.com.daciosoftware.bluetoothcommands.ui.onoff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

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
    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.ports_adapter, null);
        TextView textViewPorts = convertView.findViewById(R.id.textViewPortLabel);
        Integer port = listPorts.get(position);
        textViewPorts.setText(String.format(Locale.getDefault(), "%d", port));
        return convertView;
    }
}
