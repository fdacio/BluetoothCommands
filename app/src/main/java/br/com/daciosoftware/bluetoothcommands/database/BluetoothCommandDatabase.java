package br.com.daciosoftware.bluetoothcommands.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

public class BluetoothCommandDatabase {
    private static AppDatabase instance;
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            Log.i("BLU-DATABASE", "criou uma instancia do DATABASE");
            instance = Room.databaseBuilder(context, AppDatabase.class, "bluetooth-commands-database").allowMainThreadQueries().build();
        }
        return instance;
    }

}
