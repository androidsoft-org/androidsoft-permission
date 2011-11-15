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

import org.androidsoft.app.permission.ui.widget.ApplicationAdapter;
import org.androidsoft.app.permission.model.AppInfo;
import org.androidsoft.app.permission.service.PermissionService;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;
import java.util.List;

/**
 *
 * @author pierre
 */
public class ApplicationsListFragment extends ListFragment
{

    private AppListEventsCallback mContainerCallback;
    private Activity mActivity;

    void update(List<AppInfo> listApplications )
    {
        fillData( listApplications );
    }

    /**
     * Interface 
     */
    public interface AppListEventsCallback
    {

        /**
         * Callback
         * @param packageName The package
         */
        public void onAppSelected( String packageName );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        fillData( PermissionService.getApplicationsSortedByScore(mActivity , false ));
        registerForContextMenu(getListView());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            // check that the containing activity implements our callback
            mContainerCallback = (AppListEventsCallback) activity;
            mActivity= activity;
        }
        catch (ClassCastException e)
        {
            activity.finish();
            throw new ClassCastException(activity.toString()
                    + " must implement AppListEventsCallback");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Adapter adapter = l.getAdapter();
        AppInfo app = (AppInfo) adapter.getItem(position);
        mContainerCallback.onAppSelected( app.getPackageName() );
    }
    
    private void fillData( List<AppInfo> listApps )
    {
        ApplicationAdapter apps = new ApplicationAdapter(getActivity(), listApps );
        setListAdapter(apps);

    }
    
}
