package com.example.lovidence.fragments.communityfrags;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lovidence.R;
import com.example.lovidence.SQLite.CommunityDatabase;
import com.example.lovidence.SQLite.Community_Scrap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class community_scrap extends Fragment {
    ViewGroup viewGroup;
    ArrayList<SampleData> datalist;
    boolean lastitemVisibleFlag = false;
    //private static long lastTime;
    private static Context context;
    //private static boolean first;
    private static CommunityAdapter myAdapter;
    private static ListView listView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datalist = new ArrayList<SampleData>();
        myAdapter = new CommunityAdapter(getActivity(),datalist);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_community_private, container, false);
        context = getActivity();
        read();
        listView = (ListView)viewGroup.findViewById(R.id.private_community);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                contentAsyncTask loadContent = new contentAsyncTask(myAdapter, getActivity());
                loadContent.execute(position);
                listView.setEnabled(false);
            }
        });

        return viewGroup;
    }
    @Override
    public void onResume() {
        super.onResume();
        viewGroup.setFocusableInTouchMode(true);
        viewGroup.requestFocus();
        viewGroup.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Log.e("??", Integer.toString(fm.getBackStackEntryCount()));
                    if(fm.getBackStackEntryCount() > 1){
                        fm.popBackStack();
                        listView.setEnabled(true);
                        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                    }
                    else{
                        getActivity().onBackPressed();
                    }
                    // handle back button
                    return true;
                }
                return false;
            }
        });
    }

    private void read() {
        //get stored page by thread
        Thread_DB db_thread = new Thread_DB();
        db_thread.start();
        List<Community_Scrap> page_list = new ArrayList<Community_Scrap>();
        try {
            db_thread.join();
            page_list = db_thread.get_location_data();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(Community_Scrap c : page_list){
            Bitmap bitmap = BitmapFactory.decodeByteArray(c.getImg(),0,c.getImg().length);
            SampleData scraps = new SampleData(bitmap,c.getStr(),c.getTime());
            myAdapter.add(scraps);
        }

        //lasttiem be last element time.
    }

    //item click...
    private static class contentAsyncTask extends AsyncTask<Integer, Void, Integer> {
        FragmentActivity FA;
        CommunityAdapter myAdapter;
        public contentAsyncTask(CommunityAdapter adapter,FragmentActivity FA) {
            this.myAdapter = adapter;
            this.FA = FA;
        }

        @Override //백그라운드작업(메인스레드 X)
        protected Integer doInBackground(Integer... args) {
            Fragment fragment = new community_content();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            myAdapter.getItem(args[0]).getImg().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Bundle b = new Bundle();
            b.putByteArray("image",byteArray);
            b.putString("text",myAdapter.getItem(args[0]).getContent());
            b.putInt("flag",2);
            fragment.setArguments(b);
            //datalist.clear();
            FragmentManager fragmentManager = FA.getSupportFragmentManager();
            Log.e("stack!!!",Integer.toString(fragmentManager.getBackStackEntryCount()));
            //fragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.main_layout,fragment);
            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
            return 0;
        }
    }
    //get scraped page by thread
    private class Thread_DB extends Thread {
//        private int thread_number;
        private List<Community_Scrap> location_list;

        public void run() {
            CommunityDatabase db = CommunityDatabase.getAppDatabase(getContext());
            location_list = db.todoDao().getAll();
        }
        public List<Community_Scrap> get_location_data()
        {
            return location_list;
        }
    }


}
