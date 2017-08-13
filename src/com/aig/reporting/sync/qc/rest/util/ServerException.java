package com.aig.reporting.sync.qc.rest.util;


import java.io.File;

import util.data.KeyMap;



@SuppressWarnings("serial")
public class ServerException extends Exception {

	public ServerException(String message) {
        super(message);
    }

    public void log(File parent ,String name) {
        if(!parent.exists()){
            parent.mkdirs();
        }
        KeyMap.writeFile(new File(parent,name), this.getMessage());
    }
}
