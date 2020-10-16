package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
        setDefaults("wifi", "on", getApplicationContext());
        setDefaults("power", "on", getApplicationContext());


    }

    @SuppressLint("SetTextI18n")
    public void wifi_press(View view) {
        wifi_button = findViewById(R.id.wifiButton);
        if (wifi_button.getText().equals("On")) {
            wifi_button.setText("Off");
            setDefaults("wifi", "off", getApplicationContext());
        } else if (wifi_button.getText().equals("Off")) {
            wifi_button.setText("Always");
            setDefaults("wifi", "always", getApplicationContext());
        } else if (wifi_button.getText().equals("Always")) {
            wifi_button.setText("On");
            setDefaults("wifi", "on", getApplicationContext());
        } else {
            wifi_button.setText("you should not see this text");
        }
    }

    @SuppressLint("SetTextI18n")
    public void power_press(View view) {
        power_button = findViewById(R.id.powerButton);
        if (power_button.getText().equals("To power")) {
            power_button.setText("Off power");
            setDefaults("power", "off", getApplicationContext());
        } else if (power_button.getText().equals("Off power")) {
            power_button.setText("Always");
            setDefaults("power", "always", getApplicationContext());
        } else if (power_button.getText().equals("Always")) {
            power_button.setText("To power");
            setDefaults("power", "on", getApplicationContext());
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

    // TODO: also calls LTE as connected
    public boolean isConnectedToWifi() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public void prefbutton(View view) {

        String power = getDefaults("power", getApplicationContext());
        String wifi = getDefaults("wifi", getApplicationContext());

        boolean pow = isCharging() && (power.equals("on") || power.equals("always"));
        boolean notpow = !isCharging() && (power.equals("off") || power.equals("always"));
        boolean wi = isConnectedToWifi() && (wifi.equals("on") || wifi.equals("always"));
        boolean notwi = !isConnectedToWifi() && (wifi.equals("off") || wifi.equals("always"));


        if ((pow && wi) || (notpow && wi) || (pow && notwi) || (notpow && notwi)) {
            Toast.makeText(getApplicationContext(), "can be used", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "cannot be used!!", Toast.LENGTH_SHORT).show();
        }


    }

}
