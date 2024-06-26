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
package org.apache.pdfbox.cos;

import java.io.IOException;

/**
 * This class represents a PDF object.
 *
 * @author Ben Litchfield
 * 
 */
public class COSObject extends COSBase implements COSUpdateInfo
{
    private COSBase baseObject;
    private long objectNumber;
    private int generationNumber;
    private boolean needToBeUpdated;
    private boolean dereferencingInProgress = false;
    
    /**
     * Constructor.
     *
     * @param object The object that this encapsulates.
     *
     * @throws IOException If there is an error with the object passed in.
     */
    public COSObject( COSBase object ) throws IOException
    {
        setObject( object );
    }

    /**
     * This will get the dictionary object in this object that has the name key and
     * if it is a pdfobjref then it will dereference that and return it.
     *
     * @param key The key to the value that we are searching for.
     *
     * @return The pdf object that matches the key.
     * 
     * @deprecated This will be removed in 3.0. Call {@link #getObject()} to find out if it has the
     * correct type and then call {@link #getDictionaryObject(org.apache.pdfbox.cos.COSName)}.
     */
    @Deprecated
    public COSBase getDictionaryObject( COSName key )
    {
        COSBase retval =null;
        if( baseObject instanceof COSDictionary )
        {
            retval = ((COSDictionary)baseObject).getDictionaryObject( key );
        }
        return retval;
    }

    /**
     * This will get the dictionary object in this object that has the name key.
     *
     * @param key The key to the value that we are searching for.
     *
     * @return The pdf object that matches the key.
     * 
     * @deprecated This will be removed in 3.0. Call {@link #getObject()} to find out if it has the
     * correct type and then call {@link #getDictionaryObject(org.apache.pdfbox.cos.COSName)}.
     */
    @Deprecated
    public COSBase getItem( COSName key )
    {
        COSBase retval =null;
        if( baseObject instanceof COSDictionary )
        {
            retval = ((COSDictionary)baseObject).getItem( key );
        }
        return retval;
    }

    /**
     * This will get the object that this object encapsulates.
     *
     * @return The encapsulated object.
     */
    public COSBase getObject()
    {
        return baseObject;
    }

    /**
     * This will set the object that this object encapsulates.
     *
     * @param object The new object to encapsulate.
     *
     * @throws IOException If there is an error setting the updated object.
     */
    public final void setObject( COSBase object ) throws IOException
    {
        baseObject = object;
    }

    /**
     * Indicates that the dereferencing of the represented indirect object is in progress. It is used to detect a
     * possible recursion which most likely ends up in a stack overflow.
     * 
     * @return true if dereferencing is in progress.
     */
    public boolean derefencingInProgress()
    {
        return dereferencingInProgress;
    }

    /**
     * Start dereferencing the represented indirect object.
     */
    public void dereferencingStarted()
    {
        dereferencingInProgress = true;
    }

    /**
     * Dereferencing of the represented indirect object is finished.
     */
    public void dereferencingFinished()
    {
        dereferencingInProgress = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "COSObject{" + objectNumber + ", " + generationNumber + "}";
    }

    /** 
     * Getter for property objectNumber.
     * @return Value of property objectNumber.
     */
    public long getObjectNumber()
    {
        return objectNumber;
    }

    /** 
     * Setter for property objectNumber.
     * @param objectNum New value of property objectNumber.
     */
    public void setObjectNumber(long objectNum)
    {
        objectNumber = objectNum;
    }

    /** 
     * Getter for property generationNumber.
     * @return Value of property generationNumber.
     */
    public int getGenerationNumber()
    {
        return generationNumber;
    }

    /** 
     * Setter for property generationNumber.
     * @param generationNumberValue New value of property generationNumber.
     */
    public void setGenerationNumber(int generationNumberValue)
    {
        generationNumber = generationNumberValue;
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws IOException If an error occurs while visiting this object.
     */
    @Override
    public Object accept( ICOSVisitor visitor ) throws IOException
    {
        COSBase object = getObject();
        return object != null ? object.accept(visitor) : COSNull.NULL.accept(visitor);
    }
    
    /**
     * Get the update state for the COSWriter.
     * 
     * @return the update state.
     */
    @Override
    public boolean isNeedToBeUpdated() 
    {
        return needToBeUpdated;
    }
    
    /**
     * Set the update state of the dictionary for the COSWriter.
     * 
     * @param flag the update state.
     */
    @Override
    public void setNeedToBeUpdated(boolean flag) 
    {
        needToBeUpdated = flag;
    }

}
