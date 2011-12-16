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

import java.util.ArrayList;
import java.util.List;

/**
 * Application Changes Service
 * @author Pierre Levy
 */
public class ApplicationChangesService
{

    private static List<ApplicationChangesListener> mListListeners = new ArrayList<ApplicationChangesListener>();

    /**
     * Register a listener
     * @param listener The listener
     */
    public static synchronized void registerListener(ApplicationChangesListener listener)
    {
        if( !mListListeners.contains(listener))
        {
            mListListeners.add(listener);
        }
    }
    
    /**
     * Notify all listeners
     */
    public static synchronized void notifyListeners( )
    {
        for( ApplicationChangesListener listener : mListListeners )
        {
            listener.onApplicationChange();
        }
    }
    
    
}
