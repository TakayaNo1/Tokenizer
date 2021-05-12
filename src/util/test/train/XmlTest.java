package util.test.train;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.struct.HTMLElement;
import util.struct.Node;
import util.struct.XMLTokenizer;

public class XmlTest {
	public static void main(String[] args){
//		XMLTokenizer xml=new XMLTokenizer(new File("D:/Download/国土交通省データ/鉄道データ/N02-08/KS-META-N02-08.xml"));
		XMLTokenizer xml=new XMLTokenizer(new File("D:/Download/国土交通省データ/鉄道データ/N02-08/N02-08.xml"));
		
		Node<HTMLElement> root= xml.getRootNode();
		
		String dir="ksj:GI/dataset/ksj:object/ksj:AA01/ksj:OBJ/";
		String curve="jps:GM_Curve";
		String point="jps:GM_Point";
		String eb02="ksj:EB02";
		String eb03="ksj:EB03";
		
		List<Node<HTMLElement>> curve_list=HTMLElement.sort(root, dir+curve);
		Map<String, Node<HTMLElement>> curveMap=new HashMap<String, Node<HTMLElement>>();
		for(Node<HTMLElement> c:curve_list){
			String id=c.getElement().getMeta().get(0).split("\"")[1];
			curveMap.put(id, c);
		}
		
		List<Node<HTMLElement>> point_list=HTMLElement.sort(root, dir+point);
		Map<String, Node<HTMLElement>> pointMap=new HashMap<String, Node<HTMLElement>>();
		for(Node<HTMLElement> p:point_list){
			String id=p.getElement().getMeta().get(0).split("\"")[1];
			pointMap.put(id, p);
		}
		
		List<Node<HTMLElement>> eb03_list=HTMLElement.sort(root, dir+eb03);
		for(int i=0;i<10;i++){
			Node<HTMLElement> a=eb03_list.get(i);
			Node<HTMLElement> a_loc=HTMLElement.sort(a, "ksj:LOC").get(0);
			String a_loc_id=a_loc.getElement().getMeta().get(0).split("\"")[1];
			String a_loc_name=HTMLElement.sort(a, "ksj:STN").get(0).getChildren(0).getElement().getContent();
			
			Node<HTMLElement> a_curve=curveMap.get(a_loc_id);
			List<Node<HTMLElement>> a_indirect_points=HTMLElement.sort(a_curve, "jps:GM_Curve.segment/jps:GM_LineString/jps:GM_LineString.controlPoint/jps:GM_PointArray/GM_PointArray.column/jps:GM_Position.indirect/GM_PointRef.point");
			List<Node<HTMLElement>> a_direct_points=HTMLElement.sort(a_curve, "jps:GM_Curve.segment/jps:GM_LineString/jps:GM_LineString.controlPoint/jps:GM_PointArray/GM_PointArray.column/jps:GM_Position.direct/DirectPosition.coordinate");
			
			System.out.println(a_loc_id+"\n"+a_loc_name);
			for(Node<HTMLElement> p:a_indirect_points){
				String a_point_id=p.getElement().getMeta().get(0).split("\"")[1];
				Node<HTMLElement> a_point=pointMap.get(a_point_id);
				String coordinate=HTMLElement.sort(a_point, "jps:GM_Point.position/jps:DirectPosition/DirectPosition.coordinate").get(0).getChildren(0).getElement().getContent();
				
				System.out.println(a_point_id+" ,"+coordinate);
			}
			for(Node<HTMLElement> p:a_direct_points){
				System.out.println(p.getChildren(0).getElement().getContent());
			}
			System.out.println("");
		}
	}
}
