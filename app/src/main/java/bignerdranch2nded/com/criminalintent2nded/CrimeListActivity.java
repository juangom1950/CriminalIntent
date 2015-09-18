package bignerdranch2nded.com.criminalintent2nded;

import android.support.v4.app.Fragment;

/**
 * Created by Juan on 9/1/2015.
 */
public class CrimeListActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
