package com.example.bluetoothcommands.ui.commands;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bluetoothcommands.BluetoothConnection;
import com.example.bluetoothcommands.BluetoothConnectionListener;
import com.example.bluetoothcommands.BluetoothInstance;
import com.example.bluetoothcommands.R;

import java.util.ArrayList;
import java.util.List;

public class CommandsFragment extends Fragment implements BluetoothConnectionListener {

    private Context mContext;
    private ListView listViewData;
    private EditText editTextCommand;
    private final List<String> listData = new ArrayList<>();
    private BluetoothConnection bluetoothConnection;
    private Handler mHandler;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_commands, container, false);
        editTextCommand = root.findViewById(R.id.editTextCommand);
        listViewData = root.findViewById(R.id.listViewData);
        Button buttonSend = root.findViewById(R.id.button_send);

        buttonSend.setOnClickListener(v -> {
            if (!BluetoothInstance.isConnected()) {
                Toast.makeText(mContext, "Não há dispositivo conectado", Toast.LENGTH_LONG).show();
                return;
            }

            String command = editTextCommand.getText().toString();
            bluetoothConnection.write(command.getBytes());
            editTextCommand.setText("");

            listData.add("Enviado: " + command);

            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, listData);
            listViewData.setAdapter(itemsAdapter);

        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message){
                String dataReceiver = message.getData().getString("dados");
                listData.add("Recebido: " + dataReceiver);
                ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, listData);
                listViewData.setAdapter(itemsAdapter);
                super.handleMessage(message);
            }
        };

        bluetoothConnection = BluetoothInstance.getInstance();

        return root;

    }

    @Override
    public void onResume() {
        if (bluetoothConnection != null) {
            bluetoothConnection.setListener(this);
        }
        super.onResume();
    }

    @Override
    public void setConnected(BluetoothDevice device) {

    }

    @Override
    public void setDisconnected() {

    }

    @Override
    public void readFromDevicePaired(String dataReceiver) {
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("dados", dataReceiver);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
}