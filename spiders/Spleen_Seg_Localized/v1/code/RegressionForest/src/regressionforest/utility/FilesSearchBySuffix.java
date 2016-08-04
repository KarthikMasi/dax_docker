package regressionforest.utility;

import java.io.File;
import java.io.FilenameFilter;

public class FilesSearchBySuffix {
	public static String[] FilesSearch(String directory, final String suffix){
		File dir = new File(directory);
		FilenameFilter filter = new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.endsWith(suffix);
			}
		};
		return dir.list(filter);
	}
}