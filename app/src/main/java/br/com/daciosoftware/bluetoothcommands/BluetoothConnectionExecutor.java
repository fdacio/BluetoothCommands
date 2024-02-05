package br.com.daciosoftware.bluetoothcommands;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.daciosoftware.bluetoothcommands.alertdialog.AlertDialogProgress;

public class BluetoothConnectionExecutor {
    private final BluetoothDevice mmDevice;
    private BluetoothConnectionListener mmListener;
    private final Context mmContext;
    private boolean connected = false;
    private BluetoothSocket mmSocket;
    private OutputStream mmOutStream;
    private InputStream mmInputStream;
    private static BluetoothConnectionExecutor instance;
    private BluetoothConnectionExecutor(BluetoothDevice device, BluetoothConnectionListener listener, Context context){
        mmDevice = device;
        mmListener = listener;
        mmContext = context;
    }

    public static BluetoothConnectionExecutor builder (BluetoothDevice device, BluetoothConnectionListener listener, Context context) {
        instance = new BluetoothConnectionExecutor(device, listener, context);
        return instance;
    }

    public static BluetoothConnectionExecutor getInstance() {
        return instance;
    }

    public void setListener(BluetoothConnectionListener listener) {
        mmListener = listener;
    }

    @SuppressLint("MissingPermission")
    public void execute () {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        AlertDialogProgress dialog = new AlertDialogProgress(mmContext, AlertDialogProgress.TypeDialog.PAIR_DEVICE);
        dialog.show();

        executor.execute(() -> {
            BluetoothSocket tmp = null;
            String _uuid = "00001101-0000-1000-8000-00805F9B34FB";
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString(_uuid));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
            OutputStream tmpOut = null;
            InputStream tmpIn = null;
            try {
                tmpOut = mmSocket.getOutputStream();
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmOutStream = tmpOut;
            mmInputStream = tmpIn;

            try {
                mmSocket.connect();
                connected = true;
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }

            //Aqui seta a conexao
            handler.post(() -> {
                dialog.dismiss();
                if (connected) {
                    mmListener.setConnected(mmDevice);
                    new Thread(new BluetoothConnectionListenerServer()).start();
                } else {
                    Toast.makeText(mmContext, "Não foi possível conectar", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    public void write(byte[] buffer) {
        try {
            if (mmOutStream != null) {
                mmOutStream.write(buffer);
            }
        } catch (IOException e) {
            connected = false;
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (mmOutStream != null) {
                mmOutStream.close();
                mmOutStream = null;
            }
            if (mmSocket != null) {
                mmSocket.close();
                mmSocket = null;
            }
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class BluetoothConnectionListenerServer implements Runnable {
        @Override
        public void run() {
            while (connected) {
                if (mmInputStream != null){
                    try {
                        byte[] buffer = new byte[1024];
                        int byteLidos = mmInputStream.read(buffer);
                        if (byteLidos > 0) {
                            StringBuilder leitura = new StringBuilder();
                            for (int i = 0; i < 1024; i++) {
                                if ((buffer[i] != '\n') && (buffer[i] != '\r')) {
                                    leitura.append((char) buffer[i]);
                                } else {
                                    if (leitura.length() > 0) {
                                        mmListener.readFromDevicePaired(leitura.toString());
                                    }
                                    break;
                                }
                            }
                        }

                    } catch (IOException e) {
                        break;
                    }
                }
            }
            mmListener.setDisconnected();
        }
    }

}
