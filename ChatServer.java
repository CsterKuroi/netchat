
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ChatServer
{
	public static void main(String args[])
	{
		ServerSocket server=null;
		Socket you=null;
		Hashtable peopleList;                                 //�����������߿ͻ���ͨ�ŵķ������̵߳�ɢ�б�
		
		peopleList=new Hashtable();
		while(true)
		{
			try
			{
				server=new ServerSocket(6666);
			}
			catch(IOException e1)
			{
				System.out.println("���ڼ���");
			}
			try
			{
				you=server.accept();                                //�����Ϳͻ��˵����ӵ��׽���
				InetAddress address=you.getInetAddress();
				System.out.println("�û���IP��"+address);
			}	
			catch(IOException e)
			{
			}
			if(you!=null)
			{
				Server_thread peopleThread=new Server_thread(you,peopleList);
				peopleThread.start();                                 //��ÿͻ���ͨ�ŵķ���������ʼ�����߳�
			}	
			else{
				continue;
			}	
		}
	}
}
	
class Server_thread extends Thread
{
		String name=null,sex=null;                                   //�����ߵ��ǳƺ��Ա�
		Socket socket=null;
		File file=null;
		DataOutputStream out=null;
		DataInputStream in=null;
		Hashtable peopleList=null;
		Server_thread(Socket t,Hashtable list)
		{
			peopleList=list;
			socket=t;
			try
			{
				in=new DataInputStream(socket.getInputStream());
				out=new DataOutputStream(socket.getOutputStream());
			}
			catch(IOException e)
			{
			}	
		}
		public void run()
		{
			while(true)
			{
				String s=null;
				try
				{//�ȴ����������̣߳�ֱ���յ���Ϣ���ͻ��˷�������Ϣ
					s=in.readUTF();
					if(s.startsWith("�ǳ�:"))                    //����û��ύ���ǳƺ��Ա�
					{
						name=s.substring(s.indexOf(":")+1,s.indexOf("�Ա�"));    //��ȡ�û���Ϣ�е��ǳ�
						sex=s.substring(s.indexOf("�Ա�")+2);                    //��ȡ�Ա�
						
						boolean boo=peopleList.containsKey(name);                //���ɢ�б����Ƿ����н������ǳƵ�������
				        if(boo==false)
				        {
							
							peopleList.put(name,this);                           //����ǰ�߳���ӵ�ɢ�б��ǳ���Ϊ�ؼ���
							out.writeUTF("��������:");
							Enumeration enum1=peopleList.elements();
							
							while(enum1.hasMoreElements())                         //��ȡ���е���ͻ���ͨ�ŵķ������߳�
							{
								Server_thread th=(Server_thread)enum1.nextElement();//����ǰ�����ߵ��ǳƺ��Ա�֪ͨ���е��û�
								th.out.writeUTF("������:"+name+"�Ա�"+sex);        //Ҳ�����������ߵ�����֪ͨ���̣߳���ǰ�û���
								if(th!=this)
								{
									out.writeUTF("������:"+th.name+"�Ա�"+th.sex);
								}
							}
						}
						else
						{//������û��ǳ��Ѵ��ڣ���ʾ�û���������
							out.writeUTF("����������:");
						}
					  }
					  else if(s.startsWith("������������:"))
					  {
						 String message=s.substring(s.indexOf(":")+1);
						 Enumeration enum1=peopleList.elements();                    //��ȡ���е���ͻ���ͨ�ŵķ������߳�
						 while(enum1.hasMoreElements())
						 {
							 SimpleDateFormat df = new SimpleDateFormat("(yyyy-MM-dd HH:mm:ss)");//�������ڸ�ʽ
							((Server_thread)enum1.nextElement()).out.writeUTF("������������:"+message+df.format(new Date()));
						 }
					  }			
				      else if(s.startsWith("�û��뿪:"))
				      {
					       Enumeration enum1=peopleList.elements();               //��ȡ���е���ͻ���ͨ�ŵķ������߳�
					       while(enum1.hasMoreElements())                         //֪ͨ���������ߣ����û�������
					       {
						       try
						       {
							        Server_thread th=(Server_thread)enum1.nextElement();
							        if(th!=this&&th.isAlive())
							        {
							        	th.out.writeUTF("�û�����:"+name);
							    	}
							   }
					           catch(IOException eee)
					           {
					 	       }		
					    	}
					       if(name!=null)
				                peopleList.remove(name);
				            socket.close();                                                          //�رյ�ǰ����
				            System.out.println(name+"�ͻ��뿪��");
				            break;		                                                             //�������̵߳Ĺ������߳�����
				      }
					
				     else if(s.startsWith("˽����������:"))
				     {
				         	String ���Ļ�=s.substring(s.indexOf(":")+1,s.indexOf("#"));
				         	String toPeople=s.substring(s.indexOf("#")+1);//�ҵ�Ҫ�������Ļ����߳�
					
					        Server_thread toThread=(Server_thread)peopleList.get(toPeople);
				            if(toThread!=null)
				            {
				            	 SimpleDateFormat df = new SimpleDateFormat("(yyyy-MM-dd HH:mm:ss)");//�������ڸ�ʽ
				                 toThread.out.writeUTF("˽����������:"+���Ļ�+df.format(new Date()));
				                 out.writeUTF("˽����������:"+���Ļ�+df.format(new Date()));
				         	}
				            else
				            {//֪ͨ��ǰ�û����������Ļ������Ѿ�������
					             out.writeUTF("˽����������:"+toPeople+"�Ѿ�����");
					        }
						
					  }				
				}
			    catch(IOException ee)                               //�������߹ر��������������IOException
			   {
				    Enumeration enum1=peopleList.elements();         //��ȡ���е���ͻ���ͨ�ŵķ������߳�
				
				    while(enum1.hasMoreElements())                   //֪ͨ���������ߣ����û�����
				    {
					   try
					   {
					    	Server_thread th=(Server_thread)enum1.nextElement();
						    if(th!=this&&th.isAlive())
						    {
							     th.out.writeUTF("�û�����:"+name);
							}
						}
					    catch(IOException eee)
					    {
						}	
					}
				    if(name!=null)
				         peopleList.remove(name);
				    try                                              //�رյ�ǰ����
				    {
					    socket.close();
				    }	
				    catch(IOException eee)
				    {
				    }
			        System.out.println(name+"�û��뿪��");
			        break;	                                            //�������̵߳Ĺ������߳�����
			  }
				
		  }
	 }	

}
		