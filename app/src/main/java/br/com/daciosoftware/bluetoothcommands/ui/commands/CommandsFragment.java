package br.com.daciosoftware.bluetoothcommands.ui.commands;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
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

import br.com.daciosoftware.bluetoothcommands.MainActivity;
import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;
import br.com.daciosoftware.bluetoothcommands.database.AppDatabase;
import br.com.daciosoftware.bluetoothcommands.database.dao.CommandDao;
import br.com.daciosoftware.bluetoothcommands.database.entity.Command;

public class CommandsFragment extends Fragment implements BluetoothManagerControl.ConnectionDevice {
    private Context appContext;
    private ListView listViewCommands;
    private EditText editTextCommand;
    private Toolbar toolbar;
    private final List<Comando> commands = new ArrayList<>();
    private final List<Comando> commandsSender = new ArrayList<>();
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
                editTextCommand.setText((commandsSender.size() > 0) ? commandsSender.get(indexCommand).getTexto() : "");
                indexCommand--;
                if (indexCommand < 0) indexCommand = commandsSender.size() - 1;
                return true;
            }
            return false;
        });

        editTextCommand = root.findViewById(R.id.editTextCommand);
        listViewCommands = root.findViewById(R.id.listViewData);
        listViewCommands.setEmptyView(root.findViewById(R.id.textViewListEmpty));
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
            commands.add(comando);
            commandsSender.add(comando);
            indexCommand = commandsSender.size() - 1;
            updateListData();
        });

        buttonClear.setOnClickListener(v -> {
            commands.clear();
            updateListData();
        });

        updateStatusDeveiceParead();

        return root;
    }

    private void updateListData() {
        ComandoAdapter adapter = new ComandoAdapter(appContext, commands);
        listViewCommands.setAdapter(adapter);
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
    public void postDeviceConnection() {
    }

    @Override
    public void postDeviceDisconnection() {
        Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
        updateStatusDeveiceParead();
    }

    @Override
    public void postFailConnection() {
    }

    @Override
    public void postDataReceived(String dataReceived) {
        commands.add(new Comando(dataReceived, Comando.TypeCommand.RECEBIDO));
        updateListData();
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity mainActivity = (MainActivity) appContext;
        AppDatabase db =  mainActivity.getDatabase();
        for(Comando comando: commandsSender) {
            Command command = new Command();
            command.command = comando.getTexto();
            db.commandDao().insertAll(command);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) appContext;
        AppDatabase db =  mainActivity.getDatabase();
        CommandDao commandDao = db.commandDao();
        for(Command command: commandDao.getAll()) {
            Comando comando = new Comando(command.command, Comando.TypeCommand.ENVIADO);
            commandsSender.add(comando);
        }
    }

}