package com.example.lovidence.fragments.communityfrags;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lovidence.R;
import com.example.lovidence.fragments.Menu5Fragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class community_public extends Fragment {
    ViewGroup viewGroup;
    ArrayList<SampleData> datalist;
    boolean lastitemVisibleFlag = false;
    private static long lastTime;
    private static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        datalist = new ArrayList<SampleData>();
        setHasOptionsMenu(true);

    }

    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_community_private, container, false);
        setHasOptionsMenu(true);
        context = getActivity();
        lastTime = Long.MAX_VALUE;
        Toolbar toolbar = (Toolbar) viewGroup.findViewById(R.id.toolbar_private);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //this.InitializeMovieData();
        read();

        ListView listView = (ListView)viewGroup.findViewById(R.id.private_community);
        final CommunityAdapter myAdapter = new CommunityAdapter(getActivity(),datalist);
        listView.setAdapter(myAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
                    Log.e("updated check",Long.toString(lastTime));
                    read();// 데이터 로드(마지막 element가 보이는경우 )
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Toast.makeText(getActivity(),
                        myAdapter.getItem(position).getContent(),
                        Toast.LENGTH_LONG).show();
            }
        });

        return viewGroup;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        super.onCreateOptionsMenu(menu,inflater);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Fragment fragment = new Menu5Fragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_layout,fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }
    private void read() {

        communityAsyncTask task = new communityAsyncTask(context);
        ArrayList<String> list = new ArrayList<>();
        try {
            list = task.execute(lastTime).get();
            if(list ==null){throw new Exception();}
        }catch(Exception e){e.printStackTrace();}
        long updatedTime = 0;
        for(String e: list){
            if(e.equals("FILENOTFOUND")){return;}
            //Log.e("frag5",e);
            String[] element = e.split("-");
            //img - 1, content - 0, time - 2
            updatedTime = Long.parseLong(element[2]);
            Bitmap bm = StrToBitMap(element[1]);//element[2];//image
            SampleData sample = new SampleData(bm,element[0],updatedTime);
            datalist.add(sample);
            //MyPhoto.setImageBitmap(bm);
        }
        //lasttiem be last element time.
        lastTime = updatedTime;
    }
    private Bitmap StrToBitMap(String str){
        try{
            byte[] encodeByte = Base64.decode(str,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //list의 끝에 닿은경우 새로운 page를 불러와야함

    private static class communityAsyncTask extends AsyncTask<Long, Void, ArrayList<String>> {
        Context context;
        String couple_id;
        public communityAsyncTask(Context con){
            context = con;
            SharedPreferences sharedPref = context.getSharedPreferences("USERINFO",Context.MODE_PRIVATE);
            couple_id = sharedPref.getString("COUPLEID","");

        }

        @Override //백그라운드작업(메인스레드 X)
        protected ArrayList<String> doInBackground(Long... args) {
            HttpURLConnection httpURLConnection = null;
            String data="";
            String link="";
            try {
                //data = URLEncoder.encode("u_cp", "UTF-8") + "=" + URLEncoder.encode("public", "UTF-8");
                data = URLEncoder.encode("u_lastUpdate", "UTF-8") + "=" + URLEncoder.encode(Long.toString(lastTime), "UTF-8");
                link = "https://test-yetvm.run.goorm.io/test/"+"show3.php";
                Log.e("??",data);
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(data); //data 전송
                wr.flush();
                //결과 받음
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                httpURLConnection.disconnect();
                String[] list = sb.toString().split("@");   //each row distributed by @
                ArrayList<String> result = new ArrayList<>();
                for(int i=0; i<list.length; i++){
                    result.add(list[i]);
                }
                return result;
            }catch (FileNotFoundException e) {
                Log.e("filenotfoundexception","occured",e);
                httpURLConnection.disconnect();
                ArrayList<String> result = new ArrayList<>();
                result.add("FILENOTFOUND");
                return result;
            }
            catch (Exception e) {
                Log.d("public_community : ", "Exception Occure", e);
                httpURLConnection.disconnect();
                return null;
            }
        }
    }


}
