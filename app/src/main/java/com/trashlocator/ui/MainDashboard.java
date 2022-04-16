package com.trashlocator.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.trashlocator.R;
import com.trashlocator.ui.firebase.FirebaseInit;
import com.trashlocator.ui.picsview.UploadedPics;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainDashboard extends AppCompatActivity {
    public Button imageButtonGallery;
    public Button imageButtonCamera;
    public Button uploadButton;

    public ProgressDialog progressDialog;
    public Boolean locationPicked = false, imagechosen = false, imageUploaded = false, dataUploaded = false;
    public static final int RESULT_OK = -1;
    public String phonenumber;
    public Uri imageUri;
    public String timeStamp;
    //
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    ImageView selectedImage;
    String currentPhotoPath;
    String imageUrl;
    //    File f;
    StorageReference storageReference;
    //
    private FirebaseStorage firebaseStorage;
    public DatabaseReference databaseReference;
    public DatabaseReference databaseReferencePictures;
    //Location vars
    public FusedLocationProviderClient fusedLocationProviderClient;
    public double latitude, longitude;
    public String currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageButtonGallery = findViewById(R.id.upload_pic_gallery_button);
        imageButtonCamera = findViewById(R.id.upload_pic_camera_button);
        uploadButton = findViewById(R.id.upload_pic_button);
        selectedImage = findViewById(R.id.upload_image);

        //PHONE NUMBER from SHARED PREFERENCES
        //Instantiate
        SharedPreferences sharedPreferences = getSharedPreferences("com.trashlocator.userdetails", Context.MODE_PRIVATE);
        phonenumber = sharedPreferences.getString("PHONE", null);
        Log.d("SharedPreference", "Phone::" + sharedPreferences.getString("PHONE", null));

        //firebase hooks
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        //Initialized fusedLocationProvider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ////////////////////////////CHOOSE IMAGE FROM GALLERY BUTTON
        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromgallery();
            }
        });

        ////////////////////////////CAPTURE FROM CAMERA BUTTON

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        ////////////////////////////UPLOAD BUTTON

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set ProgressDialog
                progressDialog = new ProgressDialog(MainDashboard.this);
                //show progressDialog
                progressDialog.show();
                //set contentView for progressDialog
                progressDialog.setContentView(R.layout.progress_dialog);
                //set transparent background
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );
                pickCurrentLocation();
            }
        });

        /////////////////////////////VIEW UPLOADED IMAGES

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //INTENT FOR CLOUD GALLERY
                Intent intent = new Intent(MainDashboard.this, UploadedPics.class);
                startActivity(intent);
            }
        });
    }

    private void pickCurrentLocation() {
        //Check Permission
        if (ActivityCompat.checkSelfPermission(MainDashboard.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Inside pick current loc. has permission ");
            getLocation();
        } else {
            ActivityCompat.requestPermissions(MainDashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            Log.d("TAG", "Inside pick current loc. NO permission ");
        }

    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialise Location
                Location location = task.getResult();
                if (location != null) {

                    try {
                        //Initialise geocoder
                        Geocoder geocoder = new Geocoder(MainDashboard.this, Locale.getDefault());
                        //Initilise Address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        //Assign variables
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                        currentAddress = addresses.get(0).getAddressLine(0);
                        Log.d("TAG", "**********Location******* LAT :" + latitude + " LONG :" + longitude + " ADDR :" + currentAddress);
                        if(imagechosen)
                            uploadImageToFirebase();
                        else
                            Toast.makeText(MainDashboard.this, "Select an image", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    Log.d("TAG", "Location null ");
                }
            }
        });

    }

    public void chooseImageFromgallery(){
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.trashlocator.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);*/
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private boolean uploadImageToFirebase() {
        Log.d("INSIDE------>", "uploadIMAGE: ");
        timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());
        String filePath=phonenumber;
        Log.d("IMAGE URI------>", imageUri.toString());
        UploadTask uploadTask = storageReference.child("capture/"+timeStamp).putFile(imageUri);
        Log.d("INSIDE------>", "file PUT!");
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainDashboard.this, "Umm..something went wrong", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageUploaded=true;
                Log.d("INSIDE------>", "upload IMAGE::UPLOAD SUCCESS");

                storageReference.child("capture/"+timeStamp).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl=uri.toString();
                        uploadDataToFirebase();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainDashboard.this, "Umm..something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        return imageUploaded;
    }

    private boolean uploadDataToFirebase() {

        //FIREBASE REFERENCE

        //User Directory
        databaseReference = FirebaseInit.getDatabase().getReference().child("USERS").child(phonenumber).child("photos");
        String pushId=databaseReference.push().getKey();
        HashMap<String, String> imageDetails = new HashMap<>();
        imageDetails.put("imagelink", imageUrl);
        databaseReference.child(pushId).setValue(imageDetails);

        //Pictures Directory
        databaseReferencePictures = FirebaseInit.getDatabase().getReference().child("PICTURES");
        String pushIdAtPictures=databaseReference.push().getKey();
        HashMap<String, String> imageMetaData = new HashMap<>();
        imageMetaData.put("imagelink", imageUrl);
        imageMetaData.put("phonenumber", phonenumber);
        imageMetaData.put("id", pushIdAtPictures);
        imageMetaData.put("timestamp", timeStamp);
        databaseReferencePictures.child(pushIdAtPictures).setValue(imageMetaData);
        //Cordinates & Address
        databaseReferencePictures.child(pushIdAtPictures).child("location").child("latitude").setValue(latitude);   //LATITUDE
        databaseReferencePictures.child(pushIdAtPictures).child("location").child("longitude").setValue(longitude);   //LONGITUDE
        databaseReferencePictures.child(pushIdAtPictures).child("location").child("address").setValue(currentAddress);   //LONGITUDE

        //Dismiss progressdialog
        progressDialog.dismiss();

        Intent intent=new Intent(MainDashboard.this,Uploaded.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            selectedImage.setImageURI(imageUri);
            imagechosen=true;
        }
        else if (requestCode==CAMERA_REQUEST_CODE){
            Log.d("tag", "After request code conditn");
            if(resultCode == Activity.RESULT_OK  ){
                Log.d("tag", "After result code conditn");
                imagechosen=true;

                    File f = new File(currentPhotoPath);
                    selectedImage.setImageURI(Uri.fromFile(f));
                    Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    this.sendBroadcast(mediaScanIntent);
                    imageUri=contentUri;

            }

        }
    }
}