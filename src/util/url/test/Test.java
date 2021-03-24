package util.url.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Test {
	public static void main(String[] args) throws Exception{
		String charset="UTF-8";
		String url="https://game.mahjongsoul.com/";
		
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			//URLConnection conn = new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(url, 0)));
			is = conn.getInputStream();
			isr = new InputStreamReader(is,charset);
			br = new BufferedReader(isr);
			
			ArrayList<String> lineList = new ArrayList<String>();
			String line = null;
			while((line = br.readLine()) != null) {
				lineList.add(line);
				System.out.println(line);
			}
			
			//return lineList;
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
}
