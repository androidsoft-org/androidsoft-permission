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
package org.androidsoft.app.permission.ui.widget;

import org.androidsoft.app.permission.model.AppInfo;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import java.util.List;
import org.androidsoft.app.permission.R;

/**
 *
 * @author pierre
 */
public class ApplicationAdapter implements ListAdapter
{

    private List<AppInfo> mList;
    private Activity mActivity;
    private Context mContext;

    /**
     * Constructor
     * @param activity The activity
     * @param list The apps list
     */
    public ApplicationAdapter(Activity activity, List<AppInfo> list)
    {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mList = list;

    }

    /**
     * {@inheritDoc }
     */
    public void registerDataSetObserver(DataSetObserver arg0)
    {
    }

    /**
     * {@inheritDoc }
     */
    public void unregisterDataSetObserver(DataSetObserver arg0)
    {
    }

    /**
     * {@inheritDoc }
     */
    public int getCount()
    {
        return mList.size();
    }

    /**
     * {@inheritDoc }
     */
    public Object getItem(int position)
    {
        return mList.get(position);
    }

    /**
     * {@inheritDoc }
     */
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * {@inheritDoc }
     */
    public boolean hasStableIds()
    {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final AppInfo info = mList.get(position);

        if (convertView == null)
        {
            final LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.application_list_item, parent, false);
        }

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        imageView.setImageDrawable(info.getIcon());

        final TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setText(info.getName());

        final TextView tvVersion = (TextView) convertView.findViewById(R.id.version);
        tvVersion.setText(info.getVersion());

        final TextView tvScore = (TextView) convertView.findViewById(R.id.score);
        tvScore.setText("Score : " + info.getScore());

        final ImageView imageScore = (ImageView) convertView.findViewById(R.id.score_icon);
        if (info.getScore() == 0)
        {
            imageScore.setImageResource(R.drawable.no_permission);
        }
        else if (info.getScore() < 5)
        {
            imageScore.setImageResource(R.drawable.normal);
        }
        else
        {
            imageScore.setImageResource(R.drawable.dangerous);
        }
        return convertView;
    }

    /**
     * {@inheritDoc }
     */
    public int getItemViewType(int arg0)
    {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    public int getViewTypeCount()
    {
        return 1;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isEmpty()
    {
        return mList.isEmpty();
    }

    /**
     * {@inheritDoc }
     */
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isEnabled(int arg0)
    {
        return true;
    }
}
