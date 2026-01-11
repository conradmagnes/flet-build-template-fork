package com.flet.flet_app.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class BleScanBridge {
    private static final ConcurrentHashMap<String, ScanRecord> CACHE =
            new ConcurrentHashMap<>();
    private static BluetoothLeScanner scanner;
    private static ScanCallback callback;

    private BleScanBridge() {
        // Utility class
    }

    public static void startScan() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            return;
        }

        scanner = adapter.getBluetoothLeScanner();
        if (scanner == null) {
            return;
        }

        if (callback == null) {
            callback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    BluetoothDevice device = result.getDevice();
                    if (device == null) {
                        return;
                    }
                    String address = device.getAddress();
                    String name = device.getName();
                    int rssi = result.getRssi();
                    CACHE.put(address, new ScanRecord(address, name, rssi));
                }
            };
        }

        scanner.startScan(callback);
    }

    public static void stopScan() {
        if (scanner != null && callback != null) {
            scanner.stopScan(callback);
        }
    }

    public static void clearResults() {
        CACHE.clear();
    }

    public static ArrayList<String> getResults() {
        ArrayList<String> out = new ArrayList<>();
        for (ScanRecord rec : CACHE.values()) {
            out.add(rec.address + "|" + rec.name + "|" + rec.rssi);
        }
        return out;
    }

    public static final class ScanRecord {
        final String address;
        final String name;
        final int rssi;

        ScanRecord(String address, String name, int rssi) {
            this.address = address;
            this.name = name;
            this.rssi = rssi;
        }
    }
}
