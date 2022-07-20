package com.xandria_del.tech.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import com.squareup.picasso.Picasso;
import com.xandria_del.tech.R;
import com.xandria_del.tech.activity.order.OrderRouteActivity;
import com.xandria_del.tech.model.OrdersModel;
import com.xandria_del.tech.util.DateUtils;

import java.time.LocalDateTime;
import java.util.List;

public class OrdersListViewAdapter extends ArrayAdapter<OrdersModel> {

    public OrdersListViewAdapter(@NonNull Context context, @NonNull List<OrdersModel> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        OrdersModel order = getItem(position);// get the order for this view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_with_image, parent, false);
        TextView bookTitle = convertView.findViewById(R.id.order_book_id);
        TextView orderDate = convertView.findViewById(R.id.order_date);
        TextView bookHost = convertView.findViewById(R.id.book_host);
        TextView deliveryContact = convertView.findViewById(R.id.delivery_contact);
        ImageView thumbnail = convertView.findViewById(R.id.book_image_in_list);
        Button showInMap = convertView.findViewById(R.id.show_in_map);

        bookTitle.setText(HtmlCompat.fromHtml(
                getContext().getString(R.string.book_title).concat(" ").concat(order.getBookTitle()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        bookHost.setText(HtmlCompat.fromHtml(
                getContext().getString(R.string.book_host).concat(" ").concat(order.getHostLocationUserId()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        deliveryContact.setText(HtmlCompat.fromHtml(
                getContext().getString(R.string.drop_contact).concat(" ").concat(order.getDeliveryContact()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));

        showInMap.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), OrderRouteActivity.class);
            intent.putExtra(OrderRouteActivity.HOST_LOCATION, order.getHostAddress());
            intent.putExtra(OrderRouteActivity.DROP_LOCATION, order.getDropLocation());
            intent.putExtra(OrderRouteActivity.BOOK_ORDERED, order.getBookTitle());
            getContext().startActivity(intent);
        });

        String dateAndTime = "";

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(order.getDateOrdered());
            dateAndTime = DateUtils.getDateTimeString(localDateTime);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (dateAndTime.trim().equals(""))
            orderDate.setText(HtmlCompat.fromHtml(
                    getContext().getString(R.string.date_ordered).concat(" ").concat(order.getDateOrdered()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        else
            orderDate.setText(HtmlCompat.fromHtml(
                    getContext().getString(R.string.date_ordered).concat(" ").concat(dateAndTime),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        Picasso.get().load(order.getBookImageUrl()).into(thumbnail);

        return convertView;
    }
}
