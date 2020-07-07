package com.example.lovidence.fragments.communityfrags;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lovidence.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
    private static Fragment myfragment;
    private boolean first;
    private static CommunityGridAdapter myAdapter;
    private static GridView gridView;

    ArrayList<SampleData> post_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        datalist = new ArrayList<SampleData>();
        myAdapter = new CommunityGridAdapter(getActivity(), R.layout.community_base ,datalist);
        first = true;
        lastTime = Long.MAX_VALUE;
        setHasOptionsMenu(true);
    }

    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_community_public, container, false);
        setHasOptionsMenu(true);
        context = getActivity();
        myfragment = this;
        Toolbar toolbar = (Toolbar) viewGroup.findViewById(R.id.toolbar_public);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(context, "Public", Toast.LENGTH_SHORT).show();
        if(first){read();first = false;}

        //ListView listView = (ListView)viewGroup.findViewById(R.id.public_community);
        gridView = (GridView)viewGroup.findViewById(R.id.public_community);
        gridView.setAdapter(myAdapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                //transfer img and string
                contentAsyncTask loadContent = new contentAsyncTask(myAdapter, getActivity());
                loadContent.execute(position);
                //Log.e("count",Integer.toString(new FragmentManager.getBackStackEntryCount()));
                //myAdapter.notifyDataSetChanged();
                gridView.setEnabled(false);
                ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
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
                    Log.e("??1", Integer.toString(fm.getBackStackEntryCount()));
                    if(fm.getBackStackEntryCount() > 0){
                        fm.popBackStack();
                        gridView.setEnabled(true);
                        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                    }
                    else{
                        getActivity().onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });
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
            //Fragment fragment = new Menu5Fragment();
            //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            getActivity().onBackPressed();
            //Log.e("stack!!!",Integer.toString(fragmentManager.getBackStackEntryCount()));
            //fragmentManager.popBackStackImmediate(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.main_layout,fragment);
            //fragmentTransaction.addToBackStack(null);
            //fragmentTransaction.commit();
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
            myAdapter.add(sample);
            //datalist.add(sample);
            myAdapter.notifyDataSetChanged();//???
            //MyPhoto.setImageBitmap(bm);
            Log.e(Long.toString(lastTime),Long.toString(updatedTime));
            if(lastTime > updatedTime){lastTime = updatedTime;}
        }
        //lasttiem be last element time.

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
    private static class contentAsyncTask extends AsyncTask<Integer, Void, Integer> {
        FragmentActivity FA;
        CommunityGridAdapter myAdapter;
        public contentAsyncTask(CommunityGridAdapter adapter,FragmentActivity FA) {
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
            b.putLong("time",myAdapter.getItem(args[0]).getTime());
            b.putInt("flag",1);
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
    class CommunityGridAdapter extends BaseAdapter {
        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<SampleData> sample;
        int mylayout;

        public CommunityGridAdapter(Context context, int layout, ArrayList<SampleData> data) {
            mContext = context;
            mylayout = layout;
            sample = data;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public void add(SampleData data){
            sample.add(data);
        }


        @Override
        public int getCount()  {
            return sample.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public SampleData getItem(int position) {
            return sample.get(position);
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.community_base, null);
            ImageView imageView = (ImageView)view.findViewById(R.id.img);
            Bitmap bitmap = sample.get(position).getImg();
            imageView.setImageBitmap(bitmap);

            return view;
        }

    /*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = inf.inflate(layout, null);
        //ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1);
        //iv.setImageResource(img[position]);

        return convertView;
    }
     */
    }
}

