package com.magnes.nushuxa.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * BluetoothStateReceiverBridge forwards ACTION_STATE_CHANGED events
 * to a listener interface that Pyjnius can implement.
 */
public class BluetoothStateReceiverBridge extends BroadcastReceiver {

    /** Listener interface implemented by PythonJavaClass. */
    public interface BluetoothStateListener {
        void onBluetoothStateChanged(int state);
    }

    private BluetoothStateListener listener;

    public void setListener(BluetoothStateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener == null || intent == null) {
            return;
        }

        String action = intent.getAction();
        if (!BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            return;
        }

        int state = intent.getIntExtra(
                BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR);
        listener.onBluetoothStateChanged(state);
    }
}
