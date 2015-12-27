package com.sharkozp.galleryapplication.system;

import java.util.LinkedList;

/**
 * Created by oleksandr on 12/26/15.
 */
public interface AsyncResponse {
    void processFinish(LinkedList<String> images);
}
