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
package org.androidsoft.app.permission.model;

import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pierre
 */
public class AppInfo
{

    private String name;
    private String packageName;
    private String version;
    private Drawable icon;
    private int score;
    private List<PermissionInfo> listPermissions = new ArrayList<PermissionInfo>();
    private String[] requestedPermissions;

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * @return the icon
     */
    public Drawable getIcon()
    {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(Drawable icon)
    {
        this.icon = icon;
    }

    /**
     * @return the score
     */
    public int getScore()
    {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score)
    {
        this.score = score;
    }

    /**
     * @return the listPermissions
     */
    public List<PermissionInfo> getListPermissions()
    {
        return listPermissions;
    }

    /**
     * @param permissions 
     */
    public void setListPermissions(PermissionInfo[] permissions)
    {
        if (permissions != null)
        {
            for (int i = 0; i < permissions.length; i++)
            {
                listPermissions.add(permissions[i]);
                score += 1;
                if (permissions[i].protectionLevel == PermissionInfo.PROTECTION_DANGEROUS)
                {
                    score += 10;
                }
            }
        }
    }

    /**
     * @return the requestedPermissions
     */
    public String[] getRequestedPermissions()
    {
        return requestedPermissions;
    }

    /**
     * @param requestedPermissions the requestedPermissions to set
     */
    public void setRequestedPermissions(String[] requestedPermissions)
    {
        this.requestedPermissions = requestedPermissions;
        if (requestedPermissions != null)
        {
            for (int i = 0; i < requestedPermissions.length; i++)
            {
                score += 1;
            }
        }
    }

    /**
     * @return the packageName
     */
    public String getPackageName()
    {
        return packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }
}
