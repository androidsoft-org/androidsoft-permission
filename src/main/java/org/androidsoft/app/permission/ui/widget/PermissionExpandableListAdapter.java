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
package org.androidsoft.app.permission.ui.widget;

import org.androidsoft.app.permission.model.PermissionGroup;
import org.androidsoft.app.permission.model.Permission;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import org.androidsoft.app.permission.R;

/**
 * Permission ExpandableList Adapter
 * @author Pierre Levy
 */
public class PermissionExpandableListAdapter extends BaseExpandableListAdapter 
{
    private Activity mActivity;
    private List<PermissionGroup> mGroupsList;

    /**
     * Constructor
     * @param activity
     * @param groupsList
     */
    public PermissionExpandableListAdapter( Activity activity , List<PermissionGroup> groupsList )
    {
        mActivity = activity;
        mGroupsList = groupsList;
    }
            
    /**
     * {@inheritDoc }
     */
    public int getGroupCount()
    {
        return mGroupsList.size();
    }

    /**
     * {@inheritDoc }
     */
    public int getChildrenCount(int position)
    {
        return mGroupsList.get(position).getListPermissions().size();
    }

    /**
     * {@inheritDoc }
     */
    public Object getGroup(int position)
    {
        return mGroupsList.get(position);
    }

    /**
     * {@inheritDoc }
     */
    public Object getChild(int positionGroup, int positionChild)
    {
        return mGroupsList.get(positionGroup).getListPermissions().get( positionChild);
    }

    /**
     * {@inheritDoc }
     */
    public long getGroupId(int position)
    {
        return position;
    }

    /**
     * {@inheritDoc }
     */
    public long getChildId(int arg0, int arg1)
    {
        return 1000*arg0 + arg1;
    }

    /**
     * {@inheritDoc }
     */
    public boolean hasStableIds()
    {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    public View getGroupView(int position, boolean arg1, View convertView, ViewGroup parent)
    {
        final PermissionGroup group = mGroupsList.get(position);

        if (convertView == null)
        {
            final LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.group_list_item, parent, false);
        }

        final TextView tvLabel = (TextView) convertView.findViewById(R.id.group_label);
        tvLabel.setText(group.getLabel());

        return convertView;
    }

    /**
     * {@inheritDoc }
     */
    public View getChildView(int positionGroup, int positionChild, boolean arg2, View convertView, ViewGroup parent)
    {
        final Permission permission = mGroupsList.get(positionGroup).getListPermissions().get(positionChild);

        if (convertView == null)
        {
            final LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.permission_list_item, parent, false);
        }

        int nRes = ( permission.isDangerous() ) ? R.drawable.dangerous : R.drawable.normal;
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon_danger);
        imageView.setImageResource(nRes);

        final TextView tvLabel = (TextView) convertView.findViewById(R.id.permission_label);
        tvLabel.setText( permission.getLabel());

        final TextView tvName = (TextView) convertView.findViewById(R.id.permission_name);
        tvName.setText( permission.getName());

        final TextView tvDescription = (TextView) convertView.findViewById(R.id.permission_description);
        tvDescription.setText( permission.getDescription());

        return convertView;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isChildSelectable(int arg0, int arg1)
    {
        return true;
    }
    
}
