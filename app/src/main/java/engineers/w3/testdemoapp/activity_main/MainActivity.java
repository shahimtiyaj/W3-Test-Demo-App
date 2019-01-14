package engineers.w3.testdemoapp.activity_main;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import engineers.w3.testdemoapp.database.DAO;
import engineers.w3.testdemoapp.network_service.VolleyCustomRequest;
import engineers.w3.testdemoapp.R;
import engineers.w3.testdemoapp.adapter.ItemAdapter;
import engineers.w3.testdemoapp.app.AppController;
import engineers.w3.testdemoapp.model.Item;
import engineers.w3.testdemoapp.network_service.CloudRequest;
import engineers.w3.testdemoapp.utils.EndlessRecyclerViewScrollListener;
import engineers.w3.testdemoapp.utils.GridSpacingItemDecoration;
import engineers.w3.testdemoapp.utils.Utils;

import static com.android.volley.VolleyLog.d;
import static engineers.w3.testdemoapp.database.DBHelper.TABLE_ITEM;

/**
 * Created by Md. Imtiyaj on 1/14/2019.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ArrayList<Item> itemsArrayList;
    private SwipeRefreshLayout swipeRefresh;
    private Boolean isScrolling = false;
    private GridLayoutManager mLayoutManager;
    private ProgressBar progress;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int page = 0, limit = 5;
    private String jObjPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolBarInit();
        recyclerViewInit();
        progressBarInit();
        loadData();
    }

    /**
     * Called after on create method
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity_", "onStart");
    }

    /**
     * Called after on start method
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (!Utils.isNetworkAvailable(AppController.getInstance())) {
            Utils.longToast(AppController.getInstance(), "Network not available !");
        }
    }

    /**
     * Is network available Load data from web service only once
     * If Fully data loaded from web service second time load local data
     */
    public void loadData() {
        DAO dao = new DAO(AppController.getInstance());
        dao.open();
        if (dao.getRowCount() > 0) {
            loadItemList();
        } else {
            if (Utils.isNetworkAvailable(AppController.getInstance())) {
                GetDataFromWebService(page);
                onScrollBarInit();
            } else {
                Utils.longToast(AppController.getInstance(), "Network not available !");
                progress.setVisibility(View.GONE);
            }
        }
        dao.close();
    }

    /**
     * SetUp toolbar method
     */
    public void toolBarInit() {
        // Lookup the toolbar in activity layout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Lookup the toolbar title  in activity
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        // Lookup the toolbar in activity layout
        setSupportActionBar(toolbar);
        //Default home button enable false
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /*
    recycler view initialization method
     */
    public void recyclerViewInit() {
        // Initialize item list
        itemsArrayList = new ArrayList<Item>();
        // Lookup the recyclerview in activity layout
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // Create adapter passing in the sample item data
        adapter = new ItemAdapter(getApplicationContext(), itemsArrayList);
        //GridLayoutManager shows items in a grid.
        mLayoutManager = new GridLayoutManager(this, 2);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(mLayoutManager);
        //Set grid item decoration
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        // Set the default animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
    }

    public void onScrollBarInit() {
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                //Each page return 5 items from web service  when scroll.
                GetDataFromWebService(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
    }

    /**
     * Progress dialogue initialization
     */
    private void progressBarInit() {
        progress = (ProgressBar) findViewById(R.id.progress);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * request full screen window method
     */
    private void requestFullScreenWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Get all item list from Local database
     */
    private void loadItemList() {
        //Create database  object
        DAO dao = new DAO(AppController.getInstance());
        //Open database
        dao.open();
        itemsArrayList = dao.gettingAllItem();
        adapter = new ItemAdapter(getApplicationContext(), itemsArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        progress.setVisibility(View.GONE);
        dao.close();
    }

    /**
     * Get data from webservice
     *
     * @param page int page .Each page load 5 item
     */
    public void GetDataFromWebService(int page) {
        progressBarInit();
        final String hitURL = AppController.getItemUrl();
        //Json object for posting to server and getting object array
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("limit", limit);
            jsonObj.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //here convert json object to string for posting data to server
        jObjPost = String.valueOf(jsonObj);
        //showing progresss bar when loading data to server
        progress.setVisibility(View.VISIBLE);
        //JSON Post Request --------------------------------
        VolleyCustomRequest postRequest = new VolleyCustomRequest(Request.Method.POST, hitURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        d(TAG, "Server Response:" + response.toString());
                        try {
                            //Getting  json object node
                            JSONObject jsonObj = response.getJSONObject("result");
                            // Getting JSON Array node
                            JSONArray getData = jsonObj.getJSONArray("data");
                            // looping through All nodes
                            for (int i = 0; i < getData.length(); i++) {
                                //continue to loop it getting null value
                                if (getData.isNull(i))
                                    continue;
                                // Getting json object node
                                JSONObject c = getData.getJSONObject(i);
                                // Get the item model
                                Item itemlist = new Item();
                                //set the json data in the model
                                itemlist.setItem(c.getString("title"));
                                itemlist.setImage(c.getString("img"));
                                // adding item to ITEM list
                                itemsArrayList.add(itemlist);
                                // Insert json data into local database for  showing data into offline
                                // Replace  if same value not inserted two time
                                DAO.executeSQL("INSERT OR REPLACE INTO " + TABLE_ITEM + "(title, image) " +
                                                "VALUES(?, ?)",
                                        new String[]{c.getString("title"),
                                                c.getString("img")
                                        });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Load all data and hide the progressbar if catch any json exception
                            progress.setVisibility(View.GONE);
                        }
                        //Notify adapter if data change
                        adapter.notifyDataSetChanged();
                        //Load all data and hide the progressbar
                        progress.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        d(TAG, "Error: " + volleyError.getMessage());
                        String message = null;
                        if (volleyError instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (volleyError instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (volleyError instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (volleyError instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (volleyError instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        } else {
                            message = volleyError.toString();
                        }
                        progress.setVisibility(View.GONE);
                    }
                }) {
            /**
             * Passing some request in body. Basically here we have passed json object request
             */
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jObjPost == null ? null : jObjPost.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jObjPost, "utf-8");
                    return null;
                }
            }

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String AppId = "tXZ7d77djcCiBSd5F5BLzyJLNiPqNgMRd3Blc5dP";
                String ApiKey = "VxcGZP0f0MrRkXPefpPIhpAHjVNPChhrhuWif0uX";
                //pass application id and rest api key for security
                headers.put("Content-Type", "application/json");
                headers.put("X-Parse-Application-Id", AppId);
                headers.put("X-Parse-REST-API-Key", ApiKey);
                return headers;
            }

        };
        // Volley socket time its use for loading huge amount data
        int socketTimeout = 1800000;//18 Minutes-change to what you want//1800000 milliseconds = 18 minutes
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        //Volley cache true
        postRequest.setShouldCache(true);
        //// Add the request to the RequestQueue. from singleton class
        CloudRequest.getInstance(this).addToRequestQueue(postRequest);
    }
}
