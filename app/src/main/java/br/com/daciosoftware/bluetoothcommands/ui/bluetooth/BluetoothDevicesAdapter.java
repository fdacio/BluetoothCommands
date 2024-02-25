package br.com.daciosoftware.bluetoothcommands.ui.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import br.com.daciosoftware.bluetoothcommands.R;

public class BluetoothDevicesAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final List<BluetoothDevice> mData;

    public BluetoothDevicesAdapter(Context context, List<BluetoothDevice> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
    }

    public int getCount() {
        return (mData == null) ? 0 : mData.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.devices_adapter, null);
            holder = new ViewHolder();
            holder.deviceName =  convertView.findViewById(R.id.textViewDeviceName);
            holder.deviceAddress =  convertView.findViewById(R.id.textViewDeviceAddress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = mData.get(position);
        @SuppressLint("MissingPermission")
        String nameDevice = (device.getName() == null) ? "Dispositivo " + position : device.getName();
        holder.deviceName.setText(nameDevice);
        holder.deviceAddress.setText(device.getAddress());

        return convertView;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }


}
