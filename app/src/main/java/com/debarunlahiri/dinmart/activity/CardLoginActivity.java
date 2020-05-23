package com.debarunlahiri.dinmart.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.MainActivity;
import com.debarunlahiri.dinmart.next.R;
import com.debarunlahiri.dinmart.utils.Variables;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CardLoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private static final String TAG = "CardLoginActivity";
    private ImageButton ibCardLoginClose;
    private TextView tvOtpMessage, tvLogin;
    private EditText etPhone, etOtp, etName, etAddress;
    private Button bLogin, bOtpRetry;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public String phone, name, address, user_id;
    private Location location;
    private TextView tvClockMsg;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private GoogleMap mMap;
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_login);

        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

        ibCardLoginClose = findViewById(R.id.ibCardLoginClose);
        etPhone = findViewById(R.id.etPhone);
        etOtp = findViewById(R.id.etOtp);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        bLogin = findViewById(R.id.bLogin);
        tvOtpMessage = findViewById(R.id.tvOtpMessage);
        bOtpRetry = findViewById(R.id.bOtpRetry);
        tvLogin = findViewById(R.id.tvLogin);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        etOtp.setVisibility(View.GONE);
        etAddress.setVisibility(View.GONE);
        etName.setVisibility(View.GONE);
        tvOtpMessage.setVisibility(View.GONE);
        bOtpRetry.setVisibility(View.GONE);

        bLogin.setText("Get OTP");
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bLogin.getText().toString().equalsIgnoreCase("Submit OTP")) {
                    initOtp(phone);
                } else {
                    final String otp = etOtp.getText().toString();
                    phone = etPhone.getText().toString();
                    if (phone.isEmpty()) {
                        etPhone.setError("Enter phone number");
                    } else {
                        etOtp.setVisibility(View.VISIBLE);
//                        bLogin.setText("Submit OTP");
                        initOtp(phone);
                    }
                }
            }
        });

        ibCardLoginClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etAddress.getVisibility() == View.VISIBLE && etAddress.getText().toString().isEmpty() && etName.getVisibility() == View.VISIBLE && etName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please complete your details", Toast.LENGTH_LONG).show();
                } else  {
                    onBackPressed();
                }
            }
        });
    }

    private void initOtp(final String phone_number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phone_number, 60, TimeUnit.SECONDS, CardLoginActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                etOtp.setText(phoneAuthCredential.getSmsCode());
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    tvOtpMessage.setVisibility(View.VISIBLE);
                    tvOtpMessage.setText(e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    tvOtpMessage.setVisibility(View.VISIBLE);
                    tvOtpMessage.setText(e.getMessage());
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                bLogin.setVisibility(View.GONE);
                bOtpRetry.setVisibility(View.VISIBLE);
                tvOtpMessage.setVisibility(View.VISIBLE);
                tvOtpMessage.setText("OTP Timeout");
            }
        });

        bOtpRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOtp(phone);
                bOtpRetry.setVisibility(View.GONE);
                bLogin.setVisibility(View.VISIBLE);
                countDownTime();
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bLogin.getText().toString().equalsIgnoreCase("Save Details")) {
                    name = etName.getText().toString();
                    address = etAddress.getText().toString();

                    if (name.isEmpty()) {
                        etName.setError("Please enter name");
                    } else if (address.isEmpty()) {
                        etAddress.setError("Please enter address");
                    } else {
                        registerCompany();
                        HashMap<String, Object> dataMap = new HashMap<>();
                        dataMap.put("name", name);
                        dataMap.put("phone", phone_number);
                        dataMap.put("address", address);
                        mDatabase.child("users").child(user_id).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Details saved successfully", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void registerCompany() {
        final String key = mDatabase.child("business").push().getKey();
        final HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("owner_name", name);
        dataMap.put("company_phone_number", phone);
        dataMap.put("company_key", key);

        mDatabase.child("business").child(key).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDatabase.child("users").child(user_id).child("business").updateChildren(dataMap);
                } else {
                    String errMsg = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            final FirebaseUser user = task.getResult().getUser();
                            Variables.global_user_id = user.getUid();
                            Toast.makeText(getApplicationContext(), "Successfully Verified!", Toast.LENGTH_LONG).show();
                            mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("name").exists() && dataSnapshot.child("address").exists()) {
                                        onBackPressed();
                                    } else {
                                        tvLogin.setText("Add Details");
                                        etPhone.setVisibility(View.GONE);
                                        etOtp.setVisibility(View.GONE);
                                        etAddress.setVisibility(View.VISIBLE);
                                        etName.setVisibility(View.VISIBLE);
                                        bLogin.setText("Save Details");
                                        user_id = user.getUid();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void countDownTime() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvOtpMessage.setText("Time Remaining: " + millisUntilFinished / 1000 + " secs");
            }

            public void onFinish() {
                tvOtpMessage.setVisibility(View.GONE);
            }
        }.start();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Geocoder geocoder;
        googleMap.setMyLocationEnabled(true);
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        if (location != null) {
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (addresses != null) {
            address = addresses.get(0).getAddressLine(0);
            etAddress.setText(address);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
//            locationTv.setText("You need to install Google Play Services to use the App properly");
            Toast.makeText(getApplicationContext(), "You need to install Google Play Services to use the App properly", Toast.LENGTH_LONG).show();
        }

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this::onLocationChanged);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
//            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            Log.d("MainActivityLoc", "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    // Assign your origin and destination
//                    // These points are your markers coordinates
//                    LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
//                    LatLng dest = new LatLng(Double.parseDouble(Variables.latitude.trim()), Double.parseDouble(Variables.longitude.trim()));
//
//                    // Getting URL to the Google Directions API
//                    String url = getDirectionsUrl(origin, dest);
//
//                    DownloadTask downloadTask = new DownloadTask();
//
//                    // Start downloading json data from Google Directions API
//                    downloadTask.execute(url);
//                }
//            }, 1800);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
//            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            Log.d("MainActivityLoc", "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("ClockActivity", "dada" + connectionResult);
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this::onLocationChanged);
    }

    public void showNotification(String messageBody) {
        Context context = CardLoginActivity.this;
        String channel_id = createNotificationChannel(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel_id)
                .setContentTitle("Itech")
                .setContentText(messageBody)
                /*.setLargeIcon(largeIcon)*/
                .setSmallIcon(R.drawable.din_logo);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) ((new Date(System.currentTimeMillis()).getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());
    }

    public static String createNotificationChannel(Context context) {

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "Channel_id";

            // The user-visible name of the channel.
            CharSequence channelName = "Application_name";
            // The user-visible description of the channel.
            String channelDescription = "Application_name Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;
//            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
//            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }
}
