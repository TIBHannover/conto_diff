package org.webdifftool.client.model;

import java.util.HashMap;
import java.util.Map;

public class SemanticDiff {
    private Map<String, String> provDMs = new HashMap<>();

    private Map<String, String> locations = new HashMap<>();

    private Map<String, String> activities = new HashMap<>();

    private String baseEntity = "";

    private String softwareAgent = "";

    private Map<String, String> sourceEntities = new HashMap<>();

    private Map<String, String> sdiffEntities = new HashMap<>();

    public Map<String, String> getProvDMs() {
        return provDMs;
    }

    public void setProvDMs(Map<String, String> provDMs) {
        this.provDMs = provDMs;
    }

    public Map<String, String> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, String> locations) {
        this.locations = locations;
    }

    public Map<String, String> getActivities() {
        return activities;
    }

    public void setActivities(Map<String, String> activities) {
        this.activities = activities;
    }

    public String getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(String baseEntity) {
        this.baseEntity = baseEntity;
    }

    public String getSoftwareAgent() {
        return softwareAgent;
    }

    public void setSoftwareAgent(String softwareAgent) {
        this.softwareAgent = softwareAgent;
    }

    public Map<String, String> getSourceEntities() {
        return sourceEntities;
    }

    public void setSourceEntities(Map<String, String> sourceEntities) {
        this.sourceEntities = sourceEntities;
    }

    public Map<String, String> getSdiffEntities() {
        return sdiffEntities;
    }

    public void setSdiffEntities(Map<String, String> sdiffEntities) {
        this.sdiffEntities = sdiffEntities;
    }
}
