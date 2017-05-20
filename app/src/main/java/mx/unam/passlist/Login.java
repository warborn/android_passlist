package mx.unam.passlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class Login extends AppCompatActivity {
    public EditText etEmail;
    public EditText etPassword;
    public Button bntSend;
    public TextView tvRegister;
    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Objects
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        bntSend = (Button) findViewById(R.id.btn_send);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        //start btnSend click listener function
        bntSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                login(email, password);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private void login(String email, String password) {
        if(email.equals("") || password.equals("")) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_LONG).show();
        } else {
            attemptLogin("https://unam-passlist.herokuapp.com/auth/sign_in", email, password);
        }
    }

    private void attemptLogin(String signInUrl, String email, String password) {
        /** Create a JSON object like the following
         {
             "user": {
                 "email": "example@email.com",
                 "password": "password"
             }
         }
         */
        JSONObject jsonObject = new JSONObject();
        JSONObject userCredentials = new JSONObject();
        try {
            userCredentials.put("email", email);
            userCredentials.put("password", password);
            jsonObject.put("user", userCredentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send a HTTP post request to the sign_in API endpoint
        AndroidNetworking.post(signInUrl)
            .addJSONObjectBody(jsonObject)
            .setTag("signin")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                @Override
                public void onResponse(Response okHttpResponse, JSONObject response) {

                    // handle success
                    AuthUtils.saveHeadersToPreferences(okHttpResponse, preferences);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
//                    Log.i("HEADERS", okHttpResponse.headers().toString());
//                    Log.i("Token", okHttpResponse.header("Access-Token"));
//                    Log.i("Client", okHttpResponse.header("Client"));
//                    Log.i("Expiry", okHttpResponse.header("Expiry"));
//                    Log.i("Uid", okHttpResponse.header("Uid"));
                }

                @Override
                public void onError(ANError error) {
                    // handle error
                    Toast.makeText(getApplicationContext(), "Failed login", Toast.LENGTH_LONG).show();
                    if (error.getErrorCode() != 0) {
                        // received error from server
                        Log.e("ERROR", "onError errorBody : " + error.getErrorBody());
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.e("FATAL", "onError errorDetail : " + error.getErrorDetail());
                    }
                }
            });
    }
}
