package org.superbiz.arqpersistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//tag::doc[]
@Entity(name = "MyEntity")
public class MyEntity {
    @Id
    @Column
    private String key;

    @Column
    private String value;

    public MyEntity() {}

    public MyEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // Getters and setters omitted for brevity
//end::doc[]
    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
//tag::doc[]
}
//end::doc[]
