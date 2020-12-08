package com.kode.menklik.data;

import com.orm.SugarRecord;

/**
 * Created by Ermano N. Joseph
 * on 11/28/2017.
 */

public class Script extends SugarRecord<Script> {
    private String label;
    private String description;
    private String source;
    private String datecreated;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    @Override
    public String toString(){
        return label;
    }
}
