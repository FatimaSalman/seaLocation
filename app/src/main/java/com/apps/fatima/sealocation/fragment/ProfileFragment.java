package com.apps.fatima.sealocation.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.R;
import com.booking.rtlviewpager.RtlViewPager;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private boolean isFragmentLoaded = false;
    private TabLayout tabLayout;
    private RtlViewPager viewPager;
    private ViewPagerAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_select, container, false);

        init(view);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(View view) {
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
    }

    private void setupViewPager(ViewPager viewPager) {

        if (AppPreferences.getString(getActivity(), "partner_boat").equals("partner_boat")) {
            adapter.addFrag(new ProfileBoatFragment(), getString(R.string.boat));
        }
        if (AppPreferences.getString(getActivity(), "partner_jetski").equals("partner_jetski")) {
            adapter.addFrag(new ProfileTankFragment(), getString(R.string.tank));
        }
        if (AppPreferences.getString(getActivity(), "partner_diver").equals("partner_diver")) {
            adapter.addFrag(new ProfileDrivingFragment(), getString(R.string.driver));
        }
        if (AppPreferences.getString(getActivity(), "partner_supplier").equals("partner_supplier")) {
            adapter.addFrag(new ProfileRequirementFragment(), getString(R.string.requirement));
        }
        if (AppPreferences.getString(getActivity(), "partner_services").equals("partner_services")) {
            adapter.addFrag(new ProfileServiceFragment(), getString(R.string.service));
        }
        if (AppPreferences.getString(getActivity(), "partner_Seller").equals("partner_Seller")) {
            adapter.addFrag(new ProfileProductFragment(), getString(R.string.seller));
        }

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFragmentLoaded) {
            isFragmentLoaded = true;
        }
    }
}
