package ddioriomendes.smartpendant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private BluetoothSerial bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, AccessoryDaemon.class);
        startService(intent);
        Log.d(TAG, "onCreate");

        bluetooth = new BluetoothSerial(this, new BluetoothSerial.BluetoothSerialListener() {
            private static final String TAG = "BluetoothSerialListener";

            @Override
            public void onDeviceFound(BluetoothDevice device, int status) {
                if(status == BluetoothSerial.STATUS_DEVICE_FOUND) {
                    Log.d(TAG, "Device found: " +
                               device.getName() +
                               "(" + device.getAddress() + ")");
                    bluetooth.connect(device);
                } else {
                    Log.e(TAG, "Requested device not found.");
                }
            }

            @Override
            public void onDeviceConnected(int status) {
                Log.d("onDeviceConnected", "" + status);
                if(status == BluetoothSerial.STATUS_DEVICE_CONNECTED) {
                    Log.d(TAG, "Device connected.");
                } else if(status == BluetoothSerial.STATUS_CANNOT_OPEN_SOCKET) {
                    Log.e(TAG, "Cannot open socket.");
                } else if(status == BluetoothSerial.STATUS_CANNOT_CONNECT) {
                    Log.e(TAG, "Cannot connect to device.");
                } else if(status == BluetoothSerial.STATUS_CANNOT_GET_STREAM) {
                    Log.e(TAG, "Cannot get input/output stream");
                }
            }

            @Override
            public void onMessageReceived(String message) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        bluetooth.findBondedDevice("HC-05");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btnRed:
                bluetooth.write("r");
                break;
            case R.id.btnGreen:
                bluetooth.write("g");
                break;
            case R.id.btnBlue:
                bluetooth.write("b");
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
