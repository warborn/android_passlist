package mx.unam.passlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    ProgressBar pbLoadingIndicator;
    // TODO: Remove if necessary
    TextView tvMessage;
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
        tvMessage = (TextView) findViewById(R.id.tv_message); // Hold the user name/email messagecd
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
                        //displayUserInfo(response.getJSONObject("data"));
                        displayGroups();
                        //displayGroup();
                        //displayClass();
                        //markAssistance();
                        // Calling this method will create new a group
                        // createGroup();
                        // Calling this method will import students to an existing group
                        // importStudents();
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
                LinearLayout llMain;
                llMain = (LinearLayout)findViewById(R.id.llMain);
                Button btnAddGroup = new Button(getBaseContext());
                for (int i=0; i< response.length(); i++){
                    try {
                        final JSONObject jsonObject = response.getJSONObject(i);
                        // btn onclick action groups
                        Button btnJsonObject = new Button(getBaseContext());
                        btnJsonObject.setId(Integer.parseInt(jsonObject.getString("id")));
                        btnJsonObject.setText(jsonObject.getString("name")+" - "+jsonObject.getString("subject"));
                        btnJsonObject.setWidth(llMain.getWidth());
                        btnJsonObject.setHeight(150);
                        btnJsonObject.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                // Obtain id when onClick event start
                                String idBtn = String.valueOf(v.getId());
                                // send the id to function startCalendarActivity
                                startActivityCalendar(idBtn);
                            }
                        });
                        //btn add group
                        btnAddGroup.setText("Agregar grupos");
                        btnAddGroup.setWidth(llMain.getWidth());
                        btnAddGroup.setHeight(150);
                        btnAddGroup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Intent intent = new Intent(MainActivity.this, RegisterGroupActivity.class);
                                //startActivity(intent);
                            }
                        });
                        llMain.addView(btnJsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                llMain.addView(btnAddGroup);

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
                String errorMessage = JSONBuilder.getStringFromErrors(anError.getErrorBody());
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO: Move to the activity where the user can import a file of students
    private void importStudents() {
        // Replace the getFileFromAssets with the actual file selected by the user
        // For example: File studentsFile = aWayToGetTheFileFromTheUserDevice();
        File studentsFile = FileUtils.getFileFromAssets(this, "files/students.csv", "students.csv");
        String groupId = "1";

        PasslistService.importStudents(groupId, studentsFile, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("IMPORT_FILE_SUCCESS", response.toString());
                String successMessage = "Se han importado con exito!\n" + JSONBuilder.getStringFromImportMessages(response);
                Toast.makeText(getApplicationContext(), successMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("IMPORT_ERROR_DETAIL", anError.getErrorDetail());
                Log.e("IMPORT_FILE_ERROR", anError.getErrorBody());
                String importErrorMessages = JSONBuilder.getStringFromErrors(anError.getErrorBody());
                Toast.makeText(getApplicationContext(), importErrorMessages, Toast.LENGTH_LONG).show();
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

    private void startActivityCalendar (String id){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        bundle.putString("idGroup", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
