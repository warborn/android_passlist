package mx.unam.passlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private static final String AUTH_FIELD = "Access-Token";
    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        tvMessage = (TextView) findViewById(R.id.tv_message);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AuthUtils.validateHeaders(preferences, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
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
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void returnToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
}
