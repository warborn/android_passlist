package mx.unam.passlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private static final String AUTH_FIELD = "Access-Token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (preferences.contains(AUTH_FIELD)) {

        } else {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
    }
}
