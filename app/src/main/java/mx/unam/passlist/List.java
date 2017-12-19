package mx.unam.passlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class List extends AppCompatActivity {
    Button btn_Add_Student;
    TextView tv_Class_Day;
    GridLayout gl_List;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
//        btn_Add_Student = (Button)findViewById(R.id.btn_Add_Student);
        tv_Class_Day = (TextView)findViewById(R.id.tv_Class_Day);
        gl_List = (GridLayout)findViewById(R.id.gl_List);
        Bundle bundle = this.getIntent().getExtras();
        String id = bundle.getString("idList");
        displayClass(id);
    }

    private void displayClass(final String id) {
        String classId = id;
        PasslistService.getClass(classId, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                JSONArray jsonStudents = null;
                try {
                    JSONObject jsonClass = response.getJSONObject("class");
                    tv_Class_Day.setText(jsonClass.getString("date"));
                    jsonStudents = response.getJSONArray("students");
                    for (int i=0; i< jsonStudents.length(); i++){
                            final JSONObject jsonObject = jsonStudents.getJSONObject(i);
                            //show the account number
                            TextView tvAccount = new TextView(getBaseContext());
                            tvAccount.setText(jsonObject.getString("account_number"));
                            tvAccount.setTextColor(getResources().getColor(R.color.colorPrimaryText));
                            gl_List.addView(tvAccount);
                            //show the complete name
                            TextView tvName = new TextView(getBaseContext());
                            tvName.setText(jsonObject.getString("last_name")+" "+jsonObject.getString("maiden_name")+" "+jsonObject.getString("first_name"));
                            tvName.setTextColor(getResources().getColor(R.color.colorPrimaryText));
                            gl_List.addView(tvName);
                            //show de assistence checkbox
                            CheckBox chAssistence = new CheckBox(getBaseContext());
                            chAssistence.setId(Integer.parseInt(jsonObject.getString("id")));
                            chAssistence.setChecked(Boolean.parseBoolean(jsonObject.getString("assist")));
                            chAssistence.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    String idCh= String.valueOf(v.getId());
                                    markAssistance(idCh, id);
                                }
                            });
                            gl_List.addView(chAssistence);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("CLASS_ERROR", anError.getErrorBody());
            }
        });
    }

    private void markAssistance(String idStudent, String idClass) {
        String classId = idClass;
        String studentId = idStudent;
        PasslistService.markAssistance(classId, studentId, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("ASSISTANCE_ERROR", anError.getErrorBody());
            }
        });
    }
}
