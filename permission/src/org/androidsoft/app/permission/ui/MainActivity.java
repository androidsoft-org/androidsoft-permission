/* Copyright (c) 2010-2011 Pierre LEVY androidsoft.org
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.androidsoft.app.permission.ui;

import org.androidsoft.app.permission.service.ApplicationChangesListener;
import org.androidsoft.app.permission.service.PermissionService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import java.util.List;
import org.androidsoft.app.permission.R;
import org.androidsoft.app.permission.model.AppInfo;
import org.androidsoft.app.permission.service.ApplicationChangesService;

/**
 * Main Activity 
 * @author Pierre Levy
 */
public class MainActivity extends FragmentActivity implements ApplicationsListFragment.AppListEventsCallback, OnClickListener, ApplicationChangesListener
{

    public static String EXTRA_PACKAGE_NAME = "packageName";
    private static final String KEY_SORT = "sort";
    private static final String KEY_TOGGLE_NAME = "toggle_name";
    private static final String KEY_TOGGLE_SCORE = "toggle_score";
    private static final int SORT_SCORE = 0;
    private static final int SORT_NAME = 1;
    private Button mButtonSortByName;
    private Button mButtonSortByScore;
    private ApplicationsListFragment mApplicationsListFragment;
    private boolean mToggleName = true;
    private boolean mToggleScore = false;
    private int mSort;
    private boolean mInvalidate;
    private String mPackageName;

    /**
     * {@inheritDoc }
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        setContentView(R.layout.main);

        mButtonSortByName = (Button) findViewById(R.id.button_sort_name);
        mButtonSortByName.setOnClickListener(this);

        mButtonSortByScore = (Button) findViewById(R.id.button_sort_score);
        mButtonSortByScore.setOnClickListener(this);

        FragmentManager fm = getSupportFragmentManager();
        mApplicationsListFragment = (ApplicationsListFragment) fm.findFragmentById(R.id.fragment_applications_list);

        ApplicationChangesService.registerListener(this);
        mInvalidate = true;
    }

    /**
     * {@inheritDoc }
     */
    public void onAppSelected(String packageName)
    {
        mPackageName = packageName;
        FragmentManager fm = getSupportFragmentManager();
        ApplicationFragment applicationFragment = (ApplicationFragment) fm.findFragmentById(R.id.fragment_application_details);
        if (applicationFragment != null)
        {
            applicationFragment.updateApplication(this, packageName);
        }
        else
        {
            Intent intent = new Intent(this, ApplicationActivity.class);
            intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
            startActivity(intent);
        }
    }

    /**
     * {@inheritDoc }
     */
    public void onApplicationChange()
    {
        mInvalidate = true;
    }

    /**
     * {@inheritDoc }
     */
    public void onClick(View view)
    {
        if (view == mButtonSortByName)
        {
            sortByName();
        }
        else if (view == mButtonSortByScore)
        {
            sortByScore();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_help:
                help();
                return true;
            case R.id.menu_credits:
                credits();
                return true;
        }
        return false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(KEY_SORT, mSort);
        editor.putBoolean(KEY_TOGGLE_NAME, mToggleName);
        editor.putBoolean(KEY_TOGGLE_SCORE, mToggleScore);
        editor.commit();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        mSort = prefs.getInt(KEY_SORT, SORT_SCORE);
        mToggleName = prefs.getBoolean(KEY_TOGGLE_NAME, false);
        mToggleScore = prefs.getBoolean(KEY_TOGGLE_SCORE, false);
        if (mInvalidate)
        {
            refreshAppList();
            refreshApplicationFragment();
            mInvalidate = false;
        }
    }

    private void refreshAppList()
    {
        new LoadingTask().execute();
    }

    private void refreshApplicationFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        ApplicationFragment applicationFragment = (ApplicationFragment) fm.findFragmentById(R.id.fragment_application_details);
        if (applicationFragment != null)
        {
            applicationFragment.updateApplication(this, mPackageName);
        }
    }
    private void help()
    {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void credits()
    {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    private void sortByName()
    {
        mToggleName = !mToggleName;
        mSort = SORT_NAME;
        refreshAppList();
    }

    private void sortByScore()
    {
        mToggleScore = !mToggleScore;
        mSort = SORT_SCORE;
        refreshAppList();
    }

    private void update(List<AppInfo> list)
    {
        mApplicationsListFragment.update(list);
    }

    private List<AppInfo> getApplicationsList()
    {
        switch (mSort)
        {
            case SORT_NAME:
                return PermissionService.getApplicationsSortedByName(this, mToggleName);

            case SORT_SCORE:
            default:
                return PermissionService.getApplicationsSortedByScore(this, mToggleScore);
        }
    }

    private class LoadingTask extends AsyncTask<Void, Void, Void>
    {

        private List<AppInfo> mList;

        @Override
        protected Void doInBackground(Void... arg0)
        {
            mList = getApplicationsList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            update(mList);
        }
    }
}
