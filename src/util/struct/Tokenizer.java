package util.struct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

public abstract class Tokenizer {
	public static final String charset="UTF-8";
	private BufferedReader reader;
	private String line;
	private int line_index;
	
	public Tokenizer(URL url) throws IOException{
		URLConnection conn = url.openConnection();
		InputStream is = conn.getInputStream();
		InputStreamReader isr = new InputStreamReader(is,charset);
		reader=new BufferedReader(isr);
	}
	public Tokenizer(File file) throws IOException{
		if(file.exists()){
			FileReader fr=new FileReader(file);
			reader=new BufferedReader(fr);
		}else{
			throw new IOException("file is not found.");
		}
	}
	public Tokenizer(String text){
		reader=new BufferedReader(new StringReader(text));
	}
	
	public void addIndex(int i){
		line_index+=i;
	}
	public char getChar(){
		try{
			if(line==null){
				line=reader.readLine();
			}
			if(line.length()<=line_index){
				line=reader.readLine();
				if(line!=null){
					line_index=0;
					return ' ';
				}else{
					reader.close();
					return (char)0;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			return (char)0;
		}
		return line.charAt(line_index++);
	}
	public abstract Node<? extends Element> getRootNode();
}
