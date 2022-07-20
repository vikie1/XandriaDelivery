package com.xandria_del.tech.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class UnpickedOrdersFragment extends Fragment {
    private List<OrdersModel> ordersList;
    private OrdersListViewAdapter listViewAdapter;
    private DatabaseReference firebaseDatabaseReference;

    private View view;
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
        view = inflater.inflate(R.layout.fragment_unpicked_orders, container, false);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.ORDERS);

        // set up the the listview
        ordersList = new ArrayList<>();
        listViewAdapter = new OrdersListViewAdapter(context, new ArrayList<>());
        ListView ordersListView = view.findViewById(R.id.orders_list);
        ordersListView.setAdapter(listViewAdapter);

        // action performed when a list item is clicked
        ordersListView.setOnItemClickListener((parent, view, position, id) -> {
            OrdersModel order = listViewAdapter.getItem(position);
            if (ordersList.contains(order))
                ordersList.remove(order);
            else ordersList.add(listViewAdapter.getItem(position));
        });

        getOrdersFromDb();
        return view;
    }

    void getOrdersFromDb(){
        listViewAdapter.clear();

        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                OrdersModel ordersModel;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ordersModel = dataSnapshot.getValue(OrdersModel.class);
                    if (ordersModel != null && !ordersModel.isBookedForDelivery()
                        && ordersModel.getDropLocation() != null && ordersModel.getHostAddress() != null
                        && !ordersModel.getDropLocation().equals(ordersModel.getHostAddress())) { // if drop location is equal to host location then the order was meant for pick up not delivery

                        if (!ordersModel.isBorrowConfirmed())
                            listViewAdapter.add(ordersModel);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                OrdersModel ordersModel;
                listViewAdapter.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        ordersModel = dataSnapshot.getValue(OrdersModel.class);
                        if (ordersModel != null && !ordersModel.isBookedForDelivery()
                                && ordersModel.getDropLocation() != null && ordersModel.getHostAddress() != null
                                && !ordersModel.getDropLocation().equals(ordersModel.getHostAddress())) { // if drop location is equal to host location then the order was meant for pick up not delivery

                            if (!ordersModel.isBorrowConfirmed())
                                listViewAdapter.add(ordersModel);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                OrdersModel ordersModel;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        ordersModel = dataSnapshot.getValue(OrdersModel.class);
                        if (ordersModel != null && !ordersModel.isBookedForDelivery()
                                && ordersModel.getDropLocation() != null && ordersModel.getHostAddress() != null
                                && !ordersModel.getDropLocation().equals(ordersModel.getHostAddress())) { // if drop location is equal to host location then the order was meant for pick up not delivery

                            if (!ordersModel.isBorrowConfirmed())
                                listViewAdapter.add(ordersModel);
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                OrdersModel ordersModel;
                listViewAdapter.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        ordersModel = dataSnapshot.getValue(OrdersModel.class);
                        if (ordersModel != null && !ordersModel.isBookedForDelivery()
                                && ordersModel.getDropLocation() != null && ordersModel.getHostAddress() != null
                                && !ordersModel.getDropLocation().equals(ordersModel.getHostAddress())) { // if drop location is equal to host location then the order was meant for pick up not delivery

                            if (!ordersModel.isBorrowConfirmed())
                                listViewAdapter.add(ordersModel);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}