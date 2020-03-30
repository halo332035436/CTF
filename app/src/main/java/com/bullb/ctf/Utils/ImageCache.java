package com.bullb.ctf.Utils;

import android.util.Log;
import android.util.LruCache;


public class ImageCache {
    private static LruCache<String, byte[]> mMemoryCache;
    private static ImageCache imageCache;
    public static final String PROFILE = "profile";


    private ImageCache(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        Log.d("cache size", String.valueOf(cacheSize));

        mMemoryCache = new LruCache<String, byte[]>(cacheSize) {
            @Override
            protected int sizeOf(String key, byte[] bytes) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bytes.length / 1024;
            }
        };
    }

    public static synchronized ImageCache getInstance(){
        if(imageCache == null){
            imageCache = new ImageCache();
        }
        return imageCache;
    }


    public void addBitmapToMemoryCache(String key, byte[] bytes) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bytes);
        }else{
            mMemoryCache.remove(key);
            mMemoryCache.put(key, bytes);
        }
    }

    public byte[] getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


}
