package com.example.pick_pack.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {

    private BluetoothSocket socket;
    private OutputStream outputStream;

    private static final UUID SPP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public boolean connectToDevice(String deviceName) {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> devices = adapter.getBondedDevices();

            for (BluetoothDevice device : devices) {
                if (device.getName().equals(deviceName)) {
                    socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                    socket.connect();
                    outputStream = socket.getOutputStream();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendCommand(String cmd) {
        try {
            if (outputStream != null) {
                outputStream.write((cmd + "\n").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
