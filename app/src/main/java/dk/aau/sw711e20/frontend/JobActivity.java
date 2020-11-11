package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.termux.R;

import java.util.ArrayList;

public class JobActivity extends Activity {

    ListView job_listview;
    SharedPreferences.Editor editor;

    @SuppressLint("DefaultLocale")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_overview);
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = saved_values.edit();
        editor.putString("login", "true");
        editor.commit();

        //Toast.makeText(getApplicationContext(), "now logged in", Toast.LENGTH_SHORT).show();

        job_listview = findViewById(R.id.job_list);

        ArrayList<String> job_list = new ArrayList<>();

        for (int i=0; i < 100; i++) {
            job_list.add(String.format("Job %2d", i));
        }

        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_list_item_1, job_list);

        job_listview.setAdapter(ad);

        job_listview.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedJob = (String) adapterView.getItemAtPosition(i);
            Toast.makeText(getApplicationContext(), selectedJob, Toast.LENGTH_SHORT).show();
        });

    }

    public void settings_press(View view) {
        goToSettingsActivity(view);
    }

    public void goToSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void logout_press(View view) {
        editor.remove("login");
        editor.commit();
        goToLoginActivity(view);
    }

    public void goToLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
