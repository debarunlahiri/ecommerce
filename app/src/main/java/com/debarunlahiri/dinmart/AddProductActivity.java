package com.debarunlahiri.dinmart;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.debarunlahiri.dinmart.activity.LoginActivity;
import com.debarunlahiri.dinmart.next.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import id.zelory.compressor.Compressor;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar2;

    private EditText etAddProductName, etAddProductPrice, etAddProductDescription, etUnit, etAddProductQuantity;
    private Button addbtn;
    private ImageView imageView5;
    private ProgressDialog pd;
    private Spinner sProductCategory, spWeightUnit;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private String user_id = null;
    private String email;
    private String ds_email;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;

    private String company_name;
    private String key;
    private String product_name;
    private String product_price;
    private String product_description;
    private String product_category;
    private String product_weight_unit;
    private String product_quantity;

    private Context mContext;
    public Uri postImageUri = null;
    public Bitmap mCompressedStoryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        toolbar2 = findViewById(R.id.toolbar2);
        toolbar2.setTitleTextColor(Color.WHITE);
        toolbar2.setTitle("Add a new product");
        setSupportActionBar(toolbar2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar2.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etAddProductName = findViewById(R.id.etAddProductName);
        etAddProductPrice = findViewById(R.id.etAddProductPrice);
        etAddProductDescription = findViewById(R.id.etAddProductDescription);
        addbtn = findViewById(R.id.addbtn);
        imageView5 = findViewById(R.id.imageView5);
        sProductCategory = findViewById(R.id.sProductCategory);
        etUnit = findViewById(R.id.etUnit);
        spWeightUnit = findViewById(R.id.spWeightUnit);
        etAddProductQuantity = findViewById(R.id.etAddProductQuantity);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://next-3f50a.appspot.com");   //change the url according to your firebase app

        if (currentUser == null) {
            sendToLogin();
        } else {
            user_id = mAuth.getCurrentUser().getUid();

            ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.spinner, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            sProductCategory.setAdapter(arrayAdapter);
            sProductCategory.setOnItemSelectedListener(this);

            ArrayAdapter<CharSequence> arrayAdapter2 = ArrayAdapter.createFromResource(this, R.array.weight_unit, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spWeightUnit.setAdapter(arrayAdapter2);
            spWeightUnit.setOnItemSelectedListener(this);

            getRegisteredUserDetails();

            imageView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dexter.withActivity(AddProductActivity.this)
                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {
                                @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(AddProductActivity.this);
//                                    Intent intent = new Intent();
//                                    intent.setType("image/*");
//                                    intent.setAction(Intent.ACTION_PICK);
//                                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                                }
                                @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                                    PermissionListener dialogPermissionListener =
                                            DialogOnDeniedPermissionListener.Builder
                                                    .withContext(AddProductActivity.this)
                                                    .withTitle("Storage permission")
                                                    .withMessage("Storage permission is needed in order pick images")
                                                    .withButtonText(android.R.string.ok)
                                                    .withIcon(R.drawable.din_logo)
                                                    .build();

                                }
                                @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();

                }
            });
            addbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getInputDetails();
                }
            });
        }

    }

    private void getRegisteredUserDetails() {
//        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                email  = dataSnapshot.child("email").getValue().toString();
//                getCompanyDetails();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void getCompanyDetails() {
        mDatabase.child("business").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds_email = ds.child("email").getValue().toString();
                    if (ds_email.equals(email)) {
                        key = ds.getKey();
                        company_name = ds.child("company_name").getValue().toString();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getInputDetails() {
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        product_name = etAddProductName.getText().toString();
        product_price = etAddProductPrice.getText().toString();
        product_description = etAddProductDescription.getText().toString();
        product_quantity = etAddProductQuantity.getText().toString();
//        Toast.makeText(getApplicationContext(), product_category + product_weight_unit, Toast.LENGTH_LONG).show();

        if (product_name.isEmpty()) {
             etAddProductName.setError("Enter product name");
        } else if (product_price.isEmpty()) {
            etAddProductPrice.setError("Enter product price");
        } else if (product_description.isEmpty()) {
            etAddProductDescription.setError("Enter product description");
        } else  if (product_category.equals("Select Category")) {
            Toast.makeText(getApplicationContext(), "Please select a category", Toast.LENGTH_LONG).show();
        } else {
            try {
                uploadDetails();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                Picasso.get().load(filePath).into(imageView5);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void uploadDetails() throws IOException {
        if(filePath != null) {
            pd.show();
            File mFileGroupProfileImage = new File(filePath.getPath());

            mCompressedStoryImage = new Compressor(AddProductActivity.this).setQuality(10).compressToBitmap(mFileGroupProfileImage);

            ByteArrayOutputStream mProfileBAOS = new ByteArrayOutputStream();
            mCompressedStoryImage.compress(Bitmap.CompressFormat.JPEG, 10, mProfileBAOS);
            byte[] mProfileThumbData = mProfileBAOS.toByteArray();


            final Long ts_long = System.currentTimeMillis()/1000;
            final String ts = ts_long.toString();
            final StorageReference childRef = storageReference.child("product_images/" + ts + ".jpg");

            //uploading the image
            final UploadTask uploadTask = childRef.putBytes(mProfileThumbData);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return childRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String mUri = downloadUri.toString();
//                                    Toast.makeText(getApplicationContext(), mUri, Toast.LENGTH_LONG).show();
                                    final String product_key = mDatabase.child("products").child(product_category).push().getKey();
                                    final HashMap<String, Object> dataMap = new HashMap<>();
                                    dataMap.put("product_image", mUri);
                                    dataMap.put("product_name", product_name);
                                    dataMap.put("product_price", product_price);
                                    dataMap.put("product_description", product_description);
                                    dataMap.put("product_added_time", ts_long);
                                    dataMap.put("product_category", product_category);
                                    dataMap.put("product_weight_unit", product_weight_unit);
                                    dataMap.put("product_key", product_key);
                                    dataMap.put("admin_user_id", user_id);
                                    dataMap.put("product_quantity", product_quantity);
                                    mDatabase.child("products").child(product_key).setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
//                                                mDatabase.child("business").child(key).child("products").child(product_key).setValue(dataMap);
                                                Toast.makeText(getApplicationContext(), "Product added successfully", Toast.LENGTH_LONG).show();
                                                Intent settingsIntent = new Intent(AddProductActivity.this, MainActivity.class);
                                                startActivity(settingsIntent);
                                                finish();

                                            } else {
                                                String errMsg = task.getException().getMessage();
                                                Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    // Handle failures
                                    // ...
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Download Uri Error: " + errMsg, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } else {
                        String errMsg = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Upload Failed: " + e, Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Toast.makeText(AddProductActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendToLogin() {
        Intent loginIntent = new Intent(AddProductActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sProductCategory) {
            product_category = parent.getItemAtPosition(position).toString();
        }

        if (parent.getId() == R.id.spWeightUnit) {
            product_weight_unit = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
