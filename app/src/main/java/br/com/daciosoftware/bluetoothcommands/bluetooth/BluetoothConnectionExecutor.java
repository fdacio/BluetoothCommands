package br.com.daciosoftware.bluetoothcommands.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BluetoothConnectionExecutor {

    private BluetoothManagerControl mmBluetoothManagerControl;
    private boolean connected = false;
    private BluetoothSocket mmSocket;
    private OutputStream mmOutStream;
    private InputStream mmInputStream;
    private BluetoothConnectionExecutor instance;

    public BluetoothConnectionExecutor(BluetoothManagerControl bluetoothManagerControl) {
        mmBluetoothManagerControl = bluetoothManagerControl;
    }

    @SuppressLint("MissingPermission")
    public void executeConnection(BluetoothDevice device) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handlerConnection = new Handler(Looper.getMainLooper());
        try {
            //executor.awaitTermination(30, TimeUnit.SECONDS);
            executor.execute(() -> {
                BluetoothSocket tmp = null;
                String _uuid = "00001101-0000-1000-8000-00805F9B34FB";
                try {
                    tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(_uuid));
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
                handlerConnection.post(() -> {
                    if (connected) {
                        mmBluetoothManagerControl.setDevicePaired(device);
                        mmBluetoothManagerControl.getListenerConnectonDevice().postDeviceConnect();
                        //new BluetoothConnectionWriteServer().executeWriteServer();
                        new Thread(new BluetoothConnectionListenerServer()).start();
                    }
                });
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    public void executeDisconnect() {
        ExecutorService executorDisconnect = Executors.newSingleThreadExecutor();
        Handler handlerDisconnect = new Handler(Looper.getMainLooper());
        try {
            executorDisconnect.execute(() -> {
                closeSocketAndStream();
                handlerDisconnect.post(() -> {
                    mmBluetoothManagerControl.setDevicePaired(null);
                    mmBluetoothManagerControl.getListenerConnectonDevice().postDeviceDisconnect();
                });
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executorDisconnect.shutdown();
        }
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

    private void closeSocketAndStream() {
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
        }
    }

    private class BluetoothConnectionWriteServer implements Callable<String> {
        ExecutorService executorWriteServer = Executors.newSingleThreadExecutor();
        Handler handlerWriteServer = new Handler(Looper.getMainLooper());

        StringBuilder leitura;

        public void executeWriteServer() {

            executorWriteServer.execute(() -> {
                while (connected) {
                    if (mmInputStream != null) {
                        try {
                            byte[] buffer = new byte[1024];
                            int byteLidos = mmInputStream.read(buffer);
                            if (byteLidos > 0) {
                                leitura = new StringBuilder();
                                for (int i = 0; i < 1024; i++) {
                                    if ((buffer[i] != '\n') && (buffer[i] != '\r')) {
                                        leitura.append((char) buffer[i]);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            break;
                        }
                    }
                    if (!connected) {
                        executeDisconnect();
                    }
                }

            });
        }

        @Override
        public String call() throws Exception {
            return null;
        }
    }

    private class BluetoothConnectionListenerServer implements Runnable {
        @Override
        public void run() {
            while (connected) {
                try {
                    if (mmInputStream != null) {
                        byte[] buffer = new byte[1024];
                        int byteLidos = mmInputStream.read(buffer);
                        if (byteLidos > 0) {
                            StringBuilder leitura = new StringBuilder();
                            for (int i = 0; i < 1024; i++) {
                                if ((buffer[i] != '\n') && (buffer[i] != '\r')) {
                                    leitura.append((char) buffer[i]);
                                } else {
                                    if (leitura.length() > 0) {
                                        mmBluetoothManagerControl.getListenerConnectonDevice().postDataReceive(leitura.toString());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
            executeDisconnect();
        }
    }

}
