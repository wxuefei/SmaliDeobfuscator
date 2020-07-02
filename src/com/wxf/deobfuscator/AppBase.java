package com.wxf.deobfuscator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AppBase {
    protected static String readTextFile(String filename){
    	return readTextFile(new File(filename));
    }
    protected static String readTextFile(File file){
    	if(!file.exists() || file.isDirectory()) {
    		return null;
    	}
    	FileInputStream fis;
        try{
        	fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //file.length();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte abyte0[] = new byte[4096];
            int i = 0, j;
            do{
                try{
                    j = bis.read(abyte0);
                }
                catch(IOException ioexception){
                    ioexception.printStackTrace();
                    j = i;
                }
                if(j > 0)
                	baos.write(abyte0, 0, j);
                i = j;
            } while(j == abyte0.length);
            fis.close();
            return baos.toString("UTF-8");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    protected static byte[] readFile(File file){
    	if(!file.exists() || file.isDirectory()) {
    		return null;
    	}
    	FileInputStream fis;
        try{
        	fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte abyte0[] = new byte[4096];
            int readLen = 0, readCount = 0;
            int max = 1024*128;						// just need read 128KB
            do{
                try{
                    readLen = bis.read(abyte0);
                    if(readLen > 0) {
                    	baos.write(abyte0, 0, readLen);
                    	readCount += readLen;
                    	if(readCount >= max)
                    		break;
                    }
                    else
                    	break;
                }
                catch(IOException e2){
                    e2.printStackTrace();
                    break;
                }
            } while(true);
            fis.close();
            return baos.toByteArray();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
	protected static void writeFile(String fn, byte[] data) {
		writeFile(fn, data, false);
	}
	protected static void writeFile(File f, byte[] data) {
		writeFile(f, data, false);
	}
	protected static void writeFile(File f, String content) {
		try {
			writeFile(f, content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(String filename, byte[] data, boolean append) {
		File f = new File(filename);
		writeFile(f, data, append);
	}
	public static void writeFile(File f, byte[] data, boolean append) {
		try {
			FileOutputStream os;
			os = new FileOutputStream(f, append);
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception e) {
			println("writeFile e:"+e.toString());
		}
	}
    	
	protected synchronized static void print(String s) {
    	System.out.print(s);		
	}
	protected synchronized static void println(String s) {
    	System.out.println(s);		
	}

}
