package mx.unam.passlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    ProgressBar pbLoadingIndicator;
    // TODO: Remove if necessary
    TextView tvMessage;
    TextView tvGroups;
    TextView tvGroup;
    TextView tvClass;
    TextView tvAssistance;

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
        tvAssistance = (TextView) findViewById(R.id.tv_assistance);   // Hold a JSON string of the changes in the student's assistance
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        if(selectedItemId == R.id.it_logout) {
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
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
                        markAssistance();
                        // Calling this method will create new a group
                        // createGroup();
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

    // TODO: Move to the activity where the user will see the table of students and pass list
    // TODO: Use the JSONObject to change the checkbox status of the given student
    private void markAssistance() {
        String classId = "1";
        String studentId = "1";
        PasslistService.markAssistance(classId, studentId, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                tvAssistance.setText(response.toString());
                tvAssistance.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("ASSISTANCE_ERROR", anError.getErrorBody());
            }
        });
    }

    // TODO: Move to the activity where the user will create a new group
    private void createGroup() {
        String name = "0000";
        String subject = "Grupo de Ejemplo";
        String beginDate = "2016-02-01";
        String endDate = "2016-02-08";

        // Each class day need to be a string array
        String[] classDay1 = {"Lunes", "08:00", "10:00"};
        String[] classDay2 = {"Viernes", "08:00", "10:00"};
        // Group the individual class days into a multidimensional string array
        String[][] classDaysArray = {classDay1, classDay2};

        // Convert a multidimensional string array into a JSONArray needed by the PasslistService.createGroup
        JSONArray classDays = JSONBuilder.buildJSONArrayFromClassDays(classDaysArray);
        PasslistService.createGroup(name, subject, beginDate, endDate, classDays, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Nuevo grupo creado!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ANError anError) {
                Log.e("CREATE_GROUP_ERROR", anError.getErrorBody());
            }
        });
    }

    private void logoutUser() {
        PasslistService.logout(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                returnToLoginActivity();
            }

            @Override
            public void onError(ANError anError) {
                Log.e("LOGIN_ERROR", anError.getErrorBody());
            }
        });
    }

    private void returnToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
