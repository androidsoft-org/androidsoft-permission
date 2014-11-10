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
package org.androidsoft.app.permission.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import java.util.ArrayList;

/**
 * Preferences Service
 *
 * @author Pierre LEVY
 */
public class PreferencesService
{
    
    
    private static final String PREF_NAME = "PermissionFriendlyAppsPreferences";
    private static final String PREF_THEME = "Theme";
    private static final int THEME_LIGHT = 0;
    private static final int THEME_DARK = 1;

    private static int mTheme = 1;
    private static ArrayList<ThemeChangesListener> mThemeListeners = new ArrayList<ThemeChangesListener>();

    public static int getThemeId()
    {
        int nThemeId;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            nThemeId = isThemeDark() ? android.R.style.Theme_Material : android.R.style.Theme_Material_Light;
        }
        else
        {
            nThemeId = isThemeDark() ? android.R.style.Theme_Holo : android.R.style.Theme_Holo_Light;
        }
        return nThemeId;
    }

    public static boolean isThemeDark()
    {
        return mTheme != THEME_LIGHT;
    }
    
    public static void addThemeListener( ThemeChangesListener listener )
    {
        mThemeListeners.add(listener);
    }
    
    public static void notifyThemeListeners( Context context, boolean bDarkTheme )
    {
        mTheme = ( bDarkTheme ) ? THEME_DARK : THEME_LIGHT;
        SharedPreferences prefs = context.getSharedPreferences( PREF_NAME, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt( PREF_THEME, mTheme );
        editor.apply();
        for( ThemeChangesListener listener : mThemeListeners )
        {
            listener.onChangeTheme();
        }
    }

}
