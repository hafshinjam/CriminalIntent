package com.example.criminalintent.controller.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.criminalintent.R;
import com.example.criminalintent.controller.activity.CrimePagerActivity;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.repository.CrimeRepository;
import com.example.criminalintent.repository.RepositoryInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CrimeListFragment extends Fragment {
    public static final String TAG = "CLF";
    private RecyclerView mRecyclerView;
    private RepositoryInterface<Crime> mRepository;
    private CrimeAdapter mAdapter;
    private int tempPosition = 0;

    public CrimeListFragment() {
        // Required empty public constructor
    }

    public static CrimeListFragment newInstance() {

        Bundle args = new Bundle();

        CrimeListFragment fragment = new CrimeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepository = CrimeRepository.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        findViews(view);

        //recyclerview responsibility: positioning
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //performance issues
        updateUI();
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_crimes);
    }

    private void updateUI() {
        List<Crime> crimes = mRepository.getList();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyItemChanged(tempPosition);
        }
    }

    //view holder responsibility: hold reference to row views.
    private class NotSolvedCrimeHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewId;
        private Crime mCrime;
        private TextView mTextViewTitle;
        private TextView mTextViewDate;

        public NotSolvedCrimeHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTitle = itemView.findViewById(R.id.not_solved_crime_title);
            mTextViewDate = itemView.findViewById(R.id.not_solved_crime_date);
            mTextViewId = itemView.findViewById(R.id.not_solved_crime_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tempPosition = getLayoutPosition();
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivity(intent);
                }
            });
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTextViewTitle.setText(crime.getTitle());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd - hh:mm:ss");
            String date = formatter.format(Date.parse(mCrime.getDate().toString()));
            mTextViewDate.setText(date);
            mTextViewId.setText(crime.getId().toString());
        }
    }

    private class SolvedCrimeHolder extends RecyclerView.ViewHolder {

        private Crime mCrime;
        private TextView mTextViewTitle;
        private TextView mTextViewDate;
        private ImageView mImageViewSolved;



        public SolvedCrimeHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTitle = itemView.findViewById(R.id.solved_list_row_crime_title);
            mTextViewDate = itemView.findViewById(R.id.solved_list_row_crime_date);
            mImageViewSolved = itemView.findViewById(R.id.solved_imgview_solved);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tempPosition = getLayoutPosition();
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivity(intent);
                }
            });
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTextViewTitle.setText(crime.getTitle());
            mTextViewDate.setText(crime.getDate().toString());
        }
    }

    /*adapter responsibilities:
        1. getItemCounts
        2. create view holder
        3. bind view holder
     */
    private class CrimeAdapter extends RecyclerView.Adapter {

        private static final int SOLVED_CODE = 0;
        private static final int NOT_SOLVED_CODE = 1;

        @Override
        public int getItemViewType(int position) {
            if (mCrimes.get(position).isSolved()) {
                return SOLVED_CODE;
            } else {
                return NOT_SOLVED_CODE;
            }

        }

        private List<Crime> mCrimes;

        public List<Crime> getCrimes() {
            return mCrimes;
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view;
            RecyclerView.ViewHolder crimeHolder;
            switch (viewType) {
                case SOLVED_CODE:
                    view = inflater.inflate(R.layout.solved_list_row_crime, parent, false);
                    crimeHolder = new SolvedCrimeHolder(view) {
                    };
                    return crimeHolder;
                case NOT_SOLVED_CODE:
                    view = inflater.inflate(R.layout.not_solved_list_row_crime, parent, false);
                    crimeHolder = new NotSolvedCrimeHolder(view);
                    return crimeHolder;
                default:
                    view = inflater.inflate(R.layout.solved_list_row_crime, parent, false);
                    crimeHolder = new SolvedCrimeHolder(view);
                    return crimeHolder;

            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position);
            Crime crime = mCrimes.get(position);
            switch (getItemViewType(position)) {
                case SOLVED_CODE:
                    SolvedCrimeHolder solvedCrimeHolder = (SolvedCrimeHolder) holder;
                    solvedCrimeHolder.bindCrime(crime);
                    break;
                case NOT_SOLVED_CODE:
                    NotSolvedCrimeHolder notSolvedCrimeHolder = (NotSolvedCrimeHolder) holder;
                    notSolvedCrimeHolder.bindCrime(crime);
                    break;
            }
        }

    }
}