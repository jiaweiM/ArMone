package cn.ac.dicp.gp1809.ga.sequest;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class TextFieldInputAdapter extends KeyAdapter {
	private static final String NUMBER = "number";
	private static final String INTEGER = "integer";
	private static final String CHAR = "char";
	
	private String value;
	private int count;
	
	public TextFieldInputAdapter(String value,int count){
		super();
		
		this.value = value;
		this.count = count;
	}
	
	@Override
	public void keyTyped(KeyEvent k){
		JTextField jtf=(JTextField)k.getSource();
		String text=jtf.getText();
		if(text.length()>=count)
			k.consume();
		
		char c = k.getKeyChar();
		if(value.equals(NUMBER)){	
			if(!(c>='0'&&c<='9'||c=='.')){
				k.consume();
			}
			else if(text.length()==1&&text.equals("0")&&c!='.'){
					jtf.setText("");
					k.consume();
				}
		}
		else if(value.equals(CHAR)){
			if(!((c>='A'&&c<='Z')||(c>='a'&&c<='z'))){
				k.consume();
			}
		}
		else if(value.equals(INTEGER)){
			if(!(c>='0'&&c<='9')||(text.length()==0&&c=='0')){
				k.consume();
			}
		}
		else{
			char[] a = value.toCharArray();
			boolean b = false;
			
			for(int i=0;i<a.length;i++){
				if(c==a[i]){
					b= true;
					break;
				}
			}
			
			if(!b)
				k.consume();
		}
	}
}
