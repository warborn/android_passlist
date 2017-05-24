package mx.unam.passlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalendarActivity extends AppCompatActivity {
    TextView tvGroup, tvDates;
    LinearLayout llCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        tvGroup = (TextView)findViewById(R.id.tvGroup);
        tvDates = (TextView)findViewById(R.id.tvDates);
        llCalendar = (LinearLayout)findViewById(R.id.llCalendar);
        Bundle bundle = this.getIntent().getExtras();
        String id = bundle.getString("idGroup");
        displayGroup(id);
    }

    private void displayGroup(String id) {
        String groupId = id;
        PasslistService.getGroup(groupId, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonGroup = response.getJSONObject("group");
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

    private void startActivityList(String id){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(CalendarActivity.this, List.class);
        bundle.putString("idList", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
