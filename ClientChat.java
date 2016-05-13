
import java.awt.*;
import java.io.*;
import java.net.*;
import java.applet.*;
import java.util.Hashtable;

public class ClientChat extends Applet implements Runnable
{
	public static final int DEFAULT_WEIDH=820;
	public static final int DEFAULT_HEIGHT=520;
	Socket socket=null;
	DataInputStream in=null;
	DataOutputStream out=null;
	InputNameTextField 用户提交昵称界面=null;
	ChatArea 用户聊天界面=null;
	Hashtable listTable;                           //存放在线聊天者昵称的散列表
	Label 提示条;
	Panel north,center;
	Thread thread;
	public void init()
	{
		setSize(DEFAULT_WEIDH,DEFAULT_HEIGHT);
//		int width=getSize().width;                 //获取java applet小程序的宽
//		int height=getSize().height;               //获取java applet小程序的高
		int width=800;
		int height=500;

		listTable=new Hashtable();
		setLayout(new BorderLayout());
		用户提交昵称界面=new InputNameTextField(listTable);
		int h=用户提交昵称界面.getSize().height;
		用户聊天界面=new ChatArea("",listTable,width,height-(h+5));
		用户聊天界面.setVisible(true);
		提示条=new Label("正在连接到服务器，请稍候．．．",Label.CENTER);
		提示条.setForeground(Color.red);
		north=new Panel(new FlowLayout(FlowLayout.LEFT));
		center=new Panel();
		north.add(用户提交昵称界面);
		north.add(提示条);
		center.add(用户聊天界面);
		add(north,BorderLayout.NORTH);
		add(center,BorderLayout.CENTER);
		validate();
				}
	
	public void start()                             //清除此前的套接字
	{
		if(socket!=null&&in!=null&&out!=null)
		{
			try
			{
				socket.close();
				in.close();
				out.close();
				用户聊天界面.setVisible(false);
			}
			catch(Exception ee)
			{
			}
		}
		try
		{
			socket=new Socket("10.138.80.109",6666);
			in=new DataInputStream(socket.getInputStream());
			out=new DataOutputStream(socket.getOutputStream());
		}
		catch(IOException ee)
		{
			提示条.setText("连接失败");
		}
		if(socket!=null)                             //如果连接成功，则提示用户输入昵称
		{
			InetAddress address=socket.getInetAddress();
			提示条.setText("连接："+address+"成功");
			用户提交昵称界面.setSocketConnection(socket,in,out);
			north.validate();
		}	
		if(thread==null)                            //为该用户启动一个新线程
	    {
			thread=new Thread(this);
		    thread.start();
		} 	
    }
	public void stop()
	{
		try
		{
			socket.close();
			thread=null;
		}
		catch(IOException e)
		{
			this.showStatus(e.toString());
		}
	}
	public void run()
	{
		while(thread!=null)
		{
			if(用户提交昵称界面.get能否聊天()==true)
			{
				用户聊天界面.setVisible(true);
				用户聊天界面.setName(用户提交昵称界面.getName());
				用户聊天界面.setSocketConnection(socket,in,out);
				提示条.setText("祝聊天愉快！");
				center.validate();
				break;
			}
			try
			{
				Thread.sleep(100);
			}			
			catch(Exception e)
			{
			}	
		}
	}
}
