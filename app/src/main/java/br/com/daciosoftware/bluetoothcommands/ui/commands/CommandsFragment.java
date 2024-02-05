package br.com.daciosoftware.bluetoothcommands.ui.commands;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import br.com.daciosoftware.bluetoothcommands.BluetoothConnectionExecutor;
import br.com.daciosoftware.bluetoothcommands.BluetoothConnectionListener;
import br.com.daciosoftware.bluetoothcommands.MainActivity;
import br.com.daciosoftware.bluetoothcommands.R;

public class CommandsFragment extends Fragment implements BluetoothConnectionListener {
    private Context appContext;
    private ListView listViewComandos;
    private EditText editTextCommand;
    private Toolbar toolbar;
    private final List<Comando> comandos = new ArrayList<>();
    private final List<Comando> comandosEnviados = new ArrayList<>();
    private int indexCommand = 0;
    private BluetoothConnectionExecutor bluetoothConnection;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
    }
    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_commands, container, false);
        toolbar = root.findViewById(R.id.toolbarCommand);
        toolbar.inflateMenu(R.menu.menu_command);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_command_repeat) {
                //List<Comando> comandosEnviados = comandos.stream().filter(c -> c.getTipo().equals(Comando.TypeCommand.ENVIADO)).collect(Collectors.toList());
                editTextCommand.setText((comandosEnviados.size() > 0) ? comandosEnviados.get(indexCommand).getTexto() : "");
                indexCommand--;
                if(indexCommand < 0) indexCommand = comandosEnviados.size() - 1;
                return true;
            }
            return false;
        });
        updateStatusDeveiceParead();

        editTextCommand = root.findViewById(R.id.editTextCommand);
        listViewComandos = root.findViewById(R.id.listViewData);
        listViewComandos.setEmptyView(root.findViewById(R.id.textViewListEmpty));
        ImageButton buttonSend = root.findViewById(R.id.button_send);
        FloatingActionButton buttonClear = root.findViewById(R.id.fbClearAll);

        buttonSend.setOnClickListener(v -> {
            String command = editTextCommand.getText().toString();
            editTextCommand.setText("");
            MainActivity mainActivity = (MainActivity) appContext;
            BluetoothDevice devicePaired = mainActivity.getDevicePaired();
            if (command.isEmpty()) return;
            if (devicePaired == null) {
                Toast.makeText(appContext, "Não há dispositivo pareado", Toast.LENGTH_LONG).show();
                return;
            }
            Comando comando = new Comando(command, Comando.TypeCommand.ENVIADO);
            bluetoothConnection.write(String.format("%s\n", command).getBytes());
            comandos.add(comando);
            comandosEnviados.add(comando);
            indexCommand = comandosEnviados.size() - 1;
            updateListData();

        });

        buttonClear.setOnClickListener(v -> {
            comandos.clear();
            updateListData();
        });

        /*
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message message) {
                String texto = message.getData().getString("dados");
                comandos.add(new Comando(texto, Comando.TypeCommand.RECEBIDO));
                updateListData();
                super.handleMessage(message);
            }
        };
         */

        /*
        handlerUpdateStatusDeviceParead = new Handler() {
            @Override
            public void handleMessage(@NonNull Message message) {
                super.handleMessage(message);
                String msg = message.getData().getString("message");
                Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
                updateStatusDeveiceParead();
            }
        };
        */

        bluetoothConnection = BluetoothConnectionExecutor.getInstance();
        if (bluetoothConnection != null) bluetoothConnection.setListener(CommandsFragment.this);

        return root;
    }

    private void updateListData() {
        ComandoAdapter adapter = new ComandoAdapter(appContext, comandos);
        listViewComandos.setAdapter(adapter);
    }

    @SuppressLint({"MissingPermission"})
    private void updateStatusDeveiceParead() {
        MainActivity mainActivity = (MainActivity) appContext;
        BluetoothDevice devicePaired = mainActivity.getDevicePaired();
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
    }

    @Override
    public void setConnected(BluetoothDevice device) {
    }

    @Override
    public void setDisconnected() {
        MainActivity mainActivity = (MainActivity) appContext;
        mainActivity.setDevicePaired(null);
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
            updateStatusDeveiceParead();
        });
    }

    @Override
    public void readFromDevicePaired(String dataReceiver) {
         new Handler(Looper.getMainLooper()).post(() -> {
            comandos.add(new Comando(dataReceiver, Comando.TypeCommand.RECEBIDO));
            updateListData();
        });
    }

}