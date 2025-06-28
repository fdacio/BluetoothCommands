package br.com.daciosoftware.bluetoothcommands.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.database.entity.CommandEntity;

@Dao
public interface CommandDao {

    @Query("SELECT * FROM command ORDER BY id ASC")
    List<CommandEntity> getAll();

    @Query("DELETE FROM command WHERE id > 0")
    void deleteAll();

    @Insert
    void insert(CommandEntity command);

}
