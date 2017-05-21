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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    ProgressBar pbLoadingIndicator;
    // TODO: Remove if necessary
    TextView tvMessage;
    TextView tvGroups;
    TextView tvGroup;
    TextView tvClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        // TODO: Remove if necessary
        tvMessage = (TextView) findViewById(R.id.tv_message); // Hold the user name/email message
        tvGroups = (TextView) findViewById(R.id.tv_groups);   // Hold a JSON string of all the groups of the logged in user
        tvGroup = (TextView) findViewById(R.id.tv_group);     // Hold a JSON string of single group (including the classes calendar)
        tvClass = (TextView) findViewById(R.id.tv_class);     // Hold a JSON string of a single class (including the list of students)
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
                        displayUserInfo(response.getJSONObject("data"));
                        displayGroups();
                        displayGroup();
                        displayClass();
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

    // TODO: Remove if necessary
    private void displayUserInfo(JSONObject data) {
        try {
            String message = "Hola " + data.getString("first_name") + "/" + data.getString("email");
            Log.i("USER_MESSAGE", message);
            tvMessage.setText(message);
            tvMessage.setVisibility(View.VISIBLE);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    // TODO: Use the JSONArray response to display the groups to the user
    private void displayGroups() {
        PasslistService.getGroups(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                tvGroups.setText(response.toString());
                tvGroups.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
            }
        });
    }

    // TODO: Move to the activity where the user will see the classes calendar
    // TODO: Use the JSONObject to display the group information (optionally)
    // TODO: Use the JSONObject to display all of the classes in the calendar
    private void displayGroup() {
        String groupId = "1";
        PasslistService.getGroup(groupId, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                tvGroup.setText(response.toString());
                tvGroup.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("GROUP_ERROR", anError.getErrorBody());
            }
        });
    }

    // TODO: Move to the activity where the user will see the table of students and pass list
    // TODO: Use the JSONObject to display the class information (optionally)
    // TODO: Use the JSONObject to display all of the students in the calendar
    private void displayClass() {
        String classId = "1";
        PasslistService.getClass(classId, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                tvClass.setText(response.toString());
                tvClass.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("CLASS_ERROR", anError.getErrorBody());
            }
        });
    }

    private void returnToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }
}
