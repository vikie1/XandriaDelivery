package com.xandria_del.tech.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xandria_del.tech.R;
import com.xandria_del.tech.adapter.OrdersListViewAdapter;
import com.xandria_del.tech.constants.FirebaseRefs;
import com.xandria_del.tech.model.OrdersModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompletedOrdersFragment extends Fragment {
    private List<OrdersModel> ordersList;
    private OrdersListViewAdapter listViewAdapter;
    private DatabaseReference firebaseDatabaseReference;
    private ListView ordersListView;
    private String user;

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_completed_orders, container, false);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.ORDERS);
        user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        // set up the the listview
        ordersList = new ArrayList<>();
        listViewAdapter = new OrdersListViewAdapter(context, new ArrayList<>());
        ordersListView = mainView.findViewById(R.id.orders_list);
        ordersListView.setAdapter(listViewAdapter);

        // action performed when a list item is clicked
        ordersListView.setOnItemClickListener((parent, view, position, id) -> {
            OrdersModel order = listViewAdapter.getItem(position);
            if (ordersList.contains(order))
                ordersList.remove(order);
            else ordersList.add(listViewAdapter.getItem(position));
        });

        getOrdersFromDb();
        return mainView;
    }

    private void getOrdersFromDb(){
        listViewAdapter.clear();

        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                OrdersModel ordersModel;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ordersModel = dataSnapshot.getValue(OrdersModel.class);
                    if (ordersModel != null &&
                            ordersModel.isBookedForDelivery() &&
                            user.equals(ordersModel.getUserId())) {
                        if (ordersModel.isBorrowConfirmed())
                            listViewAdapter.add(ordersModel);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                OrdersModel ordersModel;
                OrdersListViewAdapter newListViewAdapter = new OrdersListViewAdapter(context, new ArrayList<>());
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ordersModel = dataSnapshot.getValue(OrdersModel.class);
                    if (ordersModel != null &&
                            ordersModel.isBookedForDelivery()&&
                            user.equals(ordersModel.getUserId())) {
                        if (ordersModel.isBorrowConfirmed()) {
                            newListViewAdapter.add(ordersModel);
                            ordersListView.setAdapter(newListViewAdapter);
                        } else ordersListView.setAdapter(listViewAdapter);
                    }
                }
                CompletedOrdersFragment.this.listViewAdapter = newListViewAdapter;
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                OrdersModel ordersModel;
                OrdersListViewAdapter newListViewAdapter = new OrdersListViewAdapter(context, new ArrayList<>());
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ordersModel = dataSnapshot.getValue(OrdersModel.class);
                    if (ordersModel != null &&
                            ordersModel.isBookedForDelivery() &&
                            user.equals(ordersModel.getUserId())) {
                        if (ordersModel.isBorrowConfirmed()) {
                            newListViewAdapter.add(ordersModel);
                            ordersListView.setAdapter(newListViewAdapter);
                        } else ordersListView.setAdapter(listViewAdapter);
                    }
                }
                CompletedOrdersFragment.this.listViewAdapter = newListViewAdapter;
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                OrdersModel ordersModel;
                OrdersListViewAdapter newListViewAdapter = new OrdersListViewAdapter(context, new ArrayList<>());
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ordersModel = dataSnapshot.getValue(OrdersModel.class);
                    if (ordersModel != null &&
                            ordersModel.isBookedForDelivery() &&
                            user.equals(ordersModel.getUserId())) {
                        if (ordersModel.isBorrowConfirmed()) {
                            newListViewAdapter.add(ordersModel);
                            ordersListView.setAdapter(newListViewAdapter);
                        } else ordersListView.setAdapter(listViewAdapter);
                    }
                }
                CompletedOrdersFragment.this.listViewAdapter = newListViewAdapter;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}