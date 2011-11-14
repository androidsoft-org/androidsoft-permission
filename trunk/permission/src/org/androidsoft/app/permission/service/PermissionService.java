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
package org.androidsoft.app.permission.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.androidsoft.app.permission.model.AppInfo;
import org.androidsoft.app.permission.model.Permission;
import org.androidsoft.app.permission.model.PermissionGroup;

/**
 *
 * @author pierre
 */
public class PermissionService
{

    private static final String TAG = "androidsoft";
    private static NameComparator mNameComparator = new NameComparator();
    private static ScoreComparator mScoreComparator = new ScoreComparator();

    /**
     * 
     * @param context
     * @return
     */
    public static List<AppInfo> getApplicationsSortedByName(Context context)
    {
        List<AppInfo> list = getApplications(context);
        Collections.sort(list, mNameComparator);
        return list;

    }

    /**
     * 
     * @param context
     * @return
     */
    public static List<AppInfo> getApplicationsSortedByScore(Context context)
    {
        List<AppInfo> list = getApplications(context);
        Collections.sort(list, mScoreComparator);
        return list;

    }

    /**
     * 
     * @param permissions
     * @param pm
     * @return
     */
    public static List<PermissionGroup> getPermissions(String[] permissions, PackageManager pm)
    {
        List<PermissionGroup> listGroups = new ArrayList<PermissionGroup>();

        if (permissions != null)
        {

            for (int i = 0; i < permissions.length; i++)
            {
                try
                {
                    String permission = permissions[i];
                    PermissionInfo pi = pm.getPermissionInfo(permission, PackageManager.GET_PERMISSIONS);
                    PermissionGroupInfo pgi = pm.getPermissionGroupInfo(pi.group, PackageManager.GET_PERMISSIONS);

                    PermissionGroup group = getGroup(listGroups, pi.group);
                    if (group == null)
                    {
                        group = new PermissionGroup();
                        group.setName(pgi.name);
                        group.setLabel( pgi.loadLabel(pm).toString() );
                        group.setDescription( pgi.loadDescription(pm).toString());
                        listGroups.add(group);
                    }
                    Permission p = new Permission();
                    p.setName( pi.name );
                    p.setLabel( pi.loadLabel(pm).toString() );
                    p.setDescription( pi.loadDescription(pm).toString());
                    p.setDangerous( pi.protectionLevel !=  PermissionInfo.PROTECTION_NORMAL );
                    group.addPermission(p);

                }
                catch (NameNotFoundException ex)
                {
                    Log.e(TAG, "Permission name not found : " + permissions[i]);
                }
            }
        }
        return listGroups;
    }

    private static List<AppInfo> getApplications(Context context)
    {
        List<AppInfo> list = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();
        for (PackageInfo info : pm.getInstalledPackages(PackageManager.GET_PERMISSIONS))
        {

            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                AppInfo app = new AppInfo();
                app.setName(info.applicationInfo.loadLabel(pm).toString());
                app.setPackageName(info.packageName);
                app.setVersion(info.versionName);
                app.setIcon(info.applicationInfo.loadIcon(pm));
                app.setListPermissions(info.permissions);
                app.setRequestedPermissions(info.requestedPermissions);
                list.add(app);
            }
        }
        return list;
    }

    private static PermissionGroup getGroup(List<PermissionGroup> list, String name)
    {
        for (PermissionGroup group : list)
        {
            if (group.getName().equals(name))
            {
                return group;
            }
        }
        return null;
    }

    private static class NameComparator implements Comparator<AppInfo>
    {

        public int compare(AppInfo app1, AppInfo app2)
        {
            return app1.getName().compareTo(app2.getName());
        }
    }

    private static class ScoreComparator implements Comparator<AppInfo>
    {

        public int compare(AppInfo app1, AppInfo app2)
        {
            return (app1.getScore() - app2.getScore());
        }
    }
}
