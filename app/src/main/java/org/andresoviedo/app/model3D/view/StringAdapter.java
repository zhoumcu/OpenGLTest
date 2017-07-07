package org.andresoviedo.app.model3D.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.andresoviedo.dddmodel2.R;

import java.util.ArrayList;

/**
 * author：Administrator on 2017/7/4 15:28
 * company: xxxx
 * email：1032324589@qq.com
 */

public class StringAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<RowItem> rowItems;

    public StringAdapter(Context context, ArrayList<RowItem> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public RowItem getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_demo_item, null);
            holder = new ViewHolder();
            // holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.demo_item_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.demo_item_icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        // holder.txtDesc.setText(rowItem.getDesc());
        holder.txtTitle.setText(rowItem.name);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(rowItem.image));
            holder.imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.ic_launcher2);
        }

        return convertView;
    }


    /* private view holder class */
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
    }
}
