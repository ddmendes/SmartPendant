package ddioriomendes.smartpendant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * SmartPendants main activity.
 * @author Davi Diorio Mendes [ddioriomendes@gmail.com]
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public AccessoryDaemon accessoryDaemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.sp_launcher);
        } catch (NullPointerException e) {
            Log.e(TAG, "When styling action bar: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        accessoryDaemon = AccessoryDaemon.getInstance();
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btnRed:
                accessoryDaemon.btWrite('r');
                break;
            case R.id.btnGreen:
                accessoryDaemon.btWrite('g');
                break;
            case R.id.btnBlue:
                accessoryDaemon.btWrite('b');
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        accessoryDaemon = null;
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
