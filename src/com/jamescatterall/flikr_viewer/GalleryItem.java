package com.jamescatterall.flikr_viewer;

public class GalleryItem 
{
    private String mCaption; //Define private string for captions.
    private String mId; //Define private string for ids.
    private String mUrl; //Define private string for url.

    public String getCaption() //Returns the caption.
    {
        return mCaption;
    }
    
    public void setCaption(String caption) //Sets the caption.
    {
        mCaption = caption;
    }
    
    public String getId() //Returns the id.
    {
        return mId;
    }
    
    public void setId(String id) //Sets the id.
    {
        mId = id;
    }
    
    public String getUrl() //Returns the url.
    {
        return mUrl;
    }

    public void setUrl(String url) //Sets the url.
    {
        mUrl = url;
    }

    public String toString() //Returns the caption to a string.
    {
        return mCaption;
    }
}
