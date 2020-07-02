package com.wxf.deobfuscator;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
/*
 * rename the smali files that have dup name with dir
 * 
 */
public class Main extends AppBase{
	static String srcRoot = null;
	static String dstRoot = null;
	static String dotSmali = ".smali";
	//
	static ArrayList<String> fileList 			= new ArrayList<String>();
	static HashMap<String, String> fileMap		= new HashMap<String, String>();		// filename, clz
	static ArrayList<String> dirList			= new ArrayList<String>();
	static HashMap<String, String> dirMap		= new HashMap<String, String>();		// filename, dir
	static HashMap<String, String> clzMap		= new HashMap<String, String>();		// clz, filename
	static HashMap<String, String> contentMap	= new HashMap<String, String>();		// clz, content
	static HashMap<String, String> newClzMap	= new HashMap<String, String>();		// clz, newClz
	
	public static void main(String [] args) throws Exception{
		srcRoot = "/Users/xuefeiwu/Projects/guda/eclipse/workspace_ad/oppo_mobad_demo_329_fix_ab_context/doc/smali";
		dstRoot = srcRoot + "_new";
		
		searchDir(srcRoot);
		println("fileMap.size()="+fileMap.size());
		println("fileList.size="+fileList.size());
		println("dirList.size="+dirList.size());
		println("dirMap.size="+dirMap.size());
		println("clzMap.size="+clzMap.size());
		
		readAll();
		println("contentMap.size="+contentMap.size());
    	renameAll();
    	replaceAll();
		println("newClzMap.size="+newClzMap.size());
//    	writeAll();
    	
		println("All done!");
    }
	/*
	 * read all files in fileList
	 */
	static void readAll() {
		for(String fullpath:fileList) {
			String clz = fileMap.get(fullpath);// fullpath.substring(srcRoot.length(), fullpath.length() - 6);
	    	File f = new File(fullpath);	//srcRoot, clz + dotSmali);
	    	String content = readTextFile(f);
	    	if(content != null) {
	    		contentMap.put(clz, content);
	    	}
		}
	}

	/*
	 * rename the class files that have dup name with dir
	 * 
	 */
    static void renameAll() {
		for(String pkg:dirList) {
			if(clzMap.containsKey(pkg)) {
				rename(pkg);
			}
		}
    }
    /*
     * replace old clz with new clz
     * 
     * ps: for read map list from disk
     */
    static void replaceAll() {
        try{
        	for(String clzName:newClzMap.keySet()) {
        		String newClzName = newClzMap.get(clzName);
	        	String tag = clzName + ";";
	        	String newTag = newClzName + ";";
	        	for(String fn:fileList) {
	        		String clz = fileMap.get(fn);
		        	String content = contentMap.get(clz);
		        	if(content == null) {
		        		println("content is null, "+clz);
		        		return;
		        	}
		        	String newContent = content;
		        	if(content.contains(tag)) {
		        		println("replacing ["+tag+"] to ["+newTag+"] for " + clz);
		        		newContent = content.replace(tag, newTag);
		        	}
		        	contentMap.put(clz, newContent);
	        	}
        	}
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * write all files to disk
     */
    static void writeAll() {
        try{
        	for(String fn : fileList) {
        		String clz = fileMap.get(fn);
		    	String newClzName = clz;
		    	if(newClzMap.containsKey(clz))
		    		newClzName = newClzMap.get(clz);
	        	String content = contentMap.get(clz);
	        	File dstFile = new File(dstRoot, newClzName + dotSmali);
				File dstPath = new File(dstFile.getParent());
				dstPath.mkdirs();
	        	writeFile(dstFile, content);
        	}
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
	/*
	 * 
	 */
    static void searchDir(String dir) {
    	File srcPath = new File(dir);
    	
    	String[] files = srcPath.list();
    	if(files == null) {
    		println("search list error, "+dir+" exists="+srcPath.exists()+", canRead="+srcPath.canRead());
    		return;
    	}
    	for(String f:files) {
    		File srcFile = new File(dir, f);
    		String fullpath = srcFile.getAbsolutePath();
    		if(srcFile.isDirectory()) {
    			String clz = fullpath.substring(srcRoot.length()+1);
    			clz = clz.replace("\\", "/");
//    			println("dir: " + clz);
    			dirMap.put(clz, clz);
    			dirList.add(clz);
    			searchDir(fullpath);
    		}
    		else if(fullpath.endsWith(dotSmali)) {
    			String clz = fullpath.substring(srcRoot.length() + 1, fullpath.length() - 6);
    			clz = clz.replace("\\", "/");
    			fileList.add(fullpath);
    			fileMap.put(fullpath, clz);
//    			println("clz: " + clz);
    			clzMap.put(clz, fullpath);
    		}
    	}
    }
    /*
     * rename the smali file and replace all references
     */
    static void rename(String clzName){
    	String newClzName = clzName + "_1";
		println("rename: "+clzName + "\t-> " + newClzName);
		if(dirMap.containsKey(newClzName)) {
			println("");
		}

        try{
        	String tag = clzName + ";";
        	String newTag = newClzName + ";";
        	newClzMap.put(clzName, newClzName);
//        	for(String fn:fileList) {
//        		String clz = fileMap.get(fn);
//	        	String content = contentMap.get(clz);
//	        	if(content == null) {
//	        		println("content is null, "+clz);
//	        		return;
//	        	}
//	        	String newContent = content;
//	        	if(content.contains(tag)) {
//	        		println("replacing ["+tag+"] to ["+newTag+"] for " + clz);
//	        		newContent = content.replace(tag, newTag);
//	        	}
//	        	contentMap.put(clz, newContent);
//        	}
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

	static String getNewName(int count) {
    	String n = "";
    	do{
    		if(n.length() > 0)count--;
    		int c = count % 26;
    		n = String.valueOf((char)('a' +c)) + n;
    		count = count / 26;
    	}while(count >0);
    	return n;
    }
    
}
