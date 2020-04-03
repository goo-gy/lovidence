package com.example.lovidence;
import com.example.lovidence.*;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.FrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    Menu1Fragment menu1Fragment = new Menu1Fragment();
    Menu2Fragment menu2Fragment = new Menu2Fragment();
    Menu3Fragment menu3Fragment = new Menu3Fragment();
    Menu4Fragment menu4Fragment = new Menu4Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        menu1Fragment = new Menu1Fragment();
        menu2Fragment = new Menu2Fragment();
        menu3Fragment = new Menu3Fragment();
        menu4Fragment = new Menu4Fragment();
        // 첫 화면 지정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu1Fragment).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.tab1:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu1Fragment).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab2:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu2Fragment).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab3:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu3Fragment).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.tab4:{
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,menu4Fragment).commitAllowingStateLoss();
                        return true;
                    }
                    default: return false;
                }
            }
        });
    }
}