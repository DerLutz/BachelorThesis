package com.example.chris.myapplication;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class CategoryRecView extends RecyclerView.Adapter<CategoryRecView.NumberViewHolder> {
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC, orientation;
    BufferedReader bufferedReader ;
    //StringBuilder stringBuilder;
    private String server_url = "http://192.168.178.44:1337/getData";
    private static final String TAG = CategoryRecView.class.getSimpleName();

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    final private ListItemClickListener mOnClickListener;
    StringBuilder response;


    private static int viewHolderCount;

    private int mNumberItems;

    // COMPLETED (1) Add an interface called ListItemClickListener
    // COMPLETED (2) Within that interface, define a void method called onListItemClick that takes an int as a parameter
    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    // COMPLETED (4) Add a ListItemClickListener as a parameter to the constructor and store it in mOnClickListener
    /**
     * Constructor for GreenAdapter that accepts a number of items to display and the specification
     * for the ListItemClickListener.
     *
     * @param numberOfItems Number of items to display in list
     * @param listener Listener for list item clicks
     */
    public CategoryRecView(int numberOfItems, ListItemClickListener listener, StringBuilder stringBuilder) {
        mNumberItems = numberOfItems;
        mOnClickListener = listener;
        response = stringBuilder;
        viewHolderCount = 0;
    }

    /**
     *
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link androidx.recyclerview.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new NumberViewHolder that holds the View for each list item
     */
    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.number_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        //StringBuilder response = connectToServer("Category", 12);


        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        NumberViewHolder viewHolder = new NumberViewHolder(view);


        //Todo add values
        viewHolder.viewHolderIndex.setText("Rechnung: " + Categories.getCategoryTotal(response, viewHolderCount));

        //Todo Background Color
        //int backgroundColorForViewHolder = ColorUtils.getViewHolderBackgroundColorFromInstance(context, viewHolderCount);
        //viewHolder.itemView.setBackgroundColor(backgroundColorForViewHolder);

        viewHolderCount++;
        Log.d(TAG, "onCreateViewHolder: number of ViewHolders created: "
                + viewHolderCount);
        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    // COMPLETED (5) Implement OnClickListener in the NumberViewHolder class
    /**
     * Cache of the children views for a list item.
     */
    class NumberViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView listItemNumberView;
        // Will display which ViewHolder is displaying this data
        TextView viewHolderIndex;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link CategoryRecView#onCreateViewHolder(ViewGroup, int)}
         */
        public NumberViewHolder(View itemView) {
            super(itemView);

            listItemNumberView = (TextView) itemView.findViewById(R.id.tv_item_number);
            viewHolderIndex = (TextView) itemView.findViewById(R.id.tv_view_holder_instance);
            // COMPLETED (7) Call setOnClickListener on the View passed into the constructor (use 'this' as the OnClickListener)
            itemView.setOnClickListener(this);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        //TODO add values (Receipt1 etc.)
        void bind(int listIndex) {
            String name = Categories.getCategoryName(response, listIndex);
            listItemNumberView.setText(name);
        }


        // COMPLETED (6) Override onClick, passing the clicked item's position (getAdapterPosition()) to mOnClickListener via its onListItemClick method
        /**
         * Called whenever a user clicks on an item in the list.
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }


}
