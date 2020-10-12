package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.termux.R;

public class SettingsActivity extends Activity {

    Button wifi_button;
    Button power_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


    }

    @SuppressLint("SetTextI18n")
    public void wifi_press(View view) {
        wifi_button = findViewById(R.id.wifiButton);
        if (wifi_button.getText().equals("On")) {
            wifi_button.setText("Off");
        } else if (wifi_button.getText().equals("Off")) {
            wifi_button.setText("Always");
        } else if (wifi_button.getText().equals("Always")) {
            wifi_button.setText("On");
            if (isConnectedToWifi()) {
                Toast.makeText(getApplicationContext(), "is connected to wifi", Toast.LENGTH_SHORT).show();
            } else if (!isConnectedToWifi()) {
                Toast.makeText(getApplicationContext(), "not connected to wifi", Toast.LENGTH_SHORT).show();
            }
        } else {
            wifi_button.setText("you should not see this text");
        }
    }

    @SuppressLint("SetTextI18n")
    public void power_press(View view) {
        power_button = findViewById(R.id.powerButton);
        if (power_button.getText().equals("To power")) {
            power_button.setText("Off power");
        } else if (power_button.getText().equals("Off power")) {
            power_button.setText("Always");
        } else if (power_button.getText().equals("Always")) {
            power_button.setText("To power");
            if (isCharging()) {
                Toast.makeText(getApplicationContext(), "phone is charging", Toast.LENGTH_SHORT).show();
            } else if (!isCharging()) {
                Toast.makeText(getApplicationContext(), "phone is not charging", Toast.LENGTH_SHORT).show();
            }
        } else {
            power_button.setText("you should not see this text");
        }
    }

    public boolean isCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        assert batteryStatus != null;
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    public boolean isConnectedToWifi() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
