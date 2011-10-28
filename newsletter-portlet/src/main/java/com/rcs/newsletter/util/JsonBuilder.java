package com.rcs.newsletter.util;

import com.google.gson.Gson;

/**
 * Builder that create the Json Response to be used in the front end views
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class JsonBuilder {

    private String message;
    private boolean success;    

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }
}
