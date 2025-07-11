package br.com.daciosoftware.bluetoothcommands.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "port")
public class PortEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name="pin")
    public int pin;
    @ColumnInfo(name="signal")
    public boolean signal;
    @ColumnInfo(name="label")
    public String label;

}
