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

import org.androidsoft.app.permission.service.ApplicationChangesListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import org.androidsoft.app.permission.R;
import org.androidsoft.app.permission.service.ApplicationChangesService;
import org.androidsoft.app.permission.service.PermissionService;

/**
 * Application Activity
 * @author Pierre Levy
 */
public class ApplicationActivity extends FragmentActivity implements ApplicationChangesListener
{
    private String mPackageName;
    private boolean mInvalidate;
    /**
     * 
     * @param savedInstanceState
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


        
        setContentView(R.layout.application_activity);
        
        FragmentManager fm = getSupportFragmentManager();
        ApplicationFragment applicationFragment = (ApplicationFragment) fm.findFragmentById( R.id.fragment_application_details );
        
        Intent intent = getIntent();
        mPackageName = intent.getStringExtra( MainActivity.EXTRA_PACKAGE_NAME );
        
        applicationFragment.updateApplication( this, mPackageName );
        
        ApplicationChangesService.registerListener(this);

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
    protected void onResume()
    {
        super.onResume();
        if( mInvalidate && !PermissionService.exists( this, mPackageName ))
        {
            finish();
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

    public void onApplicationChange()
    {
        mInvalidate = true;
    }

}
