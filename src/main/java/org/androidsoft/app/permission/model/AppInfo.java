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
package org.androidsoft.app.permission.model;

import android.graphics.drawable.Drawable;

/**
 * Application Info object
 * @author Pierre Levy
 */
public class AppInfo
{

    private String _name;
    private String _packageName;
    private String _version;
    private Drawable _icon;
    private int _score;
    private boolean _trusted;

    /**
     * @return the name
     */
    public String getName()
    {
        return _name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        _name = name;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return _version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        _version = version;
    }

    /**
     * @return the icon
     */
    public Drawable getIcon()
    {
        return _icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(Drawable icon)
    {
        _icon = icon;
    }

    /**
     * @return the score
     */
    public int getScore()
    {
        return _score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score)
    {
        _score = score;
    }

    /**
     * @return the packageName
     */
    public String getPackageName()
    {
        return _packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName)
    {
        _packageName = packageName;
    }

    public boolean isTrusted()
    {
        return _trusted;
    }
    
    public void setTrusted(boolean trusted)
    {
        _trusted = trusted;
    }
}
