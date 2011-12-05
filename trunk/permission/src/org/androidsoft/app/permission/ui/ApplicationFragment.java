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

import org.androidsoft.app.permission.ui.widget.PermissionExpandableListAdapter;
import org.androidsoft.app.permission.model.PermissionGroup;
import org.androidsoft.app.permission.service.PermissionService;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import org.androidsoft.app.permission.R;

/**
 * Application Fragment
 * @author Pierre Levy
 */
public class ApplicationFragment extends Fragment implements View.OnClickListener
{

    private static final String TAG = "androidsoft";
    private TextView mName;
    private ImageView mIcon;
    private TextView mTvPackageName;
    private TextView mTvVersion;
    private ExpandableListView mPermissions;
    private LinearLayout mApplicationLayout;
    private LinearLayout mNoPermissionLayout;
    private Button mButtonOpen;
    private Button mButtonUninstall;
    private Button mButtonMarket;
    private TextView mTvMessageNoApplication;
    private Activity mActivity;
    private String mPackageName;

    /**
     * {@inheritDoc }
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {

        View v = inflater.inflate(R.layout.application_details, container, false);

        mApplicationLayout = (LinearLayout) v.findViewById(R.id.layout_application);
        mApplicationLayout.setVisibility(View.GONE);
        mName = (TextView) v.findViewById(R.id.name);
        mIcon = (ImageView) v.findViewById(R.id.icon);
        mTvPackageName = (TextView) v.findViewById(R.id.package_name);
        mTvVersion = (TextView) v.findViewById(R.id.version);
        mButtonOpen = (Button) v.findViewById(R.id.button_open);
        mButtonUninstall = (Button) v.findViewById(R.id.button_uninstall);
        mPermissions = (ExpandableListView) v.findViewById(R.id.permissions_list);
        mNoPermissionLayout = (LinearLayout) v.findViewById(R.id.layout_no_permission);
        mButtonMarket = (Button) v.findViewById(R.id.button_market);
        mButtonOpen.setOnClickListener(this);
        mButtonUninstall.setOnClickListener(this);
        mButtonMarket.setOnClickListener(this);
        mTvMessageNoApplication = (TextView) v.findViewById(R.id.message_no_application);
        return v;
    }

    void updateApplication(Activity activity, String packageName)
    {
        try
        {
            mActivity = activity;
            mPackageName = packageName;
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            mName.setText(pi.applicationInfo.loadLabel(pm).toString());
            mIcon.setImageDrawable(pi.applicationInfo.loadIcon(pm));
            mTvPackageName.setText(packageName);
            mTvVersion.setText(pi.versionName);
            List<PermissionGroup> listGroups = PermissionService.getPermissions(pi.requestedPermissions, pm);
            PermissionExpandableListAdapter adapter = new PermissionExpandableListAdapter(getActivity(), listGroups);
            mPermissions.setAdapter(adapter);
            for (int i = 0; i < listGroups.size(); i++)
            {
                mPermissions.expandGroup(i);
            }
            if (listGroups.isEmpty())
            {
                mNoPermissionLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                mNoPermissionLayout.setVisibility(View.GONE);
            }
            mTvMessageNoApplication.setVisibility(View.GONE);
            mApplicationLayout.setVisibility(View.VISIBLE);
        }
        catch (NameNotFoundException ex)
        {
            Log.e(TAG, "Package name not found : " + packageName);
        }
    }

    /**
     * {@inheritDoc }
     */
    public void onClick(View view)
    {
        if (view == mButtonOpen)
        {
            open();
        }
        else if (view == mButtonUninstall)
        {
            uninstall();
        }
        else if (view == mButtonMarket)
        {
            openMarket();
        }
    }

    /**
     * Open the application
     */
    private void open()
    {
        Intent intentOpen = new Intent(Intent.ACTION_MAIN);
        PackageManager pm = mActivity.getPackageManager();
        intentOpen = pm.getLaunchIntentForPackage(mPackageName);
        if( intentOpen != null )
        {
            intentOpen.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intentOpen);
        }
        else
        {
            Toast.makeText(mActivity, getString( R.string.message_error_open), Toast.LENGTH_LONG ).show();
        }
    }

    /**
     * Uninstall the application
     */
    private void uninstall()
    {
        String uri = "package:" + mPackageName;
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.parse(uri));
        startActivity(uninstallIntent);
    }

    /**
     * Open the market
     */
    private void openMarket()
    {
        String uri = "market://details?id=" + mPackageName;
        Intent intentGoToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if( intentGoToMarket != null )
        {
            startActivity(intentGoToMarket);
        }
        else
        {
            Toast.makeText(mActivity, getString( R.string.message_error_market ), Toast.LENGTH_LONG ).show();
        }
    }
}
