package com.xandria_del.tech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.xandria_del.tech.fragment.CompletedOrdersFragment;
import com.xandria_del.tech.fragment.PendingOrdersFragment;
import com.xandria_del.tech.fragment.PickedOrdersFragment;
import com.xandria_del.tech.fragment.UnpickedOrdersFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // set up the tabs
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 pager = (ViewPager2) findViewById(R.id.pager);
        pager.setAdapter(sectionPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, pager,
                ((tab, position) -> {
                    switch (position){
                        case 0:
                            tab.setText(getResources().getText(R.string.unpicked));
                            break;
                        case 1:
                            tab.setText(getResources().getText(R.string.picked));
                            break;
                        case 2:
                            tab.setText(getResources().getText(R.string.pending));
                            break;
                        case 3:
                            tab.setText(getResources().getText(R.string.completed));
                            break;
                    }
                })
        ).attach();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private static class SectionPagerAdapter extends FragmentStateAdapter {

        public SectionPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 1:
                    return new PickedOrdersFragment();
                case 2:
                    return new PendingOrdersFragment();
                case 3:
                    return new CompletedOrdersFragment();
                default:
                    return new UnpickedOrdersFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}