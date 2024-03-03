package br.com.daciosoftware.bluetoothcommands.database;

import android.content.Context;

import androidx.room.Room;

public class BluetoothCommandDatabase {
    private static AppDatabase instance;
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "bluetooth-commands-database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return instance;
    }

}
