package ch.magnes.flet_app.wifi;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

/**
 * Bridge class that extends ConnectivityManager.NetworkCallback (abstract
 * class)
 * and forwards events to a Python-implementable interface.
 *
 * This pattern allows Pyjnius to implement the listener interface via
 * PythonJavaClass
 * while avoiding the limitation that Python cannot extend Java abstract
 * classes.
 */
public class WifiNetworkCallbackBridge extends ConnectivityManager.NetworkCallback {

    /**
     * Interface that Python can implement using PythonJavaClass.
     * All callback events from NetworkCallback are forwarded to this interface.
     */
    public interface WifiConnectionListener {
        /**
         * Called when network connection is successfully established.
         * 
         * @param networkId String representation of the Network object
         */
        void onAvailable(String networkId);

        /**
         * Called when connection request fails or user denies permission.
         */
        void onUnavailable();

        /**
         * Called when the network is about to be disconnected.
         * 
         * @param networkId   String representation of the Network object
         * @param maxMsToLive Maximum time in milliseconds before disconnection
         */
        void onLosing(String networkId, int maxMsToLive);

        /**
         * Called when the network connection is lost.
         * 
         * @param networkId String representation of the Network object
         */
        void onLost(String networkId);

        /**
         * Called when network capabilities change.
         * 
         * @param networkId    String representation of the Network object
         * @param capabilities String representation of NetworkCapabilities
         */
        void onCapabilitiesChanged(String networkId, String capabilities);
    }

    private WifiConnectionListener listener;
    private ConnectivityManager connectivityManager;

    /**
     * Set the Python listener that will receive callback events.
     * Must be called before requesting network connection.
     * 
     * @param listener Implementation of WifiConnectionListener (typically
     *                 PythonJavaClass)
     */
    public void setListener(WifiConnectionListener listener) {
        this.listener = listener;
    }

    /**
     * Provide ConnectivityManager so we can bind/unbind the process network.
     * 
     * @param connectivityManager Android ConnectivityManager instance
     */
    public void setConnectivityManager(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    @Override
    public void onAvailable(Network network) {
        if (connectivityManager != null) {
            connectivityManager.bindProcessToNetwork(network);
        }
        if (listener != null) {
            listener.onAvailable(network.toString());
        }
    }

    @Override
    public void onUnavailable() {
        if (listener != null) {
            listener.onUnavailable();
        }
    }

    @Override
    public void onLosing(Network network, int maxMsToLive) {
        if (listener != null) {
            listener.onLosing(network.toString(), maxMsToLive);
        }
    }

    @Override
    public void onLost(Network network) {
        if (connectivityManager != null) {
            connectivityManager.bindProcessToNetwork(null);
        }
        if (listener != null) {
            listener.onLost(network.toString());
        }
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        if (listener != null) {
            listener.onCapabilitiesChanged(
                    network.toString(),
                    networkCapabilities.toString());
        }
    }
}
