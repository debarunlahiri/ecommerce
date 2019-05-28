package com.example.debarunlahiri.next;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BunrabActivity extends AppCompatActivity {

    private Button button12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bunrab);

        button12 = findViewById(R.id.button12);

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url ="https://drive.google.com/open?id=1lzRYlfWpU7WZ9XQ_uyjhy0imG4VvOImD";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


    }
}
