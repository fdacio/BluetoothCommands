package br.com.daciosoftware.bluetoothcommands.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.database.entity.Command;

@Dao
public interface CommandDao {

    @Query("SELECT * FROM command")
    List<Command> getAll();

    @Query("SELECT * FROM command ORDER BY id DESC")
    List<Command> getAllOrderDesc();

    @Insert
    void insert(Command command);

    @Query("DELETE FROM command WHERE id > 0")
    void deleteAll();
}
