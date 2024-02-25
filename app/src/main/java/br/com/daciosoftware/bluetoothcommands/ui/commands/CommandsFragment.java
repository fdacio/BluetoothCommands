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

import br.com.daciosoftware.bluetoothcommands.R;
import br.com.daciosoftware.bluetoothcommands.bluetooth.BluetoothManagerControl;
import br.com.daciosoftware.bluetoothcommands.database.AppDatabase;
import br.com.daciosoftware.bluetoothcommands.database.BluetoothCommandDatabase;
import br.com.daciosoftware.bluetoothcommands.database.dao.CommandDao;
import br.com.daciosoftware.bluetoothcommands.database.entity.CommandEntity;

public class CommandsFragment extends Fragment implements BluetoothManagerControl.ConnectionDevice {
    private Context appContext;
    private ListView listViewCommands;
    private EditText editTextCommand;
    private Toolbar toolbar;
    private final List<Command> listCommands = new ArrayList<>();
    private final List<Command> commandsSender = new ArrayList<>();
    private int indexCommand = 0;
    private BluetoothManagerControl bluetoothManagerControl;
    private CommandDao commandDao;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        appContext = context;
        bluetoothManagerControl = BluetoothManagerControl.getInstance(context);
        bluetoothManagerControl.setListenerConnectionDevice(CommandsFragment.this);
        AppDatabase db =  BluetoothCommandDatabase.getInstance(appContext);
        commandDao = db.commandDao();
    }

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_commands, container, false);
        toolbar = root.findViewById(R.id.toolbarCommand);
        toolbar.inflateMenu(R.menu.menu_command);
        editTextCommand = root.findViewById(R.id.editTextCommand);
        listViewCommands = root.findViewById(R.id.listViewData);
        listViewCommands.setEmptyView(root.findViewById(R.id.textViewListEmpty));
        ImageButton buttonSend = root.findViewById(R.id.button_send);
        FloatingActionButton buttonClear = root.findViewById(R.id.fbClearAll);

        //Menu repetir comandos
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_command_repeat) {
                editTextCommand.setText((commandsSender.size() > 0) ? commandsSender.get(indexCommand).getTexto() : "");
                indexCommand--;
                if (indexCommand < 0) indexCommand = commandsSender.size() - 1;
                return true;
            }
            return false;
        });

        //Botão enviar comando
        buttonSend.setOnClickListener(v -> {
            String commandInput = editTextCommand.getText().toString();
            editTextCommand.setText("");
            BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
            if (commandInput.isEmpty()) return;
            if (devicePaired == null) {
                Toast.makeText(appContext, R.string.message_dont_device_pair, Toast.LENGTH_LONG).show();
                return;
            }
            bluetoothManagerControl.write(String.format("%s\n", commandInput).getBytes());
            Command command = new Command(commandInput, Command.TypeCommand.ENVIADO);
            listCommands.add(command);

            boolean addNewCommand = !commandsSender.contains(command);
            if (addNewCommand) {
                commandsSender.add(command);
                indexCommand = commandsSender.size() - 1;
            }
            updateListData();
            updateCommandsToDatabase(command);
        });

        //Botão deletar comandos
        buttonClear.setOnClickListener(v -> {
            listCommands.clear();
            updateListData();
        });

        updateStatusDevicePaired();
        updateCommandsFromDatabase();

        return root;
    }

    @SuppressLint({"MissingPermission"})
    private void updateStatusDevicePaired() {
        BluetoothDevice devicePaired = bluetoothManagerControl.getDevicePaired();
        toolbar.setSubtitle((devicePaired != null) ? devicePaired.getName() : null);
    }

    private void updateListData() {
        CommandAdapter adapter = new CommandAdapter(appContext, listCommands);
        listViewCommands.setAdapter(adapter);
    }

    private void updateCommandsFromDatabase() {
        List<CommandEntity> commandsList = commandDao.getAll();
        for(CommandEntity commandEntity: commandsList) {
            Command command = new Command(commandEntity.command, Command.TypeCommand.ENVIADO);
            commandsSender.add(command);
        }
    }

    private void updateCommandsToDatabase(Command c) {
        CommandEntity commandEntity = new CommandEntity();
        commandEntity.command = c.getTexto();
        List<CommandEntity> listCommands = commandDao.getAll();
        boolean addNewCommand = !listCommands.contains(commandEntity);
        if (addNewCommand) {
            if (listCommands.size() == 10) {
                commandDao.delete(listCommands.get(0));
            }
            commandDao.insert(commandEntity);
        }
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
        updateStatusDevicePaired();
    }

    @Override
    public void postFailConnection() {
    }

    @Override
    public void postDataReceived(String dataReceived) {
        listCommands.add(new Command(dataReceived, Command.TypeCommand.RECEBIDO));
        updateListData();
    }

}