/* Copyright (c) 2010-2015 Pierre LEVY androidsoft.org
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

import android.content.Intent;
import android.os.Bundle;
import org.androidsoft.app.permission.R;
import org.androidsoft.app.permission.service.PreferencesService;
import org.androidsoft.app.permission.service.ThemeChangesListener;
import org.androidsoft.utils.ui.BasicActivity;

/**
 * Permission Base Activity
 * @author Pierre LEVY
 */
public abstract class PermissionBaseActivity extends BasicActivity implements ThemeChangesListener
{
    
        @Override
    public void onCreate(Bundle icicle)
    {

        super.onCreate(icicle);

        PreferencesService.addThemeListener( this );
        setTheme(PreferencesService.getThemeId());
        
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public int getMenuResource()
    {
        return R.menu.menu_close;
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public int getMenuCloseId()
    {
        return R.id.menu_close;
    }

    
    @Override
    public void onChangeTheme()
    {
        recreate();
    }
    
}
