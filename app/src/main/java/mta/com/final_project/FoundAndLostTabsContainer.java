package mta.com.final_project;

import android.os.Bundle;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mta.com.final_project.model.SearchParams;


public class FoundAndLostTabsContainer extends Fragment {

    private final String FOUND = "found";
    private final String LOST = "lost";
    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FoundAndLostAnimalsAdapter foundAndLostAnimalsAdapter;
    private AnimalsListFragment found;
    private AnimalsListFragment Lost;
    public FoundAndLostTabsContainer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_found_and_lost_tabs_container, container, false);
        viewPager = view.findViewById(R.id.viewPager_foundAndLostTabsContainer);
        tabLayout = view.findViewById(R.id.tabsLayout_foundAndLostTabsContainer);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        if(getArguments()!=null) {
            Boolean isLost = getArguments().getBoolean("isLost");
            selectTabIsLost(isLost);
        }
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        foundAndLostAnimalsAdapter = new FoundAndLostAnimalsAdapter(getChildFragmentManager());
        searchFragment  search = new searchFragment();


        foundAndLostAnimalsAdapter.addFragment(search, "Search");
        found=  new AnimalsListFragment(FOUND);
        Lost=  new AnimalsListFragment(LOST);
        foundAndLostAnimalsAdapter.addFragment(found, "Found");
        foundAndLostAnimalsAdapter.addFragment(Lost, "Lost");

        viewPager.setAdapter(foundAndLostAnimalsAdapter);
    }

    public void setSerachParams(SearchParams serachParams,boolean isLost)
    {
        found.setSearchParams(serachParams);
        Lost.setSearchParams(serachParams);
        selectTabIsLost( isLost);
    }

    private void selectTabIsLost(Boolean isLost)
    {
        if(isLost) {
            tabLayout.getTabAt(2).select();
        }
        else
            tabLayout.getTabAt(1).select();
    }


}
