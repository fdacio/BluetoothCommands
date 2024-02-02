package br.com.daciosoftware.bluetoothcommands.ui.commands;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import br.com.daciosoftware.bluetoothcommands.BluetoothConnection;
import br.com.daciosoftware.bluetoothcommands.BluetoothConnectionListener;
import br.com.daciosoftware.bluetoothcommands.BluetoothInstance;
import br.com.daciosoftware.bluetoothcommands.MainActivity;
import br.com.daciosoftware.bluetoothcommands.R;

public class CommandsFragment extends Fragment implements BluetoothConnectionListener {

    private Context mContext;
    private ListView listViewData;
    private EditText editTextCommand;
    private final List<String> listData = new ArrayList<>();
    private final BluetoothConnection bluetoothConnection = BluetoothInstance.getInstance();
    private Handler mHandler;
    private Handler handlerUpdateStatusDeviceParead;

   private Toolbar toolbar;

    private String lastCommand;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_commands, container, false);
        toolbar = root.findViewById(R.id.toolbarCommand);
        toolbar.inflateMenu(R.menu.menu_command);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_command_repeat) {
                editTextCommand.setText((lastCommand != null) ? lastCommand : "");
                return true;
            }
            return false;
        });
        updateStatusDeveiceParead();

        editTextCommand = root.findViewById(R.id.editTextCommand);
        listViewData = root.findViewById(R.id.listViewData);
        listViewData.setEmptyView(root.findViewById(R.id.textViewListEmpty));
        ImageButton buttonSend = root.findViewById(R.id.button_send);
        FloatingActionButton buttonClear = root.findViewById(R.id.fbClearAll);

        buttonSend.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) mContext;
            BluetoothDevice devicePaired = mainActivity.getDevicePaired();
            if (devicePaired == null) {
                Toast.makeText(mContext, "Não há dispositivo pareado", Toast.LENGTH_LONG).show();
                return;
            }

            String command = editTextCommand.getText().toString();
            lastCommand = command;
            if (command.isEmpty()) return;
            editTextCommand.setText("");

            bluetoothConnection.write(String.format("%s\n", command).getBytes());

            listData.add("Enviado: " + command);
            updateListData();

        });

        buttonClear.setOnClickListener( v -> {
            listData.clear();
            updateListData();
        });

        mHandler =  new Handler() {
            @Override
            public void handleMessage(@NonNull Message message){
                String dataReceiver = message.getData().getString("dados");
                listData.add("Recebido: " + dataReceiver);
                updateListData();
                super.handleMessage(message);
            }
        };


        handlerUpdateStatusDeviceParead = new Handler() {
            @Override
            public void handleMessage(@NonNull Message message) {
                super.handleMessage(message);
                String msg = message.getData().getString("message");
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                updateStatusDeveiceParead();
            }
        };

        if (bluetoothConnection != null)  bluetoothConnection.setListener(CommandsFragment.this);

        return root;
    }

    private void updateListData() {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, listData);
        listViewData.setAdapter(itemsAdapter);
    }

    private void updateStatusDeveiceParead() {
        MainActivity mainActivity = (MainActivity) mContext;
        BluetoothDevice devicePaired = mainActivity.getDevicePaired();
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
    }
    @Override
    public void setConnected(BluetoothDevice device) {}

    @Override
    public void setDisconnected() {
        MainActivity mainActivity = (MainActivity) mContext;
        mainActivity.setDevicePaired(null);
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("message", "Dispositivo despareado.");
        msg.setData(bundle);
        handlerUpdateStatusDeviceParead.sendMessage(msg);
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