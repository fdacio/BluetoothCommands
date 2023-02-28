package com.example.bluetoothcommands.ui.commands;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bluetoothcommands.BluetoothConnection;
import com.example.bluetoothcommands.BluetoothInstance;
import com.example.bluetoothcommands.R;

public class CommandsFragment extends Fragment {

    private View root;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_commands, container, false);

        Button buttonSend = root.findViewById(R.id.button_send);
        EditText editTextCommand = root.findViewById(R.id.editTextCommand);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BluetoothInstance.isConnected()) {
                    Toast.makeText(mContext, "Não há dispositivo conectado", Toast.LENGTH_LONG).show();
                    return;
                }
                BluetoothConnection bluetoothConnection = BluetoothInstance.getInstance();
                String command = editTextCommand.getText().toString();
                bluetoothConnection.write(command.getBytes());
                editTextCommand.setText("");
            }
        });

        return root;

    }
}