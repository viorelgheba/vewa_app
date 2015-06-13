package ro.emag.hackaton.vewa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ro.emag.hackaton.vewa.R;

public class WishlistAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> values;

    public WishlistAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.values = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.wish_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.list_item_text_view);

        String s = getItem(position);
        textView.setText(s);

        return rowView;
    }

}
