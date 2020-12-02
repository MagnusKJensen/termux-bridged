package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.IntRange;

import com.termux.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends Activity {

    Button wifiButton;
    Button powerButton;
    SharedPreferences.Editor editor;
    SharedPreferences saved_values;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        editor = (SharedPreferences.Editor) Preferences.prefEditor(getApplicationContext());
        saved_values = (SharedPreferences) Preferences.savedPrefs(getApplicationContext());
        String wifi = saved_values.getString("wifi", "null");
        String power = saved_values.getString("power", "null");

        if (wifi.equals("null")){
            editor.putString("wifi", "on").commit();
            wifi = "On";
        }

        if (power.equals("null")){
            editor.putString("power", "on").commit();
            power = "on";
        }

        wifi = wifi.substring(0, 1).toUpperCase() + wifi.substring(1);

        wifiButton = findViewById(R.id.wifiButton);
        wifiButton.setText(wifi);

        powerButton = findViewById(R.id.powerButton);

        switch (power) {
            case "off":
                powerButton.setText("Off power");
                break;
            case "always":
                powerButton.setText("Always");
                break;
            case "on":
                powerButton.setText("To power");
                break;
            default:
                powerButton.setText("you should not see this text");
                break;
        }


    }

    @SuppressLint("SetTextI18n")
    public void setWifiButton(View view) {
        wifiButton = findViewById(R.id.wifiButton);
        if (wifiButton.getText().equals("On")) {
            wifiButton.setText("Off");
            editor.putString("wifi", "off").commit();
        } else if (wifiButton.getText().equals("Off")) {
            wifiButton.setText("Always");
            editor.putString("wifi", "always").commit();
        } else if (wifiButton.getText().equals("Always")) {
            wifiButton.setText("On");
            editor.putString("wifi", "on").commit();
        } else {
            wifiButton.setText("you should not see this text");
        }
    }

    @SuppressLint("SetTextI18n")
    public void setPowerButton(View view) {
        powerButton = findViewById(R.id.powerButton);
        if (powerButton.getText().equals("To power")) {
            powerButton.setText("Off power");
            editor.putString("power", "off").commit();
        } else if (powerButton.getText().equals("Off power")) {
            powerButton.setText("Always");
            editor.putString("power", "always").commit();
        } else if (powerButton.getText().equals("Always")) {
            powerButton.setText("To power");
            editor.putString("power", "on").commit();
        } else {
            powerButton.setText("you should not see this text");
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
        int type = getConnectionType(getApplicationContext());
        return type == 2;
    }

    // TODO: this function also checks if unit is usable. But this check should be moved to a listener in Termux.
    public void setTimeframeButton(View view) {

        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm");
            EditText editFrom = findViewById(R.id.fromTime);
            EditText editTo = findViewById(R.id.toTime);
            String from = editFrom.getText().toString();
            String to = editTo.getText().toString();
            Calendar cd = Calendar.getInstance();
            String ct = String.valueOf(cd.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(cd.get(Calendar.MINUTE));
            boolean isTime;

            // If one or both edit texts are empty then the timeframe is always.
            if (from.isEmpty() || to.isEmpty()) {
                isTime = true;
                editor.putString("fromTime", "always").commit();
                editor.putString("toTime", "always").commit();
            } else {
                // Converting every time to same date, so that the check doesnt take date into consideration

                Date fromTime = simpleDateFormat.parse(from);
                Date toTime = simpleDateFormat.parse(to);
                Date currentTime = simpleDateFormat.parse(ct);

                editor.putString("fromTime", fromTime.toString()).commit();
                editor.putString("toTime", toTime.toString()).commit();

                // if timewindow is passing two dates, then only one needs to be correct
                isTime = currentTime.after(fromTime) && currentTime.before(toTime);
                if (fromTime.after(toTime)) {
                    isTime = currentTime.after(fromTime) || currentTime.before(toTime);
                }
            }

            String power = saved_values.getString("power", "null");
            String wifi = saved_values.getString("wifi", "null");

            boolean pow = isCharging() && (power.equals("on") || power.equals("always"));
            boolean notpow = !isCharging() && (power.equals("off") || power.equals("always"));
            boolean wi = isConnectedToWifi() && (wifi.equals("on") || wifi.equals("always"));
            boolean notwi = !isConnectedToWifi() && (wifi.equals("off") || wifi.equals("always"));


            // Small check if phone can be used.
            if (isTime && ((pow && wi) || (notpow && wi) || (pow && notwi) || (notpow && notwi))) {
                Toast.makeText(getApplicationContext(), "can be used", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "cannot be used!!", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "Time is not written correctly", Toast.LENGTH_SHORT).show();
        }
    }

    @IntRange(from = 0, to = 3)
    public static int getConnectionType(Context context) {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3;
                }
            }
        }
        return result;
    }
}

