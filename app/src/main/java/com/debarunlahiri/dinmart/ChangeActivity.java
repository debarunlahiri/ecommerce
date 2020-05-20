package com.debarunlahiri.dinmart;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.widget.Button;
import android.widget.EditText;

import com.debarunlahiri.dinmart.next.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ChangeActivity extends AppCompatActivity {

    private Toolbar toolbar12;

    private EditText editText2, editText3, editText4;
    private Button button8;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);


    }
}
