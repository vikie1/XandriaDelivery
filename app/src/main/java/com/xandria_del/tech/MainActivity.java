package com.xandria_del.tech;

import static com.xandria_del.tech.util.LocationUtils.REQUEST_CHECK_SETTINGS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.xandria_del.tech.activity.user.LoginActivity;
import com.xandria_del.tech.constants.LoggedInUser;
import com.xandria_del.tech.fragment.CompletedOrdersFragment;
import com.xandria_del.tech.fragment.PendingOrdersFragment;
import com.xandria_del.tech.fragment.PickedOrdersFragment;
import com.xandria_del.tech.fragment.ProfileFragment;
import com.xandria_del.tech.fragment.UnpickedOrdersFragment;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup the custom tool bar as the app bar so that we can add our navigation menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(null);
        setUpNavigationDrawer(toolbar);

        // Set MainFragment as the default fragment
        Fragment mainFragment = new UnpickedOrdersFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame, mainFragment);
        fragmentTransaction.commit();

        checkLocationPermissions();
    }

    private void setUpNavigationDrawer(Toolbar toolbar){
        //setting up the navigation drawer
        DrawerLayout drawerLayout = findViewById(R.id.main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer
        ){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                // setup user name and points when drawer is opened
                TextView nameView = headerView.findViewById(R.id.nav_display_name);
                TextView pointsView = headerView.findViewById(R.id.nav_display_points);

                if (LoggedInUser.getInstance().getCurrentUser() != null){
                    nameView.setText(HtmlCompat.fromHtml(
                            getString(R.string.name).concat(" ").concat(LoggedInUser.getInstance().getCurrentUser().getName()),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                    ));
                    pointsView.setText(HtmlCompat.fromHtml(
                            getString(R.string.points).concat(" ").concat(
                                    String.valueOf(LoggedInUser.getInstance().getCurrentUser().getPoints())
                            ),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                    ));
                } else {
                    nameView.setVisibility(View.GONE);
                    pointsView.setVisibility(View.GONE);
                }
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //make the drawer respond to clicks
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkLocationPermissions() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // location is disabled
        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.

                ResolvableApiException resolvable = (ResolvableApiException) e;
                try {
                    resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;

        switch (id){
            case R.id.nav_pending:
                fragment = new PendingOrdersFragment();
                break;
            case R.id.nav_completed:
                fragment = new CompletedOrdersFragment();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, LoginActivity.class);
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            default:
                fragment = new UnpickedOrdersFragment();
                break;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();
        }
        else {
            startActivity(intent);
            finish();
        }
        DrawerLayout drawerLayout = findViewById(R.id.main);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.main);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START); // if drawer was open close it
        else if (getFragmentManager().getBackStackEntryCount() != 0) getFragmentManager().popBackStack(); // return to previous fragment
        else super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_CANCELED) {// The user was asked to change settings, but chose not to
                Toast.makeText(this, "Your location will not be determined", Toast.LENGTH_SHORT).show();
            }
        }
    }
}