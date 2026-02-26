package com.magnes.nushuxa.ble;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

public class BleScanCallbackBridge extends ScanCallback {
    public interface BLEListener {
        void onDeviceFound(ScanResult result);

        void onScanFailed(int errorCode);
    }

    private BLEListener listener;

    public void setListener(BLEListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        if (listener != null) {
            listener.onDeviceFound(result);
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        if (listener != null) {
            listener.onScanFailed(errorCode);
        }
    }
}
