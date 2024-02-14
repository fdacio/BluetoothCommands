package br.com.daciosoftware.bluetoothcommands.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.database.entity.PortEntity;

@Dao
public interface PortDao {
    @Query("SELECT * FROM port")
    List<PortEntity> getAll();
    @Insert
    void insertAll(PortEntity...ports);
    @Query("DELETE FROM port WHERE id > 0")
    public void deleteAll();

}
