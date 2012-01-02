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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.androidsoft.app.permission.Constants;
import org.androidsoft.app.permission.model.AppInfo;
import org.androidsoft.app.permission.model.Permission;
import org.androidsoft.app.permission.model.PermissionGroup;

/**
 * Permission Service
 * @author Pierre Levy
 */
public class PermissionService
{

    private static final String TAG = "androidsoft";
    private static NameComparator mNameComparator = new NameComparator();
    private static ScoreComparator mScoreComparator = new ScoreComparator();
    private static Set<String> mTrustedApps;
    private static boolean mRemoveTrusted = false;

    /**
     * 
     * @param context
     * @return
     */
    public static List<AppInfo> getApplicationsSortedByName(Context context, boolean sortOrder , boolean showTrusted )
    {
        List<AppInfo> list = getApplications(context , showTrusted );
        Collections.sort(list, mNameComparator);
        if (sortOrder)
        {
            Collections.reverse(list);
        }
        return list;

    }

    /**
     * 
     * @param context
     * @return
     */
    public static List<AppInfo> getApplicationsSortedByScore(Context context, boolean sortOrder , boolean showTrusted )
    {
        List<AppInfo> list = getApplications(context , showTrusted );
        Collections.sort(list, mScoreComparator);
        if (sortOrder)
        {
            Collections.reverse(list);
        }
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
                        group.setLabel(pgi.loadLabel(pm).toString());
                        group.setDescription(pgi.loadDescription(pm).toString());
                        listGroups.add(group);
                    }
                    Permission p = new Permission();
                    p.setName(pi.name);
                    p.setLabel(pi.loadLabel(pm).toString());
                    p.setDescription(pi.loadDescription(pm).toString());
                    p.setDangerous(pi.protectionLevel != PermissionInfo.PROTECTION_NORMAL);
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

    public static boolean exists(Context context , String packageName)
    {
        try
        {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        }
        catch (NameNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public static void addTrustedApp( Context context , String appPackage )
    {
        Set<String> trustedApps = getTrustedApps(context);
        if( !trustedApps.contains(appPackage))
        {
            trustedApps.add(appPackage);
            saveTrustedApps( context , trustedApps );
        }
    }

    public static boolean isTrusted( Context context , String appPackage)
    {
        Set<String> trustedApps = getTrustedApps(context);
        return trustedApps.contains(appPackage);
    }

    public static void removeTrustedApp( Context context , String appPackage )
    {
        Set<String> trustedApps = getTrustedApps(context);
        if( trustedApps.contains(appPackage))
        {
            trustedApps.remove(appPackage);
            saveTrustedApps( context , trustedApps );
        }
    }
    
    private static List<AppInfo> getApplications(Context context , boolean showTrusted )
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
                app.setScore(getScore(info.packageName, pm));
                list.add(app);
            }
        }
        return filterApplications(context, list, showTrusted );
    }

    
    private static List<AppInfo> filterApplications(Context context , List<AppInfo> list , boolean showTrusted )
    {
        List<AppInfo> listFiltered = new ArrayList<AppInfo>();
        Set<String> trustedApps = getTrustedApps(context);
        for( AppInfo app : list )
        {
            if( !trustedApps.contains( app.getPackageName() ))
            {
                listFiltered.add(app);
            }
            else
            {
                if( showTrusted )
                {
                    app.setTrusted( true );
                    listFiltered.add(app);
                }
            }
        }
        return listFiltered;
    }
    
    private static Set<String> getTrustedApps( Context context )
    {
        if( mTrustedApps == null )
        {
            mTrustedApps = loadTrustedPackageList( context );
        }
        return mTrustedApps;
    }
    
    private static Set<String> loadTrustedPackageList(Context context)
    {
        Set<String> set = new HashSet<String>();
        SharedPreferences prefs = context.getSharedPreferences( Constants.PREFS, Context.MODE_PRIVATE);
        return prefs.getStringSet( Constants.KEY_TRUSTED_APPS, set);
    }

    private static void saveTrustedApps(Context context , Set<String> trustedApps )
    {
        SharedPreferences prefs = context.getSharedPreferences( Constants.PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putStringSet( Constants.KEY_TRUSTED_APPS, trustedApps );
        editor.commit();
        mTrustedApps = trustedApps;
    }
    
    private static int getScore(String packageName, PackageManager pm)
    {
        int score = 0;
        try
        {
            PackageInfo pinfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            if (pinfo.requestedPermissions != null)
            {
                for (String permission : pinfo.requestedPermissions)
                {
                    PermissionInfo pi = pm.getPermissionInfo(permission, PackageManager.GET_PERMISSIONS);
                    score += (pi.protectionLevel != PermissionInfo.PROTECTION_NORMAL) ? 100 : 1;
                }
            }
        }
        catch (NameNotFoundException ex)
        {
            Log.e(TAG, "Permission name not found : ");
        }
        return score;
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
            return app1.getName().compareToIgnoreCase(app2.getName());
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
