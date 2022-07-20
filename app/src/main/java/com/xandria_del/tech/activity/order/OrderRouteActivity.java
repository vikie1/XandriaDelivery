package com.xandria_del.tech.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.xandria_del.tech.R;
import com.xandria_del.tech.fragment.OrderLocationFragment;

public class OrderRouteActivity extends AppCompatActivity {
    public static final String ORDER_LOCATION = "Order Location";
    public static final String BOOK_ORDERED = "Book Ordered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_route);

        Location location = getIntent().getParcelableExtra(ORDER_LOCATION);
        String bookOrdered = getIntent().getExtras().getString(BOOK_ORDERED);
        if (location != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ORDER_LOCATION, location);
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