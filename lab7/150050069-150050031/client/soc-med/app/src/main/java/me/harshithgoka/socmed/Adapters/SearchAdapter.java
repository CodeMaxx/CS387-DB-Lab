package me.harshithgoka.socmed.Adapters;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.harshithgoka.socmed.Misc.Constants;
import me.harshithgoka.socmed.Misc.Utils;
import me.harshithgoka.socmed.Network.NetworkService;
import me.harshithgoka.socmed.R;
import me.harshithgoka.socmed.Storage.User;

/**
 * Created by akashtrehan on 07/10/17.
 */

public class SearchAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = SearchAdapter.class.getName();
    private Context mContext;
    List<User> users = new ArrayList<>();

    public SearchAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ResultOnClickListener implements View.OnClickListener {

        User user;
        ResultOnClickListener (User user) {
            this.user = user;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, NetworkService.class);
            intent.putExtra(Constants.WHAT, Constants.GET_USER_POSTS);

            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.USER_DATA, user);

            intent.putExtra(Constants.INTENT_DATA, bundle);

            mContext.startService(intent);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.user_card, viewGroup, false);
        }

        view.setOnClickListener(new ResultOnClickListener(getItem(i)));

        ((TextView) view.findViewById(R.id.user_name)).setText(getItem(i).name);
        ((TextView) view.findViewById(R.id.user_email)).setText(getItem(i).email);
        ((TextView) view.findViewById(R.id.user_uid)).setText( "(@" + getItem(i).uid + ")");

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<User> userList = new ArrayList<>();

                if (charSequence != null && charSequence.toString().trim().length() >= 3) {
                    try {
                        URL url = new URL(Constants.URL + "SearchUser");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        try {
                            connection.setRequestMethod("POST");
                            connection.setDoOutput(true);
                            connection.setDoInput(true);


                            List<AbstractMap.SimpleEntry> list = new ArrayList<AbstractMap.SimpleEntry>();
                            list.add(new AbstractMap.SimpleEntry("uid", charSequence));

                            OutputStream os = connection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(Utils.getQuery(list));
                            writer.flush();
                            writer.close();
                            os.close();

                            int status = connection.getResponseCode();
                            Log.d(TAG, "" + status);

                            connection.getHeaderFields();

                            if (!url.getHost().equals(connection.getURL().getHost())) {
                                // we were redirected! Kick the user out to the browser to sign on?
                                throw new Exception("Login to your internet provider");
                            }


                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<User>>() {}.getType();
                            JsonObject response = Utils.getAndParse(connection.getInputStream());
                            Log.d(TAG, response.toString());

                            if (response.get("status").getAsBoolean()) {
                                userList = gson.fromJson(response.getAsJsonArray("data").get(0), listType);
                                Log.d(TAG, userList.toString());
                            }

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        } finally {
                            connection.disconnect();
                        }
                    } catch (MalformedURLException e) {
                        Log.d(TAG, e.toString());
                    } catch (IOException e) {
                        Log.d(TAG, e.toString());
                    }
                }

                FilterResults filterResults = new FilterResults();

                filterResults.count = userList.size();
                filterResults.values = userList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    users = (List<User>) filterResults.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}
