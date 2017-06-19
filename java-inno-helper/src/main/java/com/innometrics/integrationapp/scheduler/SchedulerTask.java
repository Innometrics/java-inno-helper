package com.innometrics.integrationapp.scheduler;

/**
 * Created by Mikhail Kolpakov on 13.06.17.
 */
public class SchedulerTask {
    String endpoint;// required
    String method; // required
    String id;
    String payload;
    Long delay;
    Long timestamp;
    long created;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public boolean validate() throws SchedulerTaskException {
        if (endpoint == null || endpoint.isEmpty() || method == null || method.isEmpty()) {
            throw new SchedulerTaskException("\"endpoint\" and \"method\" Should not be empty");
        }
        if (timestamp != null && delay != null) {
            throw new SchedulerTaskException("You should use only one param: timestamp or delay'");
        }
        return true;
    }
}
