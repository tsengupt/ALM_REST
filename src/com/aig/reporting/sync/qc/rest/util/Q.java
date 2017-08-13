package com.aig.reporting.sync.qc.rest.util;

public class Q {
    /**
     * query-param key
     */
    public static final String QUERY = "query";
    /**
     * query with name
     */
    public static final String NAME = "{name['%s']}";
    /**
     * query with name and parent id
     */
    public static final String NAME_PARENTID = "{name['%s'];parent-id[%s]}";
    /**
     * query with cycle id
     */
    public static final String TESTSET_OR_CYCLE_ID = "{cycle-id[%s]}";
    
}
