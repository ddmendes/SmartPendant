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
 * Created by ddiorio on 30-Sep-15.
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

    public void findBondedDevice(String deviceNamePrefix) {
        asyncFindBondedDevice.execute(deviceNamePrefix);
    }

    public void connect(BluetoothDevice device) {
        state = STATE_CONNECTING;
        asyncConnect.execute(device);
    }

    public void write(String message) {
        if(state != STATE_CONNECTED) {
            Log.e(TAG, "Cannot write to bluetooth accessory. Not connected.");
            return;
        }

        byte[] buffer = message.getBytes();
        Log.d(TAG, new String(buffer));

        try {
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "While writing to stream: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        mBluetoothSocket.close();
        mBluetoothSocket = null;

        inputStream.close();
        inputStream = null;

        outputStream.close();
        outputStream = null;
    }

    private final AsyncTask<String, Void, BluetoothDevice> asyncFindBondedDevice = new AsyncTask<String, Void, BluetoothDevice>() {
        @Override
        protected BluetoothDevice doInBackground(String... params) {
            if(params.length > 1) {
                Log.e("asyncFileBondedDevice", "More than one name prefix requested. Request just one name prefix.");
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().startsWith(String.valueOf(params[0]))) {
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
    };

    private final AsyncTask<BluetoothDevice, Void, BluetoothSocket> asyncConnect = new AsyncTask<BluetoothDevice, Void, BluetoothSocket>() {
        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... params) {
            if(params.length > 1) {
                Log.e("asyncFileBondedDevice", "Connection requested to more than one device. The connection will run just for the first device.");
            }

            String uuid = mContext.getResources().getString(R.string.bt_serial_uuid);
            BluetoothSocket bSocket = null;

            try {
                bSocket = params[0].createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid));
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

                InputStream tmpInput = null;
                OutputStream tmpOutput = null;
                try {
                    tmpInput = mBluetoothSocket.getInputStream();
                    tmpOutput = mBluetoothSocket.getOutputStream();
                } catch (IOException e) {
                    listener.onDeviceConnected(STATUS_CANNOT_GET_STREAM);
                    try {
                        mBluetoothSocket.close();
                        tmpInput.close();
                        tmpOutput.close();
                    } catch (IOException e1) {}
                    state = STATE_IDLE;
                    return;
                }

                inputStream = new BufferedInputStream(tmpInput);
                outputStream = new BufferedOutputStream(tmpOutput);

                state = STATE_CONNECTED;
                listener.onDeviceConnected(STATUS_DEVICE_CONNECTED);
            }
        }
    };

    Thread connectionListener = new Thread() {
        byte[] buffer = new byte[1024];

        @Override
        public void run() {
            // TODO listen for incoming message
            try {
                inputStream.read(buffer);
                Log.d("connectionListener", new String(buffer));
            } catch (IOException e) {
                connectionLost();
            }
        }
    };

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

        public void onDeviceFound(BluetoothDevice device, int status);
        public void onDeviceConnected(int status);
        public void onMessageReceived(String message);
        public void onConnectionLost();

    }
}
