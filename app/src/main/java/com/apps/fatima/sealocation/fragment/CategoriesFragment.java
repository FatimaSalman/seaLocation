package com.apps.fatima.sealocation.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.activities.BoatsActivity;
import com.apps.fatima.sealocation.activities.DriverActivity;
import com.apps.fatima.sealocation.activities.RequirementSeaActivity;
import com.apps.fatima.sealocation.activities.SellProductsActivity;
import com.apps.fatima.sealocation.activities.ServicesSeaActivity;
import com.apps.fatima.sealocation.activities.TanksActivity;

import java.util.Objects;

public class CategoriesFragment extends Fragment implements View.OnClickListener {

    private boolean isFragmentLoaded = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        init(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(View view) {
        ScrollView layout = view.findViewById(R.id.layout);

        TextView nameTxt = Objects.requireNonNull(getActivity()).findViewById(R.id.nameTxt);
//        nameTxt.setText(R.string.categories);
        FontManager.applyFont(getActivity(), layout);
        FontManager.applyFont(getActivity(), nameTxt);
        RelativeLayout boatLayout = view.findViewById(R.id.boatLayout);
        RelativeLayout driverLayout = view.findViewById(R.id.driverLayout);
        RelativeLayout requirementLayout = view.findViewById(R.id.requirementLayout);
        RelativeLayout serviceLayout = view.findViewById(R.id.serviceLayout);
        RelativeLayout saleLayout = view.findViewById(R.id.saleLayout);
        RelativeLayout tanksLayout = view.findViewById(R.id.tanksLayout);

        boatLayout.setOnClickListener(this);
        driverLayout.setOnClickListener(this);
        requirementLayout.setOnClickListener(this);
        serviceLayout.setOnClickListener(this);
        saleLayout.setOnClickListener(this);
        tanksLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.boatLayout) {
            Intent intent = new Intent(getActivity(), BoatsActivity.class);
            startActivity(intent);
        } else if (id == R.id.driverLayout) {
            Intent intent = new Intent(getActivity(), DriverActivity.class);
            startActivity(intent);
        } else if (id == R.id.requirementLayout) {
            Intent intent = new Intent(getActivity(), RequirementSeaActivity.class);
            startActivity(intent);
        } else if (id == R.id.serviceLayout) {
            Intent intent = new Intent(getActivity(), ServicesSeaActivity.class);
            startActivity(intent);
        } else if (id == R.id.saleLayout) {
            Intent intent = new Intent(getActivity(), SellProductsActivity.class);
            startActivity(intent);
        } else if (id == R.id.tanksLayout) {
            Intent intent = new Intent(getActivity(), TanksActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFragmentLoaded) {
            // Load your data here or do network operations here
            isFragmentLoaded = true;
        }
    }
}
