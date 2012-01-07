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
import java.util.List;
import org.androidsoft.app.permission.Constants;
import org.androidsoft.app.permission.model.AppInfo;
import org.androidsoft.app.permission.model.Permission;
import org.androidsoft.app.permission.model.PermissionGroup;

/**
 * Permission Service
 *
 * @author Pierre Levy
 */
public class PermissionService
{

    private static final String TAG = "androidsoft";
    private static NameComparator mNameComparator = new NameComparator();
    private static ScoreComparator mScoreComparator = new ScoreComparator();
    private static List<String> mTrustedApps;
    private static boolean mRemoveTrusted = false;

    /**
     * Get applications sorted by name
     * @param context The context
     * @return The list
     */
    public static List<AppInfo> getApplicationsSortedByName(Context context, boolean sortOrder, boolean showTrusted)
    {
        List<AppInfo> list = getApplications(context, showTrusted);
        Collections.sort(list, mNameComparator);
        if (sortOrder)
        {
            Collections.reverse(list);
        }
        return list;

    }

    /**
     * Gets applications list sorted by score
     * @param context The context
     * @return The list
     */
    public static List<AppInfo> getApplicationsSortedByScore(Context context, boolean sortOrder, boolean showTrusted)
    {
        List<AppInfo> list = getApplications(context, showTrusted);
        Collections.sort(list, mScoreComparator);
        if (sortOrder)
        {
            Collections.reverse(list);
        }
        return list;

    }

    /**
     * Gets the permission group list
     * @param permissions Permissions
     * @param pm the package manager
     * @return The permission group list
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

    /**
     * Checks if package exists
     * @param context The context
     * @param packageName The package name
     * @return true if the package exists, otherwise false
     */
    public static boolean exists(Context context, String packageName)
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

    /**
     * Add a trusted app
     * @param context The context
     * @param appPackage The app package name
     */
    public static void addTrustedApp(Context context, String appPackage)
    {
        List<String> trustedApps = getTrustedApps(context);
        if (!trustedApps.contains(appPackage))
        {
            trustedApps.add(appPackage);
            saveTrustedApps(context, trustedApps);
        }
    }

    /**
     * Remove the app as trusted 
     * @param context The context
     * @param appPackage The app package name
     */
    public static void removeTrustedApp(Context context, String appPackage)
    {
        List<String> trustedApps = getTrustedApps(context);
        if (trustedApps.contains(appPackage))
        {
            trustedApps.remove(appPackage);
            saveTrustedApps(context, trustedApps);
        }
    }

    /**
     * Checks if the app is trusted
     * @param context The context
     * @param appPackage The app package name
     * @return true if the app is marked as trusted
     */
    public static boolean isTrusted(Context context, String appPackage)
    {
        List<String> trustedApps = getTrustedApps(context);
        return trustedApps.contains(appPackage);
    }

    /**
     * Gets applications
     * @param context The context
     * @param showTrusted Show trusted apps
     * @return The applications list
     */
    private static List<AppInfo> getApplications(Context context, boolean showTrusted)
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
        return filterApplications(context, list, showTrusted);
    }

    /**
     * Filter the application list for trusted apps
     * @param context The context
     * @param list The source list
     * @param showTrusted Show trusted
     * @return The filtered list
     */
    private static List<AppInfo> filterApplications(Context context, List<AppInfo> list, boolean showTrusted)
    {
        List<AppInfo> listFiltered = new ArrayList<AppInfo>();
        List<String> trustedApps = getTrustedApps(context);
        for (AppInfo app : list)
        {
            if (!trustedApps.contains(app.getPackageName()))
            {
                listFiltered.add(app);
            }
            else
            {
                if (showTrusted)
                {
                    app.setTrusted(true);
                    listFiltered.add(app);
                }
            }
        }
        return listFiltered;
    }

    /**
     * Gets trusted apps
     * @param context The context
     * @return The list
     */
    private static List<String> getTrustedApps(Context context)
    {
        if (mTrustedApps == null)
        {
            mTrustedApps = loadTrustedPackageList(context);
        }
        return mTrustedApps;
    }

    /**
     * Load trusted apps from the preferences
     * @param context The context
     * @return The list
     */
    private static List<String> loadTrustedPackageList(Context context)
    {
        List<String> trustedPackages = new ArrayList<String>();
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        int count = prefs.getInt(Constants.KEY_TRUSTED_COUNT, 0);
        for (int i = 0; i < count; i++)
        {
            String trusted = prefs.getString(Constants.KEY_TRUSTED_APP + i, "");
            trustedPackages.add(trusted);
        }
        log("loadTrustedPackageList : ", trustedPackages);
        return trustedPackages;
    }

    /**
     * Save trusted apps in the preferences
     * @param context The context
     * @param trustedApps The trusted apps list
     */
    private static void saveTrustedApps(Context context, List<String> trustedApps)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(Constants.KEY_TRUSTED_COUNT, trustedApps.size());
        for (int i = 0; i < trustedApps.size(); i++)
        {
            editor.putString(Constants.KEY_TRUSTED_APP + i, trustedApps.get(i));
        }
        editor.commit();
        mTrustedApps = trustedApps;
        log("saveTrustedApps : ", trustedApps);
    }

    /**
     * Score calculation
     * @param packageName The app package name
     * @param pm The package manager
     * @return The score
     */
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
                    try
                    {
                        PermissionInfo pi = pm.getPermissionInfo(permission, PackageManager.GET_PERMISSIONS);
                        score += (pi.protectionLevel != PermissionInfo.PROTECTION_NORMAL) ? 100 : 1;
                    }
                    catch (NameNotFoundException ex)
                    {
                        Log.e(TAG, "Permission name not found : " + permission );
                    }
                }
            }
        }
        catch (NameNotFoundException ex)
        {
            Log.e(TAG, "Error getting package info : " + packageName );
        }
        return score;
    }

    /**
     * Gets the group
     * @param list The group list
     * @param name The group name
     * @return The Group
     */
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

    /**
     * Log formatter
     * @param text The message
     * @param trustedPackages The list of trusted app packages
     */
    private static void log(String text, List<String> trustedPackages)
    {
        for (String trusted : trustedPackages)
        {
            Log.d(TAG, text + trusted);
        }
    }

    /**
     * Name comparator
     */
    private static class NameComparator implements Comparator<AppInfo>
    {

        public int compare(AppInfo app1, AppInfo app2)
        {
            return app1.getName().compareToIgnoreCase(app2.getName());
        }
    }

    /**
     * Score comparator
     */
    private static class ScoreComparator implements Comparator<AppInfo>
    {

        public int compare(AppInfo app1, AppInfo app2)
        {
            return (app1.getScore() - app2.getScore());
        }
    }
}
