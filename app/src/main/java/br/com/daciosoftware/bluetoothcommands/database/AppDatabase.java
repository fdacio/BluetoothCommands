package br.com.daciosoftware.bluetoothcommands.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import br.com.daciosoftware.bluetoothcommands.database.dao.CommandDao;
import br.com.daciosoftware.bluetoothcommands.database.dao.PortDao;
import br.com.daciosoftware.bluetoothcommands.database.entity.Command;
import br.com.daciosoftware.bluetoothcommands.database.entity.Port;

@Database(entities = {Command.class, Port.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CommandDao commandDao();
    public abstract PortDao portDao();
}
