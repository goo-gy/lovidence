package com.example.lovidence.fragments.communityfrags;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.lovidence.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class community_public extends Fragment {
    private static int Currentpage;
    ArrayList<SampleData> movieDataList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    public static community_private newInstance() {
        community_private fragment = new community_private();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_community_public, container, false);
        Currentpage = 0;
        this.InitializeMovieData();

//        ListView listView = (ListView)viewGroup.findViewById(R.id.listView);
        /*final MyAdapter myAdapter = new MyAdapter(this,movieDataList);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Toast.makeText(getApplicationContext(),
                        myAdapter.getItem(position).getMovieName(),
                        Toast.LENGTH_LONG).show();
            }
        });*/
        read();
        return viewGroup;
    }
    public void InitializeMovieData()
    {
       /* movieDataList = new ArrayList<SampleData>();

        movieDataList.add(new SampleData(R.drawable.movieposter1, "미션임파서블","15세 이상관람가"));
        movieDataList.add(new SampleData(R.drawable.movieposter2, "아저씨","19세 이상관람가"));
        movieDataList.add(new SampleData(R.drawable.movieposter3, "어벤져스","12세 이상관람가"));
    */
    }

    //show first 10 page...
    private void read() {
        communityAsyncTask task = new communityAsyncTask();
        ArrayList<String> list = new ArrayList<>();
        try {
            list = task.execute(Currentpage).get();
            if(list ==null){throw new Exception();}
        }catch(Exception e){e.printStackTrace();}

        for(String e: list){
            String[] element = e.split("-");
            //element[0];//title
            //element[1];//content
            Bitmap bm = StrToBitMap(element[2]);//element[2];//image
            //MyPhoto.setImageBitmap(bm);
        }
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

    private static class communityAsyncTask extends AsyncTask<Integer, Void, ArrayList<String>> {

        @Override //백그라운드작업(메인스레드 X)
        protected ArrayList<String> doInBackground(Integer... args) {
            HttpURLConnection httpURLConnection = null;
            String data="";
            String link="";
            try {
                data = URLEncoder.encode("u_position", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(args[0]), "UTF-8");
                link = "https://test-yetvm.run.goorm.io/test/"+"링크위치";
                URL url = new URL(link);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(data); //data 전송
                wr.flush();
                //결과 받음
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "UTF-8"));
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
            } catch (Exception e) {
                Log.d("public_community : ", "Exception Occure", e);
                httpURLConnection.disconnect();
                return null;
            }
        }
    }

}
