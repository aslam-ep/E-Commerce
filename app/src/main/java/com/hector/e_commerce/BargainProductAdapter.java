package com.hector.e_commerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class BargainProductAdapter extends ArrayAdapter<BargainProduct> {
    ArrayList<BargainProduct> dataSet;
    Context context;
    private int lastPosition;

    public BargainProductAdapter(ArrayList<BargainProduct> dataSet, Context context) {
        super(context, R.layout.bargain_list_item, dataSet);
        this.dataSet = dataSet;
        this.context = context;
        this.lastPosition = -1;
    }

    private class ViewHolder{
        TextView productName, quantity, req_price, cur_price;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BargainProduct bargainProduct = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if( convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.bargain_list_item, parent, false);

            viewHolder.productName = convertView.findViewById(R.id.bargainProductName);
            viewHolder.quantity = convertView.findViewById(R.id.bargainProductQuantity);
            viewHolder.req_price = convertView.findViewById(R.id.bargainRequestedPrice);
            viewHolder.cur_price = convertView.findViewById(R.id.bargainCurrentPrice);

            result = convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.productName.setText(bargainProduct.getProductName());
        viewHolder.quantity.setText("Need : "+bargainProduct.getRequestedQuantity());
        viewHolder.cur_price.setText("Cur Price : "+bargainProduct.getCurrentPrice());
        viewHolder.req_price.setText("Req Price : "+bargainProduct.getRequestedPrice());

        return convertView;
    }
}
