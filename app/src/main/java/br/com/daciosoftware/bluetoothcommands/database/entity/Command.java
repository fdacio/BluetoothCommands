package br.com.daciosoftware.bluetoothcommands.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "command")
public class Command {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name="command")
    public String command;
}
