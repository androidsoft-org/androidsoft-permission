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
package org.androidsoft.app.permission.ui;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import org.androidsoft.app.permission.R;
import org.androidsoft.utils.res.ResourceUtils;
import org.androidsoft.utils.res.ResourceImageGetter;
import org.androidsoft.utils.ui.BasicActivity;

/**
 * Help activity
 * @author Pierre Levy
 */
public class HelpActivity extends BasicActivity
{
    @Override
    public void onCreate(Bundle icicle)
    {
    
        super.onCreate(icicle);

        setContentView(R.layout.help);
        
        TextView tv = (TextView) findViewById(R.id.help);
        String asset = getString( R.string.asset_help );

        String help = ResourceUtils.readAssetTextFile(this, asset );
        tv.setText( Html.fromHtml( help , new ResourceImageGetter( this ) , null ));
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public int getMenuResource()
    {
        return R.menu.menu_close;
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public int getMenuCloseId()
    {
        return R.id.menu_close;
    }
    
   
}
