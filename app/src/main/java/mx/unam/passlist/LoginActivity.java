package mx.unam.passlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONObject;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public EditText etEmail;
    public EditText etPassword;
    public Button bntSend;
    public TextView tvRegister;
    public SharedPreferences preferences;
    ProgressBar pbLoadingIndicator;

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
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        //start btnSend click listener function
        bntSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                login(email, password);
            }
        });

        // Start the register activity
        tvRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }

    // Show an error message or send the actual login request
    private void login(String email, String password) {
        if(email.equals("") || password.equals("")) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            attemptLogin(email, password);
        }
    }

    // Send a login request to the API with the given email and password values
    private void attemptLogin(String email, String password) {
        pbLoadingIndicator.setVisibility(View.VISIBLE);
        PasslistService.login(email, password, new OkHttpResponseAndJSONObjectRequestListener() {
            @Override
            public void onResponse(Response okHttpResponse, JSONObject response) {// handle success
                pbLoadingIndicator.setVisibility(View.INVISIBLE);
                // On success save authentication information inside of the request headers into a shared preference
                AuthUtils.saveAuthHeadersToPreferences(okHttpResponse, preferences);

                // Start the main activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(ANError error) {
                pbLoadingIndicator.setVisibility(View.INVISIBLE);
                // handle error
                Log.e("ERROR_BODY", error.getErrorBody());
                // Show the authentication errors in a toast
                String errorMessage = JSONBuilder.getStringFromErrors(error.getErrorBody());
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }
}
