package br.com.daciosoftware.bluetoothcommands.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.daciosoftware.bluetoothcommands.database.entity.Port;

@Dao
public interface PortDao {
    @Query("SELECT * FROM port")
    List<Port> getAll();
    @Insert
    void insertAll(Port ...ports);
    @Update
    public void updatePorts(Port... ports);

}
