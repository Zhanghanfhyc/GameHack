package com.fossgalaxy.games.tbs;

import com.fossgalaxy.games.tbs.rules.Rule;

import java.util.List;

public class GameDef {

    private int hexSize;
    private String resourcesFileName;
    
    private String evalFileName;
    private String ruleFileName;
    private String aiFileName;
    
    private String terrainFileName;
    
    private String[] typeFiles;
    private int turnLimit;

    private List<Rule> rules;

    public int getHexSize() {
        return hexSize;
    }

    public String getResourcesFileName() {
        return resourcesFileName;
    }

    public String getEvalFileName() {
        return evalFileName;
    }

    public String getRuleFileName() {
        return ruleFileName;
    }

    public String getAiFileName() {
        return aiFileName;
    }

    public String[] getTypesFileName() {
        return typeFiles;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public int getTurnLimit() {
        return turnLimit;
    }

    public String getTerrainFileName() {
        return terrainFileName;
    }
}
