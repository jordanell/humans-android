package com.humansapp.humans.models;

import java.util.Map;

/**
 * Created by jordan on 2014-08-14.
 */
public class Model {
    protected Map model;

    public Model(Map model) {
        this.model = model;
    }

    public Long getId() {
        return (Long) model.get("id");
    }

    public Object get(String key) {
        return model.get(key);
    }
}
