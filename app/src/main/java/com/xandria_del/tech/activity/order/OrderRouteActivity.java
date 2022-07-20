package com.xandria_del.tech.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.xandria_del.tech.R;
import com.xandria_del.tech.dto.Location;
import com.xandria_del.tech.fragment.OrderLocationFragment;

public class OrderRouteActivity extends AppCompatActivity {
    public static final String HOST_LOCATION = "Host Location";
    public static final String DROP_LOCATION = "Drop Location";
    public static final String BOOK_ORDERED = "Book Ordered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_route);

        Location hostLocation = getIntent().getParcelableExtra(HOST_LOCATION);
        Location dropLocation = getIntent().getParcelableExtra(DROP_LOCATION);
        String bookOrdered = getIntent().getExtras().getString(BOOK_ORDERED);
        if (hostLocation != null && dropLocation != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(HOST_LOCATION, hostLocation);
            bundle.putParcelable(DROP_LOCATION, dropLocation);
            bundle.putString(BOOK_ORDERED, bookOrdered);
            Fragment fragment = new OrderLocationFragment();
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        } else {
            Toast.makeText(this, "Could not parse location", Toast.LENGTH_LONG).show();
            onBackPressed();
            finish();
        }
    }
}