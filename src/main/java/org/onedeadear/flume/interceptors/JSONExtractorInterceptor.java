package org.onedeadear.flume.interceptors;

import java.util.List;

import org.json.*;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import org.apache.log4j.Logger;

public class JSONExtractorInterceptor implements Interceptor {

    private String jsonProperty;

    private static final Logger LOG = Logger.getLogger(JSONExtractorInterceptor.class);

    // private means that only Builder can build me.
    private JSONExtractorInterceptor(String jsonProperty) {
        this.jsonProperty = jsonProperty;
    }

    @Override
    public void initialize() {}

    @Override
    public Event intercept(Event event) {

        // example: change body
        JSONObject obj = new JSONObject(event.getBody());
        String message = obj.getString(jsonProperty);

        try {
            event.setBody(message.getBytes("UTF-8"));
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
            // Retrieve property from flume conf
            jsonProperty = context.getString("jsonProperty");
        }
    }
}