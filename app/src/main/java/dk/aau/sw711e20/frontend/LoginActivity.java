package dk.aau.sw711e20.frontend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.termux.R;
import com.termux.app.TermuxBridge;
import com.termux.app.TermuxService;

public class LoginActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        Toast.makeText(getApplicationContext(), "you did it yay", Toast.LENGTH_SHORT).show();

        closeKeyboard();

    }

    public void clickButton () {
        Toast.makeText(getApplicationContext(), "you did it yay", Toast.LENGTH_SHORT).show();
    }

    private void closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                = (InputMethodManager)
                getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            manager
                .hideSoftInputFromWindow(
                    view.getWindowToken(), 0);
        }
    }
    }
