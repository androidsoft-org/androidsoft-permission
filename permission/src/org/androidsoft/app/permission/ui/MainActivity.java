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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import org.androidsoft.app.permission.R;

/**
 * 
 * @author pierre
 */
public class MainActivity extends FragmentActivity implements ApplicationsListFragment.AppListEventsCallback, OnClickListener
{

    /**
     * 
     */
    public static String EXTRA_PACKAGE_NAME = "packageName";
    private Button mButtonSortByName;
    private Button mButtonSortByScore;
    private ApplicationsListFragment mApplicationsListFragment;

    /**
     * {@inheritDoc }
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mButtonSortByName = (Button) findViewById(R.id.button_sort_name);
        mButtonSortByName.setOnClickListener(this);

        mButtonSortByScore = (Button) findViewById(R.id.button_sort_score);
        mButtonSortByScore.setOnClickListener(this);

        FragmentManager fm = getSupportFragmentManager();
        mApplicationsListFragment = (ApplicationsListFragment) fm.findFragmentById(R.id.fragment_applications_list);

        mApplicationsListFragment.update(PermissionService.getApplicationsSortedByScore(this));

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

    private void sortByName()
    {
        mApplicationsListFragment.update(PermissionService.getApplicationsSortedByName(this));
    }

    private void sortByScore()
    {
        mApplicationsListFragment.update(PermissionService.getApplicationsSortedByScore(this));
    }
}
