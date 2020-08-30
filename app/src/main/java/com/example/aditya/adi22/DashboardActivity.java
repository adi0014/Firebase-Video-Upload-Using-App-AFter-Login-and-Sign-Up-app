package com.example.aditya.adi22;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    String EmailHolder;
    TextView Email;
    Button LogOUT ;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseUser mUser;
    private Button choosebtn;
    private Button uploadbtn;
    private   ProgressBar progressBar;
    private VideoView videoView;
    private EditText videoname;
    private Uri videoUri;
    MediaController mediaController;
    private StorageReference mStorageRef;
    private DatabaseReference mDataBaseRef;
    //@SuppressLint("SetTextI18n")
    public static final String TAG="LOGIN";
    private static final int PICK_VIDEO_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

       Email = (TextView)findViewById(R.id.textView1);
        LogOUT = (Button)findViewById(R.id.button1);
        choosebtn = findViewById(R.id.choose_btn);
        uploadbtn = findViewById(R.id.upload_btn);
        videoView = findViewById(R.id.video_view);
        progressBar = findViewById(R.id.progress_bar);
        videoname = findViewById(R.id.et_video_name);
        Intent intent = getIntent();
        mediaController = new MediaController(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("videos");
        mDataBaseRef = FirebaseDatabase.getInstance().getReference("videos");

        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();
        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseVideo();
            }
        });
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadVideo();
            }
        });




        // Receiving User Email Send By MainActivity.
        EmailHolder = intent.getStringExtra(login.userEmail);

        // Setting up received email to TextView.
        Email.setText(Email.getText().toString()+ EmailHolder);

        // Adding click listener to Log Out button.

        LogOUT.setOnClickListener(new View.OnClickListener() {
           // @Override
            public void onClick(View v) {


                //Finishing current DashBoard activity on button click.
               finish();

                Toast.makeText(DashboardActivity.this,"Log Out Successful", Toast.LENGTH_LONG).show();
                //Intent intent=new Intent(DashboardActivity.this,login.class);
                //startActivity(intent);
               /*if (v.getId() == R.id.button1) {
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    startActivity(new Intent(DashboardActivity.this, login.class));
                                    finish();
                                }
                            });
                }*/

            }
        });

    }
    private  void ChooseVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            videoUri = data.getData();

            videoView.setVideoURI(videoUri);


        }}

    private String getFileExtension(Uri videoUri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(videoUri));
    }

    private void UploadVideo() {

        progressBar.setVisibility(View.VISIBLE);
        if (videoUri != null){
            StorageReference reference = mStorageRef.child(System.currentTimeMillis() +
                    "." +getFileExtension(videoUri));

            reference.putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),"Upload successful",Toast.LENGTH_SHORT).show();
                            Member member = new Member(videoname.getText().toString().trim(),
                                    taskSnapshot.getUploadSessionUri().toString());
                            String UploadId = mDataBaseRef.push().getKey();
                            mDataBaseRef.child(UploadId).setValue(member);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });


        }else {
            Toast.makeText(getApplicationContext(),"No file selected",Toast.LENGTH_SHORT).show();
        }


    }


}