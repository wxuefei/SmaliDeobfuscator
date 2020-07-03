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
	static HashMap<String, String> fileMap		= new HashMap<String, String>();		// filename, clz
	static HashMap<String, String> pkgMap		= new HashMap<String, String>();		// package
	static HashMap<String, String> clzMap		= new HashMap<String, String>();		// clz, filename
	static HashMap<String, String> contentMap	= new HashMap<String, String>();		// clz, content
	static HashMap<String, String> newPkgMap	= new HashMap<String, String>();		// new package, pkg, for UTF8 replace
	static HashMap<String, String> newClzMap	= new HashMap<String, String>();		// clz, newClz
	static HashMap<String, String> subClzMap	= new HashMap<String, String>();		// clz, newClz
	static HashMap<String, String> newSubClzMap	= new HashMap<String, String>();		// newClz, clz
	static String usage = "usage: deobufscator <smali path>\n";
	
	public static void main(String [] args) throws Exception{
		if(args.length == 0) {
			println(usage);
			return;
		}
		srcRoot = args[0];
		dstRoot = srcRoot + "_new";
		searchDir(srcRoot);
		println("fileMap.size()="+fileMap.size());
		println("pkgMap.size="+pkgMap.size());
		println("clzMap.size="+clzMap.size());
		
		readAll();
		println("contentMap.size="+contentMap.size());
    	renameAll();
    	println("newClzMap.size="+newClzMap.size());
    	replaceAll();
    	writeAll();
    	
		println("All done!");
    }
	/*
	 * read all files in fileList
	 */
	static void readAll() {
		for(String fullpath:fileMap.keySet()) {
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
	 * rename UTF-8 char to ASCII
	 */
    static void renameAll() {
		for(String pkg:pkgMap.keySet()) {
			if(clzMap.containsKey(pkg)) {
		    	String newClzName = newClzName(pkg);
	
		    	newClzMap.put(pkg, newClzName);
			}
		}
		
		for(String clz:clzMap.keySet()) {
	    	String newClzName = newClzNameRemoveUTF8(clz);
	    	if(!clz.equals(newClzName)) {
//	    		newClzMap.put(clz, newClzName);
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
	        	for(String fn:fileMap.keySet()) {
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
        	for(String fn : fileMap.keySet()) {
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
    			pkgMap.put(clz, clz);
    			searchDir(fullpath);
    		}
    		else if(fullpath.endsWith(dotSmali)) {
    			String clz = fullpath.substring(srcRoot.length() + 1, fullpath.length() - 6);
    			clz = clz.replace("\\", "/");
    			fileMap.put(fullpath, clz);
    			clzMap.put(clz, fullpath);
    		}
    	}
    }

    /*
     * generate new class name
     * 
     * todo: support dir name in UTF-8  
     */
	static String newClzName(String clzName) {
    	String n = "";
    	for(int i=1;i<1000000 && clzMap.containsKey((n = clzName + "_" + i));i++);
    	
    	println("rename: "+clzName + "\t-> " + n);
    	return n;
    }
	
	static boolean isASCII(char c) {
		if((c>='a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <='9') || c == '_' || c == '$' || c == '/') {
			return true;
		}
		return false;
	}
	static boolean isASCII(String s) {
    	for(int i=0;i<s.length();i++) {
    		char c = s.charAt(i);
    		if((c>='a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <='9') || c == '_' || c == '$' || c == '/') {
    		}
    		else {
    			return false;
    		}
    	}
    	return true;
	}
    /*
     * remove the UTF-8 char in class name
     * 
     */
    private static String newClzNameRemoveUTF8(String clzName) {
    	if(isASCII(clzName)) {
    		return clzName;
    	}
    	String[] ss = clzName.split("/");
    	String clz = ss[ss.length -1];
    	String pkg = "";
    	for(int i=0;i<ss.length-1;i++) {
    		String newPkg = pkg + ss[i] + "/";
    		if(!isASCII(ss[i])) {
	    		if(newPkgMap.containsKey(newPkg)) {
	    			pkg = newPkgMap.get(newPkg);
	    		}
	    		else {
	    			for(int n = 0;n<10000;n++) {
	    				String newPkg2 = pkg + base26(n);
	    				if(!newPkgMap.containsKey(newPkg2)) {
	    					newPkgMap.put(newPkg2, newPkg);
	    					pkg = newPkg2;
	    					break;
	    				}
	    			}
	    		}
    		}
    		else {
    			pkg = newPkg;
    		}
    	}
    	ss = clz.split("\\$");
    	//clz = pkg + clz;
    	String clz2 = pkg;
    	for(int i=0;i<ss.length;i++) {
    		if(i > 0)
    			clz2 += "$"; 
    		String newClz = clz2 + ss[i];
    		if(!isASCII(ss[i])) {
	    		if(subClzMap.containsKey(newClz)) {
	    			clz2 = subClzMap.get(newClz);
	    		}
	    		else {
	    			for(int n = 0;n<10000;n++) {
	    				String newClz2 = clz2 + base26(n);
	    				if(!newSubClzMap.containsKey(newClz2) && !clzMap.containsKey(newClz2) && !pkgMap.containsKey(newClz2)) {
	    					subClzMap.put(newClz, newClz2);
	    					newSubClzMap.put(newClz2, clzName);
	    					clz2 = newClz2;
	    					break;
	    				}
	    			}
	    		}
    		}
    		else {
    			clz2 = newClz;
    		}
    	}
    	clz = clz2;
		if(!clzMap.containsKey(clz) && !newClzMap.containsKey(clz)) {
			newClzMap.put(clzName, clz);
		}
		else {
			println("need fix");
		}
    	
		println("rename: "+clzName + "\t-> " + clz);
		return clz;
	}
    
	private static String base26(int n) {
		String v = "";
		do {
			char c = (char) ('a' + n % 26);
			n = n / 26 - 1;
			v = c + v;
		}while(n>=0);
		return v;
	}

}
