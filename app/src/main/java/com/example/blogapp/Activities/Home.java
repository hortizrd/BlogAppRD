package com.example.blogapp.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.blogapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home2 );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        //ini

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();



        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
                        .setAction( "Action", null ).show();
            }
        } );
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        NavigationView navigationView = findViewById( R.id.nav_view );
        // Llamada al metodo


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow )
                .setDrawerLayout( drawer )
                .build();
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
        NavigationUI.setupActionBarWithNavController( this, navController, mAppBarConfiguration );
        NavigationUI.setupWithNavController( navigationView, navController );
        updateNaHeader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.home, menu );
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
        return NavigationUI.navigateUp( navController, mAppBarConfiguration )
                || super.onSupportNavigateUp();
    }

    public void updateNaHeader(){
        NavigationView navigationView = findViewById( R.id.nav_view );
        View headerView = navigationView.getHeaderView( 0 );
        TextView navUsername = headerView.findViewById( R.id.nav_username);
        TextView navUserMail = headerView.findViewById( R.id.nav_user_mail );
        ImageView navUserPhot= headerView.findViewById( R.id.nav_user_photo );

        navUserMail.setText( currentUser.getEmail() );
        navUsername.setText( currentUser.getDisplayName() );

        //now we will use Glide to load user image
        //first we need to import the library

        Glide.with(this).load( currentUser.getPhotoUrl()).into( navUserPhot );


    }
}
