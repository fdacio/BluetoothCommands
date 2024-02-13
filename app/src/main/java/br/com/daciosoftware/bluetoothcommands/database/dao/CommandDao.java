package br.com.daciosoftware.bluetoothcommands.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.database.entity.Command;

@Dao
public interface CommandDao {
    @Query("SELECT * FROM command")
    List<Command> getAll();

    @Insert
    void insertAll(Command ...commands);
}
