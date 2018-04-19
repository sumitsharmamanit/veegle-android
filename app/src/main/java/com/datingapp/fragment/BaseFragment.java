package com.datingapp.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;



public class BaseFragment extends Fragment{

    public void replaceFragmentWithoutBack(int containerViewId, Fragment fragment, String fragmentTag) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .commit();
    }

    public void replaceFragmentWithBack(int containerViewId,
                                        Fragment fragment,
                                        String fragmentTag,
                                        String backStackStateName) {
        try {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void replaceFragment(int containerViewId, Fragment frag) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        if (manager != null) {
            FragmentTransaction t = manager.beginTransaction();
            Fragment currentFrag = manager.findFragmentById(containerViewId);

            //Check if the new Fragment is the same
            //If it is, don't add to the back stack
            if (currentFrag != null && currentFrag.getClass().equals(frag.getClass())) {
                t.replace(containerViewId, frag).commit();
            } else {
                t.replace(containerViewId, frag).addToBackStack(null).commit();
            }
        }
    }

    public void addFragmentWithoutRemove(int containerViewId, Fragment fragment, String fragmentName) {
        try {
        String tag = (String) fragment.getTag();
        getActivity().getSupportFragmentManager().beginTransaction()
                // remove fragment from fragment manager
                //fragmentTransaction.remove(getActivity().getSupportFragmentManager().findFragmentByTag(tag));
                // add fragment in fragment manager
                .add(containerViewId, fragment, fragmentName)
                // add to back stack
                .addToBackStack(tag)
                .commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addDetailsFragment(int containerViewId, Fragment whereYouMove, String previousFragmentTag) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.hide(getFragmentManager().findFragmentByTag(previousFragmentTag));
        ft.add(containerViewId, whereYouMove);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public void addFragmentWithRemove(int containerViewId, Fragment fragment, String fragmentName) {
        String tag = (String) fragment.getTag();
        getActivity().getSupportFragmentManager().beginTransaction()
                // remove fragment from fragment manager
                .remove(getActivity().getSupportFragmentManager().findFragmentByTag(tag))
                // add fragment in fragment manager
                .add(containerViewId, fragment, fragmentName)
                // add to back stack
                .addToBackStack(tag)
                .commit();
    }

    /**
     * Method for get screen height and width
     */
    private int width, height;

    public int[] getDeviceHeightWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        int[] i = new int[2];
        i[0] = height;
        i[1] = width;
        return i;
    }
}
