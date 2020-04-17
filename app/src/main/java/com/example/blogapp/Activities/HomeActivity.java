package com.example.blogapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blogapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button btnSalir;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        btnSalir =findViewById( R.id.home_btnsalir );
        mAuth = FirebaseAuth.getInstance();

        btnSalir.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                Salir();
            }
        } );

    }

    private void signOut(){
        mAuth.signOut();
        finish();
    }
    public void Salir(){

        Intent RegresarLogin = new Intent( getApplicationContext(),LoginActivity.class );
        startActivity( RegresarLogin );
    }
}
