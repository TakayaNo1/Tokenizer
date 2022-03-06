package test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import util.struct.Element;
import util.struct.Node;
import util.struct.json.JsonElement;
import util.struct.json.JsonTokenizer;

public class JsonTest {
	public static void main(String[] args) throws MalformedURLException, IOException {
		String json="{\r\n"
				+ "    \"文字列\": \"文字列\",\r\n"
				+ "    \"数値\": 0,\r\n"
				+ "    \"真偽値\": true,\r\n"
				+ "    \"null値\": null,\r\n"
				+ "    \"配列\": [\r\n"
				+ "        \"要素1\",\r\n"
				+ "        \"要素2\"\r\n"
				+ "    ],\r\n"
				+ "    \"オブジェクト\": {\r\n"
				+ "        \"キー\": \"値\"\r\n"
				+ "    }\r\n"
				+ "}\r\n"
				+ "";
//		JsonTokenizer tkn=new JsonTokenizer(json);
		JsonTokenizer tkn=new JsonTokenizer(new File("src/file/test.json"));
//		JsonTokenizer tkn=new JsonTokenizer(new URL("https://www.easports.com/fifa/ultimate-team/api/fut/item?name=ronaldo"));
		Node<JsonElement> root=tkn.getRootNode();
//		Element.show(root);
		JsonElement.search(root, "{/items/{").forEach((e1)->{
			JsonElement.search(e1, "rating").forEach((e2)->{System.out.println(e2.getElement().getValue());});
			JsonElement.search(e1, "commonName").forEach((e2)->{System.out.println(e2.getElement().getValue());});
			JsonElement.search(e1, "firstName").forEach((e2)->{System.out.println(e2.getElement().getValue());});
			JsonElement.search(e1, "lastName").forEach((e2)->{System.out.println(e2.getElement().getValue());});
			System.out.println();
		});
	}
}
