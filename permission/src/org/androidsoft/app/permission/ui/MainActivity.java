/* Copyright (c) 2010-2012 Pierre LEVY androidsoft.org
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

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import org.androidsoft.app.permission.R;
import org.androidsoft.app.permission.model.AppInfo;
import org.androidsoft.app.permission.service.ApplicationChangesService;

/**
 * Main Activity
 *
 * @author Pierre Levy
 */
public class MainActivity extends FragmentActivity implements ApplicationsListFragment.AppListEventsCallback, OnClickListener, ApplicationChangesListener
{

    public static String EXTRA_PACKAGE_NAME = "packageName";
    private static final String KEY_SORT = "sort";
    private static final String KEY_TOGGLE_NAME = "toggle_name";
    private static final String KEY_TOGGLE_SCORE = "toggle_score";
    private static final String KEY_SHOW_TRUSTED = "show_trusted";
    private static final String KEY_FILTER_ENABLED = "filter_enabled";
    private static final String KEY_FILTER_VALUE = "filter_value";
    
    private static final int SORT_SCORE = 0;
    private static final int SORT_NAME = 1;
    
    private TextView mButtonSortByName;
    private TextView mButtonSortByScore;
    private ImageView mButtonShowTrusted;
    private ImageView mButtonFilter;
    private View mLayoutFilter;
    private TextView mTvFilter;
    private View mIndicatorName;
    private View mIndicatorScore;
    private View mIndicatorTrusted;
    private View mIndicatorFilter;
    private ApplicationsListFragment mApplicationsListFragment;
    private boolean mToggleName = true;
    private boolean mToggleScore = false;
    private boolean mShowTrusted;
    private boolean mFilterEnabled;
    private String mFilterValue;
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

        mButtonSortByName = (TextView) findViewById(R.id.button_sort_name);
        mButtonSortByName.setOnClickListener(this);

        mButtonSortByScore = (TextView) findViewById(R.id.button_sort_score);
        mButtonSortByScore.setOnClickListener(this);

        mButtonShowTrusted = (ImageView) findViewById(R.id.button_show_trusted);
        mButtonShowTrusted.setOnClickListener(this);

        mButtonFilter = (ImageView) findViewById(R.id.button_filter);
        mButtonFilter.setOnClickListener(this);

        mTvFilter = (TextView) findViewById(R.id.label_filter);
        mLayoutFilter = findViewById(R.id.layout_filter);
        mLayoutFilter.setOnClickListener(this);

        mIndicatorName = findViewById(R.id.indicator_name);
        mIndicatorScore = findViewById(R.id.indicator_score);
        mIndicatorTrusted = findViewById(R.id.indicator_trusted);
        mIndicatorFilter = findViewById(R.id.indicator_filter);

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
        refreshAppList();
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
        else if (view == mButtonShowTrusted)
        {
            toggleShowTrusted();
        }
        else if (view == mButtonFilter)
        {
            toggleFilter();
        }
        else if (view == mLayoutFilter)
        {
            updateFilter();
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
        editor.putBoolean(KEY_SHOW_TRUSTED, mShowTrusted);
        editor.putBoolean(KEY_FILTER_ENABLED, mFilterEnabled);
        editor.putString(KEY_FILTER_VALUE, mFilterValue);
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
        mShowTrusted = prefs.getBoolean(KEY_SHOW_TRUSTED, true);
        mFilterEnabled = prefs.getBoolean(KEY_FILTER_ENABLED, false);
        mFilterValue = prefs.getString(KEY_FILTER_VALUE, null );
        if (mInvalidate)
        {
            updateUI();
            refreshApplicationFragment();
            mInvalidate = false;
        }
        refreshIndicators();
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
        updateUI();
    }

    private void sortByScore()
    {
        mToggleScore = !mToggleScore;
        mSort = SORT_SCORE;
        updateUI();
    }

    private void toggleShowTrusted()
    {
        mShowTrusted = !mShowTrusted;
        updateUI();
    }

    private void updateUI()
    {
        refreshAppList();
        refreshIndicators();
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
                return PermissionService.getApplicationsSortedByName(this, mToggleName, mShowTrusted, mFilterValue);

            case SORT_SCORE:
            default:
                return PermissionService.getApplicationsSortedByScore(this, mToggleScore, mShowTrusted, mFilterValue);
        }
    }

    private void refreshIndicators()
    {
        if (mSort == SORT_SCORE)
        {
            mIndicatorScore.setBackgroundResource(R.drawable.bar_on);
            mIndicatorName.setBackgroundResource(R.drawable.bar_off);
        }
        else
        {
            mIndicatorScore.setBackgroundResource(R.drawable.bar_off);
            mIndicatorName.setBackgroundResource(R.drawable.bar_on);
        }

        if (mShowTrusted)
        {
            mIndicatorTrusted.setBackgroundResource(R.drawable.bar_on);
        }
        else
        {
            mIndicatorTrusted.setBackgroundResource(R.drawable.bar_off);
        }

        if (mFilterEnabled)
        {
            mLayoutFilter.setVisibility(View.VISIBLE);
            mIndicatorFilter.setBackgroundResource(R.drawable.bar_on);
            mTvFilter.setText(mFilterValue);

        }
        else
        {
            mLayoutFilter.setVisibility(View.GONE);
            mIndicatorFilter.setBackgroundResource(R.drawable.bar_off);
        }
    }

    private void toggleFilter()
    {
        mFilterEnabled = !mFilterEnabled;

        if (mFilterEnabled)
        {
            updateFilter();
        }
        else
        {
            mFilterValue = null;
            updateUI();
        }
    }

    private void updateFilter()
    {
        final String[] items = PermissionService.getPermissions();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title_select_permission));
        builder.setItems(items, new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int item)
            {
                mFilterValue = items[item];
                updateUI();
            }
        });
        
        AlertDialog alert = builder.create();
        
        alert.show();

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
