/* Copyright (c) 2010-2014 Pierre LEVY androidsoft.org
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

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import org.androidsoft.app.permission.R;
import org.androidsoft.app.permission.service.PreferencesService;

/**
 *
 * @author Pierre LEVY
 */
public class PreferencesActivity extends PermissionBaseActivity implements OnCheckedChangeListener
{

    private CompoundButton  mToggleDarkTheme;
    private boolean mInitialThemeStatus;
    private boolean mCurrentThemeStatus;

    @Override
    public void onCreate(Bundle icicle)
    {

        super.onCreate(icicle);

        setContentView(R.layout.preferences);

        mToggleDarkTheme = (CompoundButton) findViewById(R.id.checkbox_theme_dark);
        mToggleDarkTheme.setOnCheckedChangeListener( this );
        mInitialThemeStatus = PreferencesService.isThemeDark();
        mCurrentThemeStatus = mInitialThemeStatus;
        mToggleDarkTheme.setChecked( mCurrentThemeStatus );

    }
    

    @Override
    public void onCheckedChanged( CompoundButton buttonView, boolean isChecked)
    {
        if( buttonView == mToggleDarkTheme )
        {
            mCurrentThemeStatus = isChecked;
        }
    }

    @Override
    protected void onPause()
    {
        if( mCurrentThemeStatus != mInitialThemeStatus )
        {
            PreferencesService.notifyThemeListeners( this , mCurrentThemeStatus );
            mInitialThemeStatus = mCurrentThemeStatus;
        }
        
        super.onPause(); 
    }
    
    @Override
    public void onChangeTheme()
    {
        // do nothing;
    }

    
}
