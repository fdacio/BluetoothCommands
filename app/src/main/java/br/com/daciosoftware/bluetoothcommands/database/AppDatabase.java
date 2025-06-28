package br.com.daciosoftware.bluetoothcommands.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import br.com.daciosoftware.bluetoothcommands.database.dao.CommandDao;
import br.com.daciosoftware.bluetoothcommands.database.dao.PortDao;
import br.com.daciosoftware.bluetoothcommands.database.entity.CommandEntity;
import br.com.daciosoftware.bluetoothcommands.database.entity.PortEntity;

@Database(entities = {CommandEntity.class, PortEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CommandDao commandDao();
    public abstract PortDao portDao();
}
