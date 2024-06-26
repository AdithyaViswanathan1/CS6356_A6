/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDStructureElementNameTreeNode;
import org.apache.pdfbox.pdmodel.common.COSDictionaryMap;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;

/**
 * A root of a structure tree.
 * 
 * @author Ben Litchfield
 * @author Johannes Koch
 * 
 */
public class PDStructureTreeRoot extends PDStructureNode
{

    /**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(PDStructureTreeRoot.class);

    private static final String TYPE = "StructTreeRoot";

    /**
     * Default Constructor.
     * 
     */
    public PDStructureTreeRoot()
    {
        super(TYPE);
    }

    /**
     * Constructor for an existing structure element.
     * 
     * @param dic The existing dictionary.
     */
    public PDStructureTreeRoot(COSDictionary dic)
    {
        super(dic);
    }

    /**
     * Returns the K array entry.
     * 
     * @return the K array entry
     *
     * @deprecated use {@link #getK()} only. /K can be a dictionary or an array, and the next level
     * can also be a dictionary. See file 054080.pdf in PDFBOX-4417 and read "Entries in the
     * structure tree root" in the PDF specification.
     */
    @Deprecated
    public COSArray getKArray()
    {
        COSBase k = this.getCOSObject().getDictionaryObject(COSName.K);
        if (k instanceof COSDictionary)
        {
            COSDictionary kdict = (COSDictionary) k;
            k = kdict.getDictionaryObject(COSName.K);
            if (k instanceof COSArray)
            {
                return (COSArray) k;
            }
        }
        else if (k instanceof COSArray)
        {
            return (COSArray) k;
        }

        return null;
    }

    /**
     * Returns the K entry. This can be a dictionary representing a structure element, or an array
     * of them.
     *
     * @return the K entry.
     */
    public COSBase getK()
    {
        return this.getCOSObject().getDictionaryObject(COSName.K);
    }

    /**
     * Sets the K entry.
     * 
     * @param k the K value
     */
    public void setK(COSBase k)
    {
        this.getCOSObject().setItem(COSName.K, k);
    }

    /**
     * Returns the ID tree.
     * 
     * @return the ID tree
     */
    public PDNameTreeNode<PDStructureElement> getIDTree()
    {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.ID_TREE);
        if (base instanceof COSDictionary)
        {
            return new PDStructureElementNameTreeNode((COSDictionary) base);
        }
        return null;
    }

    /**
     * Sets the ID tree.
     * 
     * @param idTree the ID tree
     */
    public void setIDTree(PDNameTreeNode<PDStructureElement> idTree)
    {
        this.getCOSObject().setItem(COSName.ID_TREE, idTree);
    }

    /**
     * Returns the parent tree.
     * 
     * @return the parent tree
     */
    public PDNumberTreeNode getParentTree()
    {
        COSBase base = getCOSObject().getDictionaryObject(COSName.PARENT_TREE);
        if (base instanceof COSDictionary)
        {
            return new PDNumberTreeNode((COSDictionary) base, PDParentTreeValue.class);
        }
        return null;
    }

    /**
     * Sets the parent tree.
     * 
     * @param parentTree the parent tree
     */
    public void setParentTree(PDNumberTreeNode parentTree)
    {
        this.getCOSObject().setItem(COSName.PARENT_TREE, parentTree);
    }

    /**
     * Returns the next key in the parent tree.
     * 
     * @return the next key in the parent tree
     */
    public int getParentTreeNextKey()
    {
        return this.getCOSObject().getInt(COSName.PARENT_TREE_NEXT_KEY);
    }

    /**
     * Sets the next key in the parent tree.
     * 
     * @param parentTreeNextkey the next key in the parent tree.
     */
    public void setParentTreeNextKey(int parentTreeNextkey)
    {
        this.getCOSObject().setInt(COSName.PARENT_TREE_NEXT_KEY, parentTreeNextkey);
    }

    /**
     * Returns the role map.
     * 
     * @return the role map
     */
    public Map<String, Object> getRoleMap()
    {
        COSBase rm = this.getCOSObject().getDictionaryObject(COSName.ROLE_MAP);
        if (rm instanceof COSDictionary)
        {
            try
            {
                return COSDictionaryMap.convertBasicTypesToMap((COSDictionary) rm);
            }
            catch (IOException e)
            {
                LOG.error(e,e);
            }
        }
        return new HashMap<String, Object>();
    }

    /**
     * Sets the role map.
     * 
     * @param roleMap the role map
     */
    public void setRoleMap(Map<String, String> roleMap)
    {
        COSDictionary rmDic = new COSDictionary();
        for (Map.Entry<String, String> entry : roleMap.entrySet())
        {
            rmDic.setName(entry.getKey(), entry.getValue());
        }
        this.getCOSObject().setItem(COSName.ROLE_MAP, rmDic);
    }

    /**
     * Sets the ClassMap.
     * 
     * @return the ClassMap, never null. The elements are either {@link PDAttributeObject} or lists
     * of it.
     */
    public Map<String, Object> getClassMap()
    {
        Map<String, Object> classMap = new HashMap<String, Object>();
        COSDictionary classMapDictionary = this.getCOSObject().getCOSDictionary(COSName.CLASS_MAP);
        if (classMapDictionary == null)
        {
            return classMap;
        }
        for (Map.Entry<COSName,COSBase> entry : classMapDictionary.entrySet())
        {
            COSName name = entry.getKey();
            COSBase base = entry.getValue();
            if (base instanceof COSObject)
            {
                base = ((COSObject) base).getObject();
            }
            if (base instanceof COSDictionary)
            {
                classMap.put(name.getName(), PDAttributeObject.create((COSDictionary) base));
            }
            else if (base instanceof COSArray)
            {
                COSArray array = (COSArray) base;
                List<PDAttributeObject> list = new ArrayList<PDAttributeObject>();
                for (int i = 0; i < array.size(); ++i)
                {
                    COSBase base2 = array.getObject(i);
                    if (base2 instanceof COSDictionary)
                    {
                        list.add(PDAttributeObject.create((COSDictionary) base2));
                    }
                }
                classMap.put(name.getName(), list);
            }
        }
        return classMap;
    }

    /**
     * Sets the ClassMap.
     * 
     * @param classMap null, or a map whose elements are either {@link PDAttributeObject} or lists
     * of it.
     */
    public void setClassMap(Map<String, Object> classMap)
    {
        if (classMap == null || classMap.isEmpty())
        {
            this.getCOSObject().removeItem(COSName.CLASS_MAP);
            return;
        }
        COSDictionary classMapDictionary = new COSDictionary();
        for (Map.Entry<String,Object> entry : classMap.entrySet())
        {
            String name = entry.getKey();
            Object object = entry.getValue();
            if (object instanceof PDAttributeObject)
            {
                classMapDictionary.setItem(name, ((PDAttributeObject) object).getCOSObject());
            }
            else if (object instanceof List)
            {
                List<PDAttributeObject> list = (List<PDAttributeObject>) object;
                COSArray array = new COSArray();
                for (PDAttributeObject attributeObject : list)
                {
                    array.add(attributeObject.getCOSObject());
                }
                classMapDictionary.setItem(name, array);
            }
        }
        this.getCOSObject().setItem(COSName.CLASS_MAP, classMapDictionary);        
    }
}
