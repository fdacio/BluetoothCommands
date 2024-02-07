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

import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;

public class CommandsFragment extends Fragment implements BluetoothManagerControl.ConnectionDevice {
    private Context appContext;
    private ListView listViewComandos;
    private EditText editTextCommand;
    private Toolbar toolbar;
    private final List<Comando> comandos = new ArrayList<>();
    private final List<Comando> comandosEnviados = new ArrayList<>();
    private int indexCommand = 0;

    private BluetoothManagerControl bluetoothManagerControl;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
        bluetoothManagerControl = BluetoothManagerControl.getInstance(context);
        bluetoothManagerControl.setListenerConnectionDevice(CommandsFragment.this);
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
                if (indexCommand < 0) indexCommand = comandosEnviados.size() - 1;
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
            BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
            if (command.isEmpty()) return;
            if (devicePaired == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                return;
            }
            Comando comando = new Comando(command, Comando.TypeCommand.ENVIADO);
            bluetoothManagerControl.write(String.format("%s\n", command).getBytes());
            comandos.add(comando);
            comandosEnviados.add(comando);
            indexCommand = comandosEnviados.size() - 1;
            updateListData();
        });

        buttonClear.setOnClickListener(v -> {
            comandos.clear();
            updateListData();
        });

        return root;
    }

    private void updateListData() {
        ComandoAdapter adapter = new ComandoAdapter(appContext, comandos);
        listViewComandos.setAdapter(adapter);
    }

    @SuppressLint({"MissingPermission"})
    private void updateStatusDeveiceParead() {
        BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
    }

    @Override
    public void initConnection() {

    }

    @Override
    public void postDeviceConnect() {
    }

    @Override
    public void postDeviceDisconnect() {
        Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
        updateStatusDeveiceParead();
    }

    @Override
    public void postDataReceive(String dataReceiver) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()-> {
            comandos.add(new Comando(dataReceiver, Comando.TypeCommand.RECEBIDO));
            updateListData();
        });
    }
}