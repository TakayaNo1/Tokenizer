package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class PDFController {

	public static void main(String[] args) throws IOException {
		File infile=new File("src/file/input.txt");
		File outfile=new File("src/file/output.txt");
		FileReader reader=new FileReader(infile);
		BufferedReader br=new BufferedReader(reader);
		
		FileWriter writer=new FileWriter(outfile);
		BufferedWriter bw=new BufferedWriter(writer);
		
		String line;
		
		while((line=br.readLine())!=null) {
			line.replaceAll("\n", "");
			if(line.endsWith("-")) {
				line=line.substring(0, line.length()-1);
			}
			String[] split=line.split(Pattern.quote("."));
			line=String.join(".\n", split);
			
			bw.write(line);
		}
		bw.write(".");
		
		br.close();
		bw.close();
	}
}
