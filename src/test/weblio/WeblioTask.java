package test.weblio;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WeblioTask extends JFrame implements KeyListener,ActionListener{
	
	private static final long serialVersionUID = 1L;
	private static final int MaxDataQueueSize=8;
	private static final int MaxTaskQueueSize=4;
	private Map<String, String> dictionary=new HashMap<String, String>();
	private int dictionaryIndex=0;
	private List<WeblioData> dataQueue=new CopyOnWriteArrayList<>();
	private List<Thread> taskQueue=new CopyOnWriteArrayList<>();
	
	private WeblioSQL wsql;
	private boolean isActive=true;
	
	private JPanel panel;
	private JLabel text;
	private JButton button;
	
	public WeblioTask() throws SQLException {
		wsql=new WeblioSQL();
		ResultSet result=wsql.getPreparedWordSet();
		this.dictionary=new HashMap<String, String>();
		while(result.next()) {
			String url=result.getString("url");
			String word=result.getString("word");
			dictionary.put(url, word);
		}
		
		setBounds(0, 0, 600, 100);
		setMinimumSize(new Dimension(600,100));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Weblio");
		setVisible(true);
		
		panel=new JPanel();
		text=new JLabel("TextField");
		button=new JButton("Esc");
		button.addActionListener(this);
		panel.add(text);
		panel.add(button);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		addKeyListener(this);
	}
	
	private void startTaskControllThread() {
		while(true) {
			if(taskQueue.size()==0 && !isActive) {
				break;
			}
			
			for(Thread task : taskQueue) {
				if(!task.isAlive()) {
					taskQueue.remove(task);
					updateText();
					break;
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void startDataLoaderThread(){
		for(Entry<String, String> entry : dictionary.entrySet()) {
			try {
				while(dataQueue.size()>=MaxDataQueueSize || taskQueue.size()>=MaxTaskQueueSize) {
					if(!isActive) {
						break;
					}
					Thread.sleep(2000);
				}
				
				String url=entry.getKey();
				String word=entry.getValue();
				
				Thread task=new Thread(()->{
					try {
						startDataLoader(url, word);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				task.start();
				taskQueue.add(task);
				updateText();
				
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(!isActive) {
				break;
			}
			
			dictionaryIndex++;
		}
		isActive=false;
		System.out.println("DataLoader thread end");
	}
	private void startDataLoader(String url,String word) throws IOException {
		System.out.println(url+" "+word);
		WeblioData data=WeblioData.load(url, word);
		dataQueue.add(data);
		updateText();
	}
	private void startSQLTask() {
		while(true) {
			if(dataQueue.size()==0) {
				if(!isActive) {
					break;
				}
				continue;
			}
			WeblioData data = dataQueue.remove(0);
			updateText();
			try {
				if(data.isNull()) {
					System.out.println(data.getWord()+" ("+data.getURL()+") is Null.");
					wsql.delete(data.getWord());
				}else{
//					System.out.println(data.getWord()+"\n"+data.getExplain());
					wsql.completeWord(data);
				}
				wsql.commit();
			}catch(SQLException e) {
				try {
					wsql.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			
			System.out.println(data.getWord()+" completed.");
		}
		System.out.println("SQL thread end");
	}
	private void updateText() {
		text.setText("Data Queue Size = "+dataQueue.size()+", Task Queue Size = "+taskQueue.size()+", "+dictionaryIndex+"/"+dictionary.size());
	}
	public int start() throws InterruptedException, IOException {
		Thread t0=new Thread(()->startTaskControllThread());
		Thread t1=new Thread(()->startDataLoaderThread());
		Thread t2=new Thread(()->startSQLTask());
		t0.start();
		t1.start();
		t2.start();
		t0.join();
		t1.join();
		t2.join();
		
		return dictionary.size();
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==27) {
			isActive=false;
			System.out.println("close proccess.");
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		isActive=false;
		System.out.println("close proccess.");
	}
	
	public static void main(String[] args) throws SQLException, IOException, InterruptedException {
		WeblioTask task=new WeblioTask();
		task.start();
		System.exit(-1);
	}
}
