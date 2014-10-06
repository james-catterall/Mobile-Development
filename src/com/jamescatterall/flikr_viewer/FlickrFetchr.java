package com.jamescatterall.flikr_viewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.util.Log;

public class FlickrFetchr 
{
    public static final String TAG = "PhotoFetcher";

    public static final String PREF_SEARCH_QUERY = "searchQuery";
    public static final String PREF_LAST_RESULT_ID = "lastResultId";

    private static final String ENDPOINT = "http://api.flickr.com/services/rest/";
    private static final String API_KEY = "69ac1bd42546a9c10aa50ab9f24c430c";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final String PARAM_EXTRAS = "extras";
    private static final String PARAM_TEXT = "text";

    private static final String EXTRA_SMALL_URL = "url_s";

    private static final String XML_PHOTO = "photo";

    public byte[] getUrlBytes(String urlSpec) throws IOException
    {
        URL url = new URL(urlSpec); //Define url as a new URL.
        HttpURLConnection connection = (HttpURLConnection)url.openConnection(); //Defining the connection.

        try 
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) //Check if connection is not working.
            {
                return null;
            }

            int bytesRead = 0; 
            byte[] buffer = new byte[1024];
            
            while ((bytesRead = in.read(buffer)) > 0)  //Loops until bytesread is greater than 0.
            {
                out.write(buffer, 0, bytesRead); //Reads until connection runs out of data.
            }
            out.close();
            return out.toByteArray();
        } 
        
        finally 
        {
            connection.disconnect(); //Disconnects the connection.
        }
    }
    
    
    String getUrl(String urlSpec) throws IOException 
    {
        return new String(getUrlBytes(urlSpec));
    }
    
    public ArrayList<GalleryItem> fetchItems() 
    {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .build().toString();
        return downloadGalleryItems(url);
    }
    
    public ArrayList<GalleryItem> downloadGalleryItems(String  url) 
    {
        ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
        
        try 
        {
            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml: " + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            
            parseItems(items, parser);
        } 
        catch (IOException ioe) 
        {
            Log.e(TAG, "Failed to fetch items", ioe);
        } 
        catch (XmlPullParserException xppe) 
        {
            Log.e(TAG, "Failed to parse items", xppe);
        }
        return items;
    }

    
    public ArrayList<GalleryItem> search(String query) 
    {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_TEXT, query)
                .build().toString();
        return downloadGalleryItems(url);
    }

    
    void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException 
    {
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) 
        {
            if (eventType == XmlPullParser.START_TAG &&
                XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);

                GalleryItem item = new GalleryItem();
                item.setId(id);
                item.setCaption(caption);
                item.setUrl(smallUrl);
                items.add(item);
            }

            eventType = parser.next();
        }
    }
}