package util.url;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class HTMLDownloader {
	public static final String charset="UTF-8";
	
	public static List<String> read(String url) throws Exception {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			is = conn.getInputStream();
			isr = new InputStreamReader(is,charset);
			br = new BufferedReader(isr);
			
			ArrayList<String> lineList = new ArrayList<String>();
			String line = null;
			while((line = br.readLine()) != null) {
				lineList.add(line);
			}
			return lineList;
		}finally {
			try {
				br.close();
			}catch(Exception e) {
			}
			try {
				isr.close();
			}catch(Exception e) {
			}
			try {
				is.close();
			}catch(Exception e) {
			}
		}
	}
	
	public static void main(String[] args){
		try {
			//List<String> list=read("https://ja.wikipedia.org/wiki/%E3%83%A9%E3%83%B3%E3%83%80%E3%83%A0%E3%82%A6%E3%82%A9%E3%83%BC%E3%82%AF");
			List<String> list=read("https://www.futbin.com/20/player/44079/lionel-messi");
			for(String line:list){
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
