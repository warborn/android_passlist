package mx.unam.passlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity {
    public EditText etName;
    public EditText etLastName;
    public EditText etLastMotherName;
    public EditText etEmail;
    public EditText etPassword;
    public Button btnSend;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Objects
        etName = (EditText)findViewById(R.id.et_name);
        etLastName = (EditText)findViewById(R.id.et_last_name);
        etLastMotherName = (EditText)findViewById(R.id.et_mother_last_name);
        etEmail = (EditText)findViewById(R.id.et_email);
        etPassword = (EditText)findViewById(R.id.et_password);
        btnSend = (Button)findViewById(R.id.btn_send);
        //Start function onClick BtnSend
        btnSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0) {

            }
        });
        //End function
    }
}
