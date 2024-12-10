package com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cq_mobile.HelperManagers.BackPressManager;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.CustomBottomNavView;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForNewBuild;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers.SetupMainTaskManager;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.RetrieveDataFromAPIMangers.SetupRecyclerViewManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewBuild extends AppCompatActivity implements OnMapReadyCallback, SetupMainTaskManager.OnCoordinatesReceivedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private BackPressManager backPressManager;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private NavigationManagerForNewBuild navigationManager;
    TextView showBottomSheet;
    private SetupMainTaskManager setupMainTaskManager;
    private SetupRecyclerViewManager setupRecyclerViewManager;
    private Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newbuild);

        backPressManager = new BackPressManager(this);
        showBottomSheet = findViewById(R.id.showBottomSheet);

        String jobId = getIntent().getStringExtra("job_id");
        if (jobId != null) {
            Log.d("job ID ->", "Received Job ID: " + jobId);
        } else {
            Log.e("job ID ->", "No Job ID received!");
        }

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);  // Will call onMapReady when ready
        }

        View bottomSheet = findViewById(R.id.new_built_bottom_sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.post(() -> bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight() / 3));
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        setupRecyclerViewManager = new SetupRecyclerViewManager(this, findViewById(R.id.recycler_view));
        setupRecyclerViewManager.setupRecyclerView(jobId);

        CustomBottomNavView bottomNavView = findViewById(R.id.custom_bottom_nav_view);
        navigationManager = new NavigationManagerForNewBuild(this, bottomSheet, bottomSheetBehavior);
        navigationManager.setUpNavigation(bottomNavView);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressManager.handleBackPress(MainActivity.class);
                finish();
            }
        });

        showBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheet.post(() -> bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight() / 3));
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Initialize the SetupMainTaskManager only after googleMap is ready
        setupMainTaskManager = new SetupMainTaskManager(this, googleMap, findViewById(R.id.task_title),
                findViewById(R.id.task_description), findViewById(R.id.task_location), findViewById(R.id.task_number),
                findViewById(R.id.spinner_task), this);  // Pass listener for coordinates

        String jobId = getIntent().getStringExtra("job_id");
        if (jobId != null) {
            setupMainTaskManager.setupMainTask(jobId);
        }

        // Check location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission not granted to access location", Toast.LENGTH_SHORT).show();
            return;
        }

        googleMap.setMyLocationEnabled(true);

        // Get the user's current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                googleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                Log.d("UserLocation",userLocation.latitude+" "+userLocation.longitude);
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(this, "Location permission is required to display your position", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCoordinatesReceived(double latitude, double longitude) {
        // Create a LatLng object for the task location
        LatLng taskLatLng = new LatLng(latitude, longitude);
        BitmapDescriptor customMarkerIcon = getCustomCircleMarkerIcon(NewBuild.this);

        marker = googleMap.addMarker(new MarkerOptions()
                .position(taskLatLng)
                .icon(customMarkerIcon)
                .anchor(0.6f, 0.6f)
                .zIndex(5.0f));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taskLatLng, 15));
    }



    public BitmapDescriptor getCustomCircleMarkerIcon(Context context) {
        int diameter = 200;
        int padding = 25;
        Bitmap bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        float radius = diameter / 2f;
        // Draw the filled circle
        canvas.drawCircle(radius, radius, radius, paint);

        // Load the drawable (replace with your image resource)
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.map_marker_todo);

        if (drawable != null) {
            // Convert the drawable to a bitmap
            Bitmap drawableBitmap = drawableToBitmap(drawable);

            // Scale the drawable bitmap to fit the circle with padding
            int scaledWidth = diameter - 2 * padding;
            int scaledHeight = diameter - 2 * padding;
            Bitmap scaledImage = Bitmap.createScaledBitmap(drawableBitmap, scaledWidth, scaledHeight, false);

            // Calculate the starting position to center the image within the circle
            float left = padding;
            float top = padding;

            // Draw the image onto the canvas with padding
            canvas.drawBitmap(scaledImage, left, top, null);

            // Draw the border around the circle
            Paint borderPaint = new Paint();
            borderPaint.setColor(Color.WHITE); // Border color
            borderPaint.setStrokeWidth(5); // Border thickness
            borderPaint.setStyle(Paint.Style.STROKE); // Only draw the border
            canvas.drawCircle(radius, radius, radius - 5, borderPaint);
        }

        // Create the BitmapDescriptor from the bitmap
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    public void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
