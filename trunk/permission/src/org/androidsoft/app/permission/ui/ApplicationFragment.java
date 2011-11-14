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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import org.androidsoft.app.permission.R;

/**
 *
 * @author pierre
 */
public class ApplicationFragment extends Fragment
{
    private static final String TAG = "androidsoft";
    private TextView mName;
    private ImageView mIcon;
    private TextView mPackageName;
    private TextView mVersion;
    private ExpandableListView mPermissions;
    
    /**
     * 
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        
        View v = inflater.inflate(R.layout.application_details, container, false);
        
        mName = (TextView) v.findViewById(R.id.name );
        mIcon = (ImageView) v.findViewById(R.id.icon );
        mPackageName = (TextView) v.findViewById(R.id.package_name );
        mVersion = (TextView) v.findViewById(R.id.version );
        mPermissions = (ExpandableListView) v.findViewById(R.id.permissions_list);        
        return v;
    }
    
    void updateApplication( Activity activity , String packageName)
    {
        try
        {
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS );
            mName.setText(pi.applicationInfo.loadLabel(pm).toString());
            mIcon.setImageDrawable( pi.applicationInfo.loadIcon(pm));
            mPackageName.setText(packageName);
            mVersion.setText(pi.versionName);
            List<PermissionGroup> listGroups = PermissionService.getPermissions( pi.requestedPermissions , pm );
            PermissionExpandableListAdapter adapter = new PermissionExpandableListAdapter( getActivity(), listGroups );  
            mPermissions.setAdapter(adapter);
            for( int i = 0 ; i < listGroups.size() ; i++ )
            {
                mPermissions.expandGroup(i);
            }
        }
        catch (NameNotFoundException ex)
        {
            Log.e( TAG , "Package name not found : " + packageName );
        }
    }
    
}
