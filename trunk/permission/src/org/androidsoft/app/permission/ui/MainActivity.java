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

import org.androidsoft.app.permission.service.PermissionService;
import android.content.Intent;
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

/**
 * Main Activity 
 * @author pierre
 */
public class MainActivity extends FragmentActivity implements ApplicationsListFragment.AppListEventsCallback, OnClickListener
{

    public static String EXTRA_PACKAGE_NAME = "packageName";
    private Button mButtonSortByName;
    private Button mButtonSortByScore;
    private ApplicationsListFragment mApplicationsListFragment;
    private boolean mToggleName;
    private boolean mToggleScore;

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

        new LoadingTask().execute();
    }

    /**
     * {@inheritDoc }
     */
    public void onAppSelected(String packageName)
    {
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
            case R.id.menu_credits:
                credits();
                return true;
        }
        return false;
    }

    private void credits()
    {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    private void sortByName()
    {
        mToggleName = !mToggleName;
        mApplicationsListFragment.update(PermissionService.getApplicationsSortedByName(this, mToggleName));
    }

    private void sortByScore()
    {
        mToggleScore = !mToggleScore;
        mApplicationsListFragment.update(PermissionService.getApplicationsSortedByScore(this, mToggleScore));
    }

    private class LoadingTask extends AsyncTask<Void, Void, Void>
    {
        List<AppInfo> mListApps;

        @Override
        protected Void doInBackground(Void... arg0)
        {
            mListApps = PermissionService.getApplicationsSortedByScore( MainActivity.this, mToggleScore);
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            mApplicationsListFragment.update( mListApps );
        }

    }
}
