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

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.androidsoft.app.permission.R;

/**
 *
 * @author pierre
 */
public class HelpActivity extends Activity
{
    @Override
    public void onCreate(Bundle icicle)
    {
    
        super.onCreate(icicle);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.help);
        
        TextView tv = (TextView) findViewById(R.id.help);
        tv.setText( Html.fromHtml(readHelp().toString()));
    }
    
    private CharSequence readHelp()
    {
        BufferedReader in = null;
        try
        {
            String asset = getString( R.string.help_asset );
            in = new BufferedReader(new InputStreamReader(getAssets().open( asset )));
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = in.readLine()) != null)
            {
                buffer.append(line).append('\n');
            }
            return buffer;
        } catch (IOException e)
        {
            return "";
        } finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    Log.e("ARTags", "Error closing input stream while reading asset", e );
                }
            }
        }
    }

}
