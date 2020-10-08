package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        } else {
            power_button.setText("you should not see this text");
        }
    }
}
