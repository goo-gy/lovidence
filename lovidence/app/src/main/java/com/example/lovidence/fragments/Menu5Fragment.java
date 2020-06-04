package com.example.lovidence.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lovidence.R;
import com.example.lovidence.fragments.communityfrags.community_private;
import com.example.lovidence.fragments.communityfrags.community_public;

import java.lang.reflect.Field;

public class Menu5Fragment extends Fragment {
    ViewGroup viewGroup;
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentStateAdapter pagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu5, container, false);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = viewGroup.findViewById(R.id.pager);
        reduceDragSensitivity();
        pagerAdapter = new ScreenSlidePagerAdapter(getActivity());
        mPager.setAdapter(pagerAdapter);
        return viewGroup;
    }



    private void reduceDragSensitivity(){
        try{
            Field ff = ViewPager2.class.getDeclaredField("mRecyclerView");
            ff.setAccessible(true);
            RecyclerView recyclerView = (RecyclerView) ff.get(mPager);
            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop") ;
            touchSlopField.setAccessible(true);
            int touchSlop = (int) touchSlopField.get(recyclerView);
            touchSlopField.set(recyclerView,touchSlop*4);   //touch sensitive
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            int index = position;
            switch(index){
                case 0: return community_private.newInstance("private","start");
                case 1 : return community_public.newInstance();
                default: return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }


        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
    