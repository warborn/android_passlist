package mx.unam.passlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    TextView tvMessage;
    ProgressBar pbLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pbLoadingIndicator.setVisibility(View.VISIBLE);
        PasslistService.validateToken(preferences, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                pbLoadingIndicator.setVisibility(View.INVISIBLE);
                try {
                    if (response.getBoolean("success")) {
                        displayUserGroups(response.getJSONObject("data"));
                    } else {
                        returnToLoginActivity();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                pbLoadingIndicator.setVisibility(View.INVISIBLE);
                anError.printStackTrace();
                returnToLoginActivity();
            }
        });
    }

    // Currently this method displays the user name in a TextView
    private void displayUserGroups(JSONObject data) {
        try {
            String message = "Hola " + data.getString("first_name") + "/" + data.getString("email");
            Log.i("USER_MESSAGE", message);
            tvMessage.setText(message);
            tvMessage.setVisibility(View.VISIBLE);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void returnToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
}
