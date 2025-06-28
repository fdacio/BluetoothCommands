package br.com.daciosoftware.bluetoothcommands.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.database.entity.PortEntity;

@Dao
public interface PortDao {
    @Query("SELECT * FROM port ORDER BY id")
    List<PortEntity> getAll();
    @Insert
    void insertAll(PortEntity...ports);
    @Update
    void update(PortEntity port);
}
