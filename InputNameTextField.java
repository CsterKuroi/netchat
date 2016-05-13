
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.io.*;
import java.util.Hashtable;

public class InputNameTextField extends Panel implements ActionListener,Runnable
{
	TextField nameFile=null;                        //用来输入用户昵称的文本条
	String name=null;                               
	Checkbox male=null,female=null;                 //选择性别的单选框
	CheckboxGroup group=null;
	Button 进入聊天室=null,退出聊天室=null;
	Socket socket=null;                             //和服务器连接的套接字
	DataInputStream in=null;                        //读取服务器发来的消息
	DataOutputStream out=null;                      //向服务器发送消息
	Thread thread=null;                             //负责读取服务器发来的信息的线程
	boolean 能否聊天=false;
	Hashtable listTable;                            //存放在线聊天者昵称的散列表
	
	public InputNameTextField(Hashtable listTable)
	{
		this.listTable=listTable;
		nameFile=new TextField(10);
		group=new CheckboxGroup();
		male=new Checkbox("男",true,group);
		female=new Checkbox("女",false,group);
		进入聊天室=new Button("进入");
		退出聊天室=new Button("退出");
		进入聊天室.addActionListener(this);
		退出聊天室.addActionListener(this);
		thread=new Thread(this);
		add(new Label("昵称: "));
		add(nameFile);
		add(male);
		add(female);
		add(进入聊天室);
		add(退出聊天室);
		退出聊天室.setEnabled(false);
	}
		
	public void set能否聊天(boolean b)
	{
		能否聊天=b;
	}
	public boolean get能否聊天()
	{
		return 能否聊天; 
	}
	public String getName()
	{
		return name;
	}
	public void setName(String s)
	{
		name=s;
	}	
		
	public void setSocketConnection(Socket socket,DataInputStream in,DataOutputStream out)
	{
		this.socket=socket;
		this.in=in;
		this.out=out;
		try{
			thread.start();
		}
		catch(Exception e)
		{
			nameFile.setText(" "+e);
		}	
	}
    public Socket getSocket()
    {
    	return socket;
    }		
    
    public void actionPerformed(ActionEvent e)
    {
    	if(e.getSource()==进入聊天室)
    	{
    		退出聊天室.setEnabled(true);
    		if(能否聊天==true)
    		{
               nameFile.setText(name+"您正在聊天!");  
    		}
    		else
    		{
    			this.setName(nameFile.getText());
    			String sex=group.getSelectedCheckbox().getLabel();
    			if(socket!=null&&name!=null)
    			{//将用户信息发送给服务器端
    				try
    				{
    					out.writeUTF("昵称:"+name+"性别:"+sex);
    				}
    				catch(IOException ee)
    				{
    					nameFile.setText("无法联通服务器"+ee);
    				}
    			 }
    		  }	
    	  }
    	  if(e.getSource()==退出聊天室)
    	  {//通知服务器用户已经离开
    		  try
    		  {
    			  out.writeUTF("用户离开:");
    		  }
    		  catch(IOException ee)
    		  {
    		  }	
    	 }
    }	
    public void run()
    {
    	String message=null;
    	while(true)
    	{//接受服务器发来的消息，并进行分析处理
    		  if(in!=null)
    		  {
    		  	  try
    		  	  {
    		  		  message=in.readUTF();
    		  	  }
    		  	  catch(IOException e)
    		  	  {
    		  		   nameFile.setText("和服务且连接已断开"+e);
    		  	  }
    	      }	
    		  if(message!=null)
    		  {
    			  if(message.startsWith("可以聊天:"))
                  {
        	            能否聊天=true;
        	            break;
        	      }	
                  else if(message.startsWith("聊天者:"))
                  {
        	          String people=message.substring(message.indexOf(":")+1);    //将目前在线的聊天者昵称添加到散列表中
        	          listTable.put(people,people);
        	      }		  	
                  else if(message.startsWith("不可以聊天:"))
                  {
        	           能否聊天=false;
        	           nameFile.setText("该昵称已被占用,请选择其他昵称.");
        	      }
    		  }
             
    	}
    }
}