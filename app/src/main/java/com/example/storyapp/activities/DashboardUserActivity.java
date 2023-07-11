package com.example.storyapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.storyapp.BooksUserFragment;
import com.example.storyapp.databinding.ActivityDashboardUserBinding;
import com.example.storyapp.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {


public ArrayList<ModelCategory> categoryArrayList;
public ViewPagerAdapter viewPagerAdapter;
    //view binding
    private ActivityDashboardUserBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();


        setupViewPagerAdapter(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        //handle click,logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(DashboardUserActivity.this, MainActivity.class));
                finish();
            }
        });

//        //handle click, open profile
//        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(DashboardUserActivity.this, ProfileActivity.class));
//            }
//        });

    }

    private void setupViewPagerAdapter(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);
        categoryArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();

                ModelCategory modelAll = new ModelCategory("01","All", "", 1);
                ModelCategory modelMostViewed = new ModelCategory("02","Most Viewed", "", 1);
                ModelCategory modelMostDownloaded = new ModelCategory("03","Most Downloaded", "", 1);

                categoryArrayList.add(modelAll);
                categoryArrayList.add(modelMostViewed);
                categoryArrayList.add(modelMostDownloaded);

                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        ""+modelAll.getId(),
                        ""+modelAll.getCategory(),
                        ""+modelAll.getUid()
                ), modelAll.getCategory());

                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        ""+modelMostViewed.getId(),
                        ""+modelMostViewed.getCategory(),
                        ""+modelMostViewed.getUid()
                ), modelMostViewed.getCategory());

                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        ""+modelMostDownloaded.getId(),
                        ""+modelMostDownloaded.getCategory(),
                        ""+modelMostDownloaded.getUid()
                ), modelMostDownloaded.getCategory());

                viewPagerAdapter.notifyDataSetChanged();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    categoryArrayList.add(model);
                    viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                            ""+model.getId(),
                            ""+model.getCategory(),
                            ""+model.getUid()), model.getCategory());
                    viewPagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewPager.setAdapter(viewPagerAdapter);
    }

        public class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<BooksUserFragment> fragmentList = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context;

            public ViewPagerAdapter(FragmentManager fm, int behavior, Context context) {
                super(fm, behavior);
                this.context = context;
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            private void addFragment(BooksUserFragment fragment, String title){
                fragmentList.add(fragment);
                fragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position){
                return fragmentTitleList.get(position);
            }
        }


    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            binding.subTitleTv.setText("Not Logged In");
            binding.profileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DashboardUserActivity.this, "You are not logged in", Toast.LENGTH_SHORT).show();
                 }
            });

        }
        else {
            String email =firebaseUser.getEmail();
            binding.subTitleTv.setText(email);
            binding.profileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DashboardUserActivity.this, ProfileActivity.class));
                }
            });

        }
    }
}