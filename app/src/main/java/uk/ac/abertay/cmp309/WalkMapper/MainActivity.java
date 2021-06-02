package uk.ac.abertay.cmp309.WalkMapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private int currentNavItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // FRAGMENT CODE
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(), "visible_fragment").commit();
            navigationView.setCheckedItem(R.id.nav_home);
            currentNavItem = R.id.nav_home;
        }

        createNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView.getCheckedItem().getItemId() != currentNavItem) {
            navigationView.setCheckedItem(currentNavItem);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(), "visible_fragment").commit();
                currentNavItem = R.id.nav_home;
                break;
            case R.id.nav_walks:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WalksFragment(), "visible_fragment").commit();
                currentNavItem = R.id.nav_walks;
                break;
            case R.id.nav_map:
                startMapActivity();
                break;
            case R.id.nav_stats:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StatsFragment(), "visible_fragment").commit();
                currentNavItem = R.id.nav_stats;
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void startMapActivity() {
        startActivity(new Intent(MainActivity.this, MapsActivity.class));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        NotificationChannel channel = new NotificationChannel("walkNotification", "WalkReminderChannel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel for Walk Reminders");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}