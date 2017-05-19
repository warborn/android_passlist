package mx.unam.passlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {
    public EditText etEmail;
    public EditText etPassword;
    public Button bntSend;
    public TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Objects
        etEmail = (EditText) findViewById(R.id.Email);
        etPassword = (EditText) findViewById(R.id.Password);
        bntSend = (Button) findViewById(R.id.Send);
        tvRegister = (TextView) findViewById(R.id.Register);
        //start btnSend click listener function
        bntSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

            }
        });
    }
}
