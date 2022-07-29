package test.weblio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import test.SQL;

public class WeblioSQL extends SQL{
	public WeblioSQL() {
		super(false);
	}
	
	public static final int preparedID=-1;
	public static final int deletedID=-1000;
	
	public ResultSet getPreparedWordSet() throws SQLException {
		return result("select*from word where explanation_id = -1");
	}
//	private void prepareWords(Map<String,String> words) {
//		String sql="";
//	}
	private int prepareWord(String url,String word) throws SQLException {
		ResultSet result=result("select*from word where word=\""+word+"\"");
		if(!result.next()) {
			execute("insert into word(url,word,explanation_id) value(\""+url+"\",\""+word+"\",-1)");
		}
		
		result=result("select*from word where word=\""+word+"\"");
		if(result.next()) {
			return result.getInt("word_id");
		}else {
			return -1;
		}
	}
//	private boolean exists(String word) throws SQLException {
//		ResultSet result=result("select*from word where word=\""+word+"\"");
//		return result.next() && !result.next();
//	}
	private int addExplain(int wordId, String explain) throws SQLException {
		int count=execute("insert into explanation(word_id,explanation) value("+wordId+",\""+explain+"\")");
		if(count <= 0) {
			System.err.println("sql error : could not insert explanation. count="+count+".");
			System.err.println(explain);
		}
		ResultSet result=result("select*from explanation where word_id="+wordId);
		result.next();
		return result.getInt("explanation_id");
	}
	public void completeWord(WeblioData data) throws SQLException {
		ResultSet result=result("select*from word where word=\""+data.getWord()+"\"");
		result.next();
		int wordId=result.getInt("word_id");
		int explanationId=addExplain(wordId, data.getExplain());//add explanation
		
		execute("update word a set a.explanation_id="+explanationId+" where word_id="+wordId);//complete word
		
		for(Entry<String,String> entry:data.getWordList().entrySet()) {
			int childWordId = prepareWord(entry.getKey(), entry.getValue());//prepare new words
			if(childWordId>0) {
				execute("insert into relation(parent_word_id,child_word_id) value("+wordId+","+childWordId+")");
			}
		}
	}
	public void delete(String word) {
//		execute("update word a set a.explanation_id="+deletedID+" where word=\""+word+"\"");
//		execute("delete from word where word=\""+word+"\"");
	}
}
