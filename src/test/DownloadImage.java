package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImage {
	public static void main(String[] args){
		String url="https://s3.ultimate-guitar.com/musescore.scoredata/g/4c21a9c42b06f0ad819c668fd86b302027f486bd/score_4.svg?response-content-disposition=attachment%3B%20filename%3D%22score_4.svg%22&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=4SHNMJS3MTR9XKK7ULEP%2F20220214%2Fus-west%2Fs3%2Faws4_request&X-Amz-Date=20220214T143557Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Signature=b065ea171fc4b728b043cde3acbcc80208336ddc729fd2b29f5ae8e858e3c6f5";
		String charset="UTF-8";
		
		try {
			URLConnection conn = new URL(url).openConnection();
			InputStream is = conn.getInputStream();
	//		BufferedImage bi=ImageIO.read(is);
			
			File image=new File("src/file/image.svg");
			System.out.println(image.exists());
			if(!image.exists())image.createNewFile();
			FileOutputStream fos=new FileOutputStream(image);
			fos.write(is.readAllBytes());
			
			is.close();
			fos.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
