package mx.unam.passlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegisterGroupActivity extends AppCompatActivity {
    EditText etName;
    EditText etSubject;
    EditText etBeginDate;
    EditText etEndDate;
    EditText etDay;
    EditText etBeginHour;
    EditText etEndHour;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);
        etName = (EditText) findViewById(R.id.et_name);
        etSubject = (EditText) findViewById(R.id.et_subject);
        etBeginDate = (EditText) findViewById(R.id.et_begin_date);
        etEndDate = (EditText) findViewById(R.id.et_end_date);
        etDay = (EditText) findViewById(R.id.et_day);
        etBeginHour = (EditText) findViewById(R.id.et_begin_time);
        etEndHour = (EditText) findViewById(R.id.et_end_time);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
    }

    // TODO: Move to the activity where the user will create a new group
    private void createGroup() {
        String name = etName.getText().toString();
        String subject = etSubject.getText().toString();
        String beginDate = etBeginDate.getText().toString();
        String endDate = etEndDate.getText().toString();

        String day = etDay.getText().toString();
        String beginHour = etBeginHour.getText().toString();
        String endHour = etEndHour.getText().toString();

        // Each class day need to be a string array
        String[] classDay1 = {day, beginHour, endHour};
        // Group the individual class days into a multidimensional string array
        String[][] classDaysArray = {classDay1};

        // Convert a multidimensional string array into a JSONArray needed by the PasslistService.createGroup
        JSONArray classDays = JSONBuilder.buildJSONArrayFromClassDays(classDaysArray);
        PasslistService.createGroup(name, subject, beginDate, endDate, classDays, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Nuevo grupo creado!", Toast.LENGTH_SHORT).show();
                startMainActivity();
            }

            @Override
            public void onError(ANError anError) {
                Log.e("CREATE_GROUP_ERROR", anError.getErrorBody());
                String errorMessage = JSONBuilder.getStringFromErrors(anError.getErrorBody());
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void startMainActivity() {
        Intent intent = new Intent(RegisterGroupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
