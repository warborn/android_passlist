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

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {
    public EditText etName;
    public EditText etLastName;
    public EditText etMotherLastName;
    public EditText etEmail;
    public EditText etPassword;
    public EditText etPasswordConfirmation;
    public Button btnSend;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Objects
        etName = (EditText)findViewById(R.id.et_name);
        etLastName = (EditText)findViewById(R.id.et_last_name);
        etMotherLastName = (EditText)findViewById(R.id.et_mother_last_name);
        etEmail = (EditText)findViewById(R.id.et_email);
        etPassword = (EditText)findViewById(R.id.et_password);
        etPasswordConfirmation = (EditText)findViewById(R.id.et_password_confirmation);
        btnSend = (Button)findViewById(R.id.btn_send);
        //Start function onClick BtnSend
        btnSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {
                registerUser();
            }
        });
        //End function
    }

    private void registerUser() {
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        String lastName = etLastName.getText().toString();
        String motherLastName = etMotherLastName.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConfirmation = etPasswordConfirmation.getText().toString();

        PasslistService.registerUser(email, name, lastName, motherLastName, password, passwordConfirmation, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("status") && response.getString("status").equals("success")) {
                        String successMessage = "Tu cuenta ha sido registrada con exito! Por favor primer confirma tu correo electrónico";
                        Toast.makeText(getApplicationContext(), successMessage, Toast.LENGTH_LONG).show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            public void run() {
                                returnToLoginActivity();
                                finish();
                            }
                        }, 4000);
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                Log.e("REGISTER_ERROR", anError.getErrorBody());
                String errorMessages = JSONBuilder.getStringFromErrorMessages(anError.getErrorBody());
                Toast.makeText(getApplicationContext(), errorMessages, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void returnToLoginActivity() {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }
}
