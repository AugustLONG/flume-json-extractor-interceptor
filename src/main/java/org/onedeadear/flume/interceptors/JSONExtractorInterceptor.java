package org.onedeadear.flume.interceptors;

import java.util.List;

import org.json.*;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import org.apache.log4j.Logger;

public class JSONExtractorInterceptor implements Interceptor {

    private static final Logger LOG = Logger.getLogger(JSONExtractorInterceptor.class);
    
    private String jsonProperty;

    
    public JSONExtractorInterceptor(String jsonProperty) {
        this.jsonProperty = jsonProperty;
    }

    @Override
    public void initialize() {}

    @Override
    public Event intercept(Event event) {

        // Get the property from the body
        String body = new String(event.getBody());
        JSONObject obj = new JSONObject(body);
        String extraction = obj.getString(jsonProperty);

        try {
            event.setBody(extraction.getBytes("UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            LOG.warn(e);
            // drop event completely
            return null;
        }

        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        for (Event event:events) {
            intercept(event);
        }
        return events;
    }

    @Override
    public void close() {}


    public static class Builder implements Interceptor.Builder {

        private String jsonProperty;        

        @Override
        public Interceptor build() {
            return new JSONExtractorInterceptor(jsonProperty);
        }

        @Override
        public void configure(Context context) {
            // Retrieve property from flume agent configuration
            jsonProperty = context.getString("jsonProperty");
        }
    }
}