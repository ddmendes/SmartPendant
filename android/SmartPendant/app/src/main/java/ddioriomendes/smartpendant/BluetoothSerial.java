package ddioriomendes.smartpendant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Controls the bluetooth connection and communication routines.
 * @author Davi Diorio Mendes [ddioriomendes@gmail.com]
 */
public class BluetoothSerial {
    public static final String TAG = "BluetoothSerial";

    public static final int STATUS_DEVICE_FOUND = 0;
    public static final int STATUS_DEVICE_NOT_FOUND = 1;
    public static final int STATUS_DEVICE_CONNECTED = 2;
    public static final int STATUS_CANNOT_OPEN_SOCKET = 3;
    public static final int STATUS_CANNOT_CONNECT = 4;
    public static final int STATUS_CANNOT_GET_STREAM = 5;

    public static final int STATE_IDLE = 10;
    public static final int STATE_CONNECTING = 11;
    public static final int STATE_CONNECTED = 12;

    private int state;
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothSerialListener listener;
    private BluetoothSocket mBluetoothSocket;
    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;
    private final Context mContext;



    public BluetoothSerial(Context context, BluetoothSerialListener externalListener) {
        Log.d(TAG, "Constructing BluetoothSerial.");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothSocket = null;

        listener = externalListener;
        mContext = context;

        state = STATE_IDLE;
    }

    public Boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public void findBondedDevice(String deviceNamePrefix) {
        new AsyncFindBondedDevice(deviceNamePrefix).execute();
    }

    public void connect(BluetoothDevice device) {
        state = STATE_CONNECTING;
        new AsyncConnect(device).execute();
    }

    public void write(String message) {
        if(state != STATE_CONNECTED) {
            Log.e(TAG, "Cannot write to bluetooth accessory. Not connected.");
            return;
        }

        byte[] buffer = message.getBytes();
        Log.d(TAG, message);

        try {
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "While writing to stream: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        if(state == STATE_CONNECTED) {
            mBluetoothSocket.close();
            mBluetoothSocket = null;

            inputStream.close();
            inputStream = null;

            outputStream.close();
            outputStream = null;
        }

        state = STATE_IDLE;
    }

    private class AsyncFindBondedDevice extends AsyncTask<Void, Void, BluetoothDevice> {

        private final String prefix;

        public AsyncFindBondedDevice(String namePrefix) {
            prefix = namePrefix;
        }

        @Override
        protected BluetoothDevice doInBackground(Void... params) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().startsWith(prefix)) {
                    return device;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(BluetoothDevice bluetoothDevice) {
            if(bluetoothDevice != null) {
                listener.onDeviceFound(bluetoothDevice, STATUS_DEVICE_FOUND);
            } else {
                listener.onDeviceFound(null, STATUS_DEVICE_NOT_FOUND);
            }
        }
    }

    private class AsyncConnect extends AsyncTask<Void, Void, BluetoothSocket> {

        private BluetoothDevice mBluetoothDevice;

        public AsyncConnect(BluetoothDevice device) {
            mBluetoothDevice = device;
        }

        @Override
        protected BluetoothSocket doInBackground(Void... params) {
            String uuid = mContext.getResources().getString(R.string.bt_serial_uuid);
            BluetoothSocket bSocket;

            try {
                bSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid));
            } catch (IOException e) {
                listener.onDeviceConnected(STATUS_CANNOT_OPEN_SOCKET);
                state = STATE_IDLE;
                return null;
            }

            mBluetoothAdapter.cancelDiscovery();

            try {
                bSocket.connect();
            } catch (IOException e) {
                try {
                    bSocket.close();
                } catch (IOException e1) {}
                listener.onDeviceConnected(STATUS_CANNOT_CONNECT);
                state = STATE_IDLE;
                return null;
            }

            return bSocket;
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            if(bluetoothSocket != null) {
                mBluetoothSocket = bluetoothSocket;

                InputStream tmpInput;
                OutputStream tmpOutput;
                try {
                    tmpInput = mBluetoothSocket.getInputStream();
                    tmpOutput = mBluetoothSocket.getOutputStream();
                } catch (IOException e) {
                    listener.onDeviceConnected(STATUS_CANNOT_GET_STREAM);
                    try {
                        close();
                    } catch (IOException e1) {}
                    state = STATE_IDLE;
                    return;
                }

                inputStream = new BufferedInputStream(tmpInput);
                outputStream = new BufferedOutputStream(tmpOutput);

                state = STATE_CONNECTED;
                new ConnectionListener().start();
                listener.onDeviceConnected(STATUS_DEVICE_CONNECTED);
            }
        }
    }

    private class ConnectionListener extends Thread {
        byte[] buffer = new byte[1024];
        String message = "";

        @Override
        public void run() {
            int n;
            try {
                while(state == STATE_CONNECTED) {
                    n = inputStream.read(buffer);
                    message = message.concat(new String(buffer, 0, n, "UTF-8"));
                    Log.d("connectionListener", message);

                    if(message.endsWith("\n")) {
                        listener.onMessageReceived(message);
                        message = "";
                    }
                }
            } catch (IOException e) {
                connectionLost();
            }
        }
    }

    private void connectionLost() {
        Log.e(TAG, "Bluetooth connection lost.");
        state = STATE_IDLE;
        try {
            close();
        } catch (IOException e) {
            Log.e(TAG, "While closing stream and socket due to lost connection: " + e.getMessage());
        }
        listener.onConnectionLost();
    }

    public interface BluetoothSerialListener {

        void onDeviceFound(BluetoothDevice device, int status);
        void onDeviceConnected(int status);
        void onMessageReceived(String message);
        void onConnectionLost();

    }
}
