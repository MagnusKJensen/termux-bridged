package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.termux.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends Activity {

    Button wifiButton;
    Button powerButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setDefaults("wifi", "on", getApplicationContext());
        setDefaults("power", "on", getApplicationContext());

    }

    @SuppressLint("SetTextI18n")
    public void setWifiButton(View view) {
        wifiButton = findViewById(R.id.wifiButton);
        if (wifiButton.getText().equals("On")) {
            wifiButton.setText("Off");
            setDefaults("wifi", "off", getApplicationContext());
        } else if (wifiButton.getText().equals("Off")) {
            wifiButton.setText("Always");
            setDefaults("wifi", "always", getApplicationContext());
        } else if (wifiButton.getText().equals("Always")) {
            wifiButton.setText("On");
            setDefaults("wifi", "on", getApplicationContext());
        } else {
            wifiButton.setText("you should not see this text");
        }
    }

    @SuppressLint("SetTextI18n")
    public void setPowerButton(View view) {
        powerButton = findViewById(R.id.powerButton);
        if (powerButton.getText().equals("To power")) {
            powerButton.setText("Off power");
            setDefaults("power", "off", getApplicationContext());
        } else if (powerButton.getText().equals("Off power")) {
            powerButton.setText("Always");
            setDefaults("power", "always", getApplicationContext());
        } else if (powerButton.getText().equals("Always")) {
            powerButton.setText("To power");
            setDefaults("power", "on", getApplicationContext());
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

    // TODO: also calls LTE as connected. which it should not do.
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
                setDefaults("fromTime", "always", getApplicationContext());
                setDefaults("toTime", "always", getApplicationContext());
            } else {
                // Converting every time to same date, so that the check doesnt take date into consideration

                Date fromTime = simpleDateFormat.parse(from);
                Date toTime = simpleDateFormat.parse(to);
                Date currentTime = simpleDateFormat.parse(ct);

                setDefaults("fromTime", fromTime.toString(), getApplicationContext());
                setDefaults("toTime", toTime.toString(), getApplicationContext());

                // if timewindow is passing two dates, then only one needs to be correct
                isTime = currentTime.after(fromTime) && currentTime.before(toTime);
                if (fromTime.after(toTime)) {
                    isTime = currentTime.after(fromTime) || currentTime.before(toTime);
                }
            }

            String power = getDefaults("power", getApplicationContext());
            String wifi = getDefaults("wifi", getApplicationContext());

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
}
