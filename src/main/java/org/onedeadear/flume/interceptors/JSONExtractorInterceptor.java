package org.onedeadear.flume.interceptors;

import java.util.List;

import org.json.*;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import org.apache.log4j.Logger;

public class JSONExtractorInterceptor implements Interceptor {

    private static final Logger LOG = Logger.getLogger(JSONExtractorInterceptor.class);
    
    private String _jsonProperty;
    private OnError _onError;
    
    public JSONExtractorInterceptor(String jsonProperty, OnError onError) {
        this._jsonProperty = jsonProperty;

        this._onError = onError;
    }

    @Override
    public void initialize() {}

    @Override
    public Event intercept(Event event) {
        try {
            // Get the property from the body
            String body = new String(event.getBody());
            JSONObject obj = new JSONObject(body);
            String extraction = obj.getString(_jsonProperty);

            event.setBody(extraction.getBytes("UTF-8"));
        } catch (Exception e) {
            LOG.warn(e);

            if (_onError == OnError.DropEvent)
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

    public enum OnError{
        Continue,
        DropEvent
    }

    public static class Builder implements Interceptor.Builder {

        private String _jsonProperty;
        private OnError _onError;

        @Override
        public Interceptor build() {
            return new JSONExtractorInterceptor(_jsonProperty, _onError);
        }

        @Override
        public void configure(Context context) {
            // Retrieve property from flume agent configuration
            _jsonProperty = context.getString("jsonProperty");

            String onError = context.getString("onError");
            _onError = onError != null && !onError.isEmpty() ? OnError.valueOf(onError) : OnError.Continue;
        }
    }
}
