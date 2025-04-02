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
    private int lastIndexCommand = 0;
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
                String lastCommand = (!commandsSender.isEmpty()) ? commandsSender.get(lastIndexCommand).getTexto() : "";
                editTextCommand.setText(lastCommand);
                lastIndexCommand--;
                if (lastIndexCommand < 0) lastIndexCommand = commandsSender.size() - 1;
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
            } else {
                commandsSender.remove(command);
                commandsSender.add(command);
            }
            lastIndexCommand = commandsSender.size() - 1;
            updateListData();
        });

        //Botão deletar comandos
        buttonClear.setOnClickListener(v -> {
            listCommands.clear();
            updateListData();
        });

        updateStatusDevicePaired();
        getCommandsFromDatabase();

        bluetoothManagerControl.write(String.format("%s\n", "tab:2").getBytes());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        bluetoothManagerControl.setListenerConnectionDevice(CommandsFragment.this);
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

    private void getCommandsFromDatabase() {
        List<CommandEntity> commandsList = commandDao.getAll();
        for(CommandEntity commandEntity: commandsList) {
            Command command = new Command(commandEntity.command, Command.TypeCommand.ENVIADO);
            commandsSender.add(command);
        }
        lastIndexCommand = commandsSender.size() - 1;
    }

    private void updateCommandsToDatabase() {
        int firstIndex = - 1;
        int flagIndex = 10;
        int index = 1;
        for (int i = (commandsSender.size() -1); i >= 0; i--) {
            if (index == flagIndex) {
                firstIndex = commandsSender.indexOf(commandsSender.get(i));
                break;
            }
            index++;
        }
        if (firstIndex == -1) {
            firstIndex = 0;
        }
        commandDao.deleteAll();
        for (int i = firstIndex; i < commandsSender.size(); i++) {
            String command = commandsSender.get(i).getTexto();
            CommandEntity commandEntity = new CommandEntity();
            commandEntity.command = command;
            commandDao.insert(commandEntity);
        }
    }

    @Override
    public void initConnection(BluetoothDevice device) {

    }

    @Override
    public void postDeviceConnection(BluetoothDevice device) {
    }

    @Override
    public void postDeviceDisconnection() {
        Toast.makeText(appContext, R.string.message_despair_device, Toast.LENGTH_SHORT).show();
        updateStatusDevicePaired();
    }

    @Override
    public void postFailConnection(BluetoothDevice device) {
    }

    @Override
    public void postDataReceived(String dataReceived) {
        listCommands.add(new Command(dataReceived, Command.TypeCommand.RECEBIDO));
        updateListData();
    }

    @Override
    public void onStop() {
        super.onStop();
        updateCommandsToDatabase();
    }

}