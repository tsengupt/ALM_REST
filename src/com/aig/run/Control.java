package com.aig.run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.aig.reporting.sync.qc.rest.QCRestSync;

public class Control {

	public static void main(String[] args) throws Exception {
		
		List<File> attach = new ArrayList<>();
		QCRestSync qcRestSync = new QCRestSync();
		attach.add(new File("C:\\DMStatus.log"));
		qcRestSync.updateResults("Test", "Passed", attach);
		
	}
    

}
