package mx.unam.passlist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class CalendarActivity extends AppCompatActivity {
    private static final int FILE_REQUEST_CODE = 1;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    TextView tvGroup, tvDates;
    LinearLayout llCalendar;
    Button btnImport;;
    String importGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        tvGroup = (TextView)findViewById(R.id.tvGroup);
        tvDates = (TextView)findViewById(R.id.tvDates);
        llCalendar = (LinearLayout)findViewById(R.id.llCalendar);
        btnImport = (Button) findViewById(R.id.btn_import);
        verifyStoragePermissions(this);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        Bundle bundle = this.getIntent().getExtras();
        String id = bundle.getString("idGroup");
        displayGroup(id);
    }

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona un Archivo"), FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            File studentsFile = new File(filePath.getPath());
            importStudents(studentsFile, importGroupId);
        }
    }

    private void displayGroup(String id) {
        String groupId = id;
        PasslistService.getGroup(groupId, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonGroup = response.getJSONObject("group");
                    importGroupId = jsonGroup.getString("id");
                    tvGroup.setText(jsonGroup.getString("name")+" - "+jsonGroup.getString("subject"));
                    tvDates.setText("Fecha de inicio:"+jsonGroup.getString("begin_date")+"\n Fecha de fin:"+jsonGroup.getString("end_date"));
                    JSONArray jsonArray = response.getJSONArray("classes");
                    for (int i=0; i< jsonArray.length(); i++){
                            final JSONObject jsonObject = jsonArray.getJSONObject(i);
                            // btn onclick action groups
                            Button btnJsonObject = new Button(getBaseContext());
                            btnJsonObject.setId(Integer.parseInt(jsonObject.getString("id")));
                            btnJsonObject.setText(jsonObject.getString("date"));
                            btnJsonObject.setWidth(llCalendar.getWidth());
                            btnJsonObject.setHeight(150);
                            btnJsonObject.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    // Obtain id when onClick event start
                                    String idBtn = String.valueOf(v.getId());
                                    // send the id to function startCalendarActivity
                                    startActivityList(idBtn);
                                }
                            });
                        llCalendar.addView(btnJsonObject);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //response.getJSONObject("");
            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("GROUP_ERROR", anError.getErrorBody());
            }
        });
    }

    private void importStudents(File studentsFile, String groupId) {
        // Replace the getFileFromAssets with the actual file selected by the user
        // For example: File studentsFile = aWayToGetTheFileFromTheUserDevice();
        //File studentsFile = FileUtils.getFileFromAssets(this, "files/students.csv", "students.csv");
        // String groupId = "1";

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

    private void startActivityList(String id){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(CalendarActivity.this, List.class);
        bundle.putString("idList", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
