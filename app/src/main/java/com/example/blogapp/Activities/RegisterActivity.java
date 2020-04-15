package com.example.blogapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {



    ImageView imgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;



    private EditText userMail, userPassword,userPassword2,userName;
    private ProgressBar londingProgres;
    private Button regBtn;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );


        //iniciar los views
        userMail = findViewById( R.id.regMail );
        userPassword =findViewById( R.id.regPassword );
        userPassword2=findViewById( R.id.regPassword2 );
        userName = findViewById( R.id.regName );
        londingProgres = findViewById( R.id.progressBar );
        regBtn = findViewById( R.id.regBtn );
        londingProgres.setVisibility( View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

         regBtn.setOnClickListener( new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 regBtn.setVisibility( View.INVISIBLE );
                 londingProgres.setVisibility( View.VISIBLE );
                 final String email = userMail.getText().toString();
                 final String password = userPassword.getText().toString();
                 final String password2 = userPassword2.getText().toString();
                 final String name = userName.getText().toString();


                if(email.isEmpty()|| name.isEmpty() || password.isEmpty()|| !password.equals(password2)){
                    showMessage("Please Verify all fields");


                    regBtn.setVisibility( View.VISIBLE );
                    londingProgres.setVisibility( View.INVISIBLE );

                }
                else{
                    // ya que todos los campos estan completos se puede crear la cuenta

                    CreateUserAccount(email,name,password);
                }





             }
         } );


        imgUserPhoto = findViewById( R.id.regUserPhoto );
        imgUserPhoto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                }
                else {
                   openGallery();
                }


            }
        } );
    }

    private void CreateUserAccount(String email, final String name, String password) {
          //Este metodo crea una cuenta

        mAuth.createUserWithEmailAndPassword( email,password )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //user account create succesfully
                            showMessage( "Account created" );
                            //after we create user account we need to update his profile picture and name
                            updateUserInfo(name,pickedImgUri,mAuth.getCurrentUser());
                        }
                        else{
                             // account creation failed
                            showMessage( "account creation failed" + task.getException().getMessage() );
                            regBtn.setVisibility( View.VISIBLE );
                            londingProgres.setVisibility( View.INVISIBLE );
                        }
                    }
                });

    }
    //update user photo and name
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        // first we need to upload user photo to firebase storage and get url

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child( "users_photo" );
        final StorageReference imageFilePath = mStorage.child( pickedImgUri.getLastPathSegment() );
        imageFilePath.putFile( pickedImgUri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image uploaded succesfully
                //now we can get our image url
                imageFilePath.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // uri contain user
                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri( uri )
                                .build();


                        currentUser.updateProfile( profleUpdate )
                                .addOnCompleteListener( new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            // user info update successfully
                                            showMessage( "Register Complete" );
                                            updateUI();
                                        }
                                    }
                                } );
                    }
                } );
            }
        } );

    }

    private void updateUI() {
         Intent homeActivity = new Intent( getApplicationContext(),HomeActivity.class );
         startActivity( homeActivity );
         finish();
    }

    private void showMessage(String mensaje) {
        Toast.makeText( getApplicationContext(),mensaje,Toast.LENGTH_SHORT ).show();
    }

    private void openGallery() {

        //TODO: open gallery intent and wait for user to pick and image

        Intent galleryIntent = new Intent( Intent.ACTION_GET_CONTENT );
        galleryIntent.setType( "image/*" );
        startActivityForResult( galleryIntent,REQUESCODE);
    }



    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission( RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED
        ) {
            if(ActivityCompat.shouldShowRequestPermissionRationale( RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText( RegisterActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT ).show();
            }
            else {
                ActivityCompat.requestPermissions( RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }

        else
        {
            openGallery();


        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data !=null){

            //the user has successfully picked an image
            //we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            imgUserPhoto.setImageURI(pickedImgUri);
        }
    }
}
