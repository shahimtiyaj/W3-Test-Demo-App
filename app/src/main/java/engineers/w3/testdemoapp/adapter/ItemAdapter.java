package engineers.w3.testdemoapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import engineers.w3.testdemoapp.R;
import engineers.w3.testdemoapp.app.AppController;
import engineers.w3.testdemoapp.model.Item;
/*
 Create the basic adapter extending from RecyclerView.Adapter
  Note that we specify the custom ViewHolder which gives us access to our views
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context mContext;
    //Store a member variable for the item
    private List<Item> itemList;

    /*
     Provide a direct reference to each of the views within a data item
     Used to cache the views within the item layout for fast access
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        /* holder should contain a member variable
         for any view that will be set as you render a row
        */
        public TextView title;
        public ImageView image;

        /* We also create a constructor that accepts the entire item row
         and does the view lookups to find each subview
         */
        public ItemViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }

    // Pass in the item array and context into the constructor
    public ItemAdapter(Context mContext, List<Item> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        // Return a new holder instance
        return new ItemViewHolder(itemView);
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        // Get the item model based on position
        Item item = itemList.get(position);
        // Set item views based on our views and data model
        holder.title.setText(item.getItem());

        Glide.with(AppController.getInstance())
                .load(item.getImage())
                .into(holder.image);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
