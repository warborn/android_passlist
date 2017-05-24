package mx.unam.passlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class List extends AppCompatActivity {
    TextView tvTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Bundle bundle = this.getIntent().getExtras();
        String id = bundle.getString("idList");
        displayClass(id);
    }

    private void displayClass(String id) {
        String classId = id;
        PasslistService.getClass(classId, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                tvTest = (TextView) findViewById(R.id.tvTest);
                tvTest.setText(response.toString());
            }

            @Override
            public void onError(ANError anError) {
                anError.printStackTrace();
                Log.e("CLASS_ERROR", anError.getErrorBody());
            }
        });
    }
}
