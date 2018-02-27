package com.ap.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Status implements Serializable {
    private static final long serialVersionUID = -3009157732242241606L;
    @Id
    @Column(name = "KEY")
    private String key;

    @Column(name = "VALUE", length = 20000)
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}