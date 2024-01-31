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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

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
    private List<String> listData = new ArrayList<>();
    private BluetoothConnection bluetoothConnection = BluetoothInstance.getInstance();
    private Handler mHandler;
    private View root;
    private Toolbar toolbar;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = inflater.inflate(R.layout.fragment_commands, container, false);
        toolbar = root.findViewById(R.id.toolbarCommand);
        toolbar.setTitle(R.string.title_commands);
        MainActivity mainActivity = (MainActivity) mContext;
        BluetoothDevice devicePaired = mainActivity.getDevicePaired();
        if (devicePaired != null) {
            toolbar.setSubtitle(devicePaired.getName());
        }
        editTextCommand = root.findViewById(R.id.editTextCommand);
        listViewData = root.findViewById(R.id.listViewData);
        listViewData.setEmptyView(root.findViewById(R.id.textViewListEmpty));
        ImageButton buttonSend = root.findViewById(R.id.button_send);
        FloatingActionButton buttonClear = root.findViewById(R.id.fbClearAll);

        buttonSend.setOnClickListener(v -> {
            if (!BluetoothInstance.isConnected()) {
                Toast.makeText(mContext, "Não há dispositivo conectado", Toast.LENGTH_LONG).show();
                return;
            }

            String command = editTextCommand.getText().toString();
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

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message message){
                String dataReceiver = message.getData().getString("dados");
                listData.add("Recebido: " + dataReceiver);
                updateListData();
                super.handleMessage(message);
            }
        };

        if (bluetoothConnection != null)  bluetoothConnection.setListener(CommandsFragment.this);

        return root;
    }

    private void updateListData() {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, listData);
        listViewData.setAdapter(itemsAdapter);
    }

    @Override
    public void setConnected(BluetoothDevice device) {}

    @Override
    public void setDisconnectedInView() {}

    @Override
    public void readFromDevicePaired(String dataReceiver) {
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("dados", dataReceiver);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
}