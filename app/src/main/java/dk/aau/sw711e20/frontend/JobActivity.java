package dk.aau.sw711e20.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.termux.R;

import java.util.ArrayList;

public class JobActivity extends Activity {

    ListView job_listview;

    @SuppressLint("DefaultLocale")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_overview);

        job_listview = (ListView)findViewById(R.id.job_list);

        ArrayList<String> job_list = new ArrayList<>();

        for (int i=0; i < 100; i++) {
            job_list.add(String.format("Job %2d", i));
        }

        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_list_item_1, job_list);

        job_listview.setAdapter(ad);

    }

    public void logout_press(View view) {
        goToJobActivity(view);
    }

    public void goToJobActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
