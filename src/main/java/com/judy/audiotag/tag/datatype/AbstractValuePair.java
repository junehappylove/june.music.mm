/**
 * @author : Paul Taylor
 * <p/>
 * Version @version:$Id: AbstractValuePair.java,v 1.5 2007/11/23 14:35:42 paultaylor Exp $
 * <p/>
 * Jaudiotagger Copyright (C)2004,2005
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 * you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p/>
 * Description:
 */
package com.judy.audiotag.tag.datatype;

import java.io.Serializable;
import java.util.*;

/**
 * A two way mapping between an id and a value
 */
public abstract class AbstractValuePair<K extends Serializable>
{
    protected final Map<K, String> idToValue = new LinkedHashMap<K, String>();
    protected final Map<Object, Object> valueToId = new LinkedHashMap<Object, Object>();
    protected final List<String> valueList = new ArrayList<String>();

    protected Iterator<K> iterator = idToValue.keySet().iterator();

    protected String value;

    /**
     * Get list in alphabetical order
     */
    public List<String> getAlphabeticalValueList()
    {
        return valueList;
    }

    public Map<K, String> getIdToValueMap()
    {
        return idToValue;
    }

    public Map<Object, Object> getValueToIdMap()
    {
        return valueToId;
    }

    /**
     *
     * @return the number of elements in the mapping
     */
    public int getSize()
    {
        return valueList.size();
    }
}
