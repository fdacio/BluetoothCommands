package br.com.daciosoftware.bluetoothcommands.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "command")
public class CommandEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name="command")
    public String command;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandEntity that = (CommandEntity) o;
        return command.equals(that.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command);
    }
}
