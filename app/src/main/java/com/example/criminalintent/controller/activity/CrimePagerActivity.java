package com.example.criminalintent.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.criminalintent.R;
import com.example.criminalintent.controller.fragment.CrimeDetailFragment;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.repository.CrimeRepository;
import com.example.criminalintent.repository.RepositoryInterface;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.CrimeId";
    public static final String TAG = "CPA";

    public static ViewPager2 mCrimeViewPager;
    private RepositoryInterface mRepository;

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mRepository = CrimeRepository.getInstance();
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int position = mRepository.getPosition(crimeId);

        findViews();
        setUI(position);
    }

    private void findViews() {
        mCrimeViewPager = findViewById(R.id.crime_view_pager);
    }

    private void setUI(int position) {
        FragmentStateAdapter adapter = new CrimeViewPagerAdapter(this, mRepository.getList());
        mCrimeViewPager.setAdapter(adapter);

        //this method "must" be placed after setAdapter.
        mCrimeViewPager.setCurrentItem(position);
        mCrimeViewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });

        mCrimeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            int current;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == 1)
                    if (current == 0)
                        mCrimeViewPager.setCurrentItem(mRepository.getList().size() - 1);
                    else if (current == 99) {
                        mCrimeViewPager.setCurrentItem(0);
                    }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                current = position;

            }
        });
    }

    private class CrimeViewPagerAdapter extends FragmentStateAdapter {
        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        private List<Crime> mCrimes;

        public List<Crime> getCrimes() {
            return mCrimes;
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        public CrimeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Crime> crimes) {
            super(fragmentActivity);

            mCrimes = crimes;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d(TAG, "position: " + (position + 1));
            return CrimeDetailFragment.newInstance(mCrimes.get(position).getId());
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}