import java.util.*;
import java.io.*;
import java.net.*;

public class Server
{
	public static void main(String args[]) throws Exception
	{
		ServerSocket socket1 = new ServerSocket(2004);
		System.out.println("\nServer started on port 2004");
		while(true)
		{
			System.out.println("\nWaiting for Connection ...");
			File currentDirectory = new File("DirectoryA");
			if (!currentDirectory.exists()) {
				if (currentDirectory.mkdir()) {
					System.out.println("\nThe directory has been created.");
				} else {
					System.out.println("\nFailed to create the directory.");
				}// End of else
			}// End of if
			transferfile transfer = new transferfile(socket1.accept());
		}// End of while
	}// End of main
}// End of Server

class transferfile extends Thread
{
	Socket ClientSoc;

	DataInputStream dataInput;
	DataOutputStream dataOutput;
	
	transferfile(Socket socket1)
	{
		try
		{
			ClientSoc = socket1;						
			dataInput = new DataInputStream(ClientSoc.getInputStream());
			dataOutput = new DataOutputStream(ClientSoc.getOutputStream());
			System.out.println("FTP Client Connected ...");
			start();
			
		}// End of try
		catch(Exception ex)
		{
		}// End of catch		
	}// End of transferFile
	
	public void run()
	{
		while(true)
		{
			try
			{
				System.out.println("Waiting for Command ...");
				String Command = dataInput.readUTF();
				if(Command.compareTo("GET") == 0)
				{
					System.out.println("\tGET Command Received ...");
					SendFile();
					continue;
				}// End of if
				else if(Command.compareTo("SEND") == 0)
				{
					System.out.println("\tSEND Command Receiced ...");				
					ReceiveFile();
					continue;
				}// End of else if
				else if(Command.compareTo("DISCONNECT") == 0)
				{
					System.out.println("\tDisconnect Command Received ...");
					System.exit(1);
				}// End of else if
			}// End of try
			catch(Exception ex)
			{
			}// End of catch
		}// End of while
	}// End of run
	
	void ReceiveFile() throws Exception
	{
		String filename = dataInput.readUTF();
		if(filename.compareTo("File not found") == 0)
		{
			return;
		}// End of if
		
		File dir = new File("Files");
		dir.mkdirs();
		File f = new File(dir, filename);
		String option;
		
		if(f.exists())
		{
			dataOutput.writeUTF("File Already Exists");
			option = dataInput.readUTF();
		}// End of if
		else
		{
			dataOutput.writeUTF("SendFile");
			option = "Y";
		}// End of else
			
			if(option.compareTo("Y") == 0)
			{
				FileOutputStream fout=new FileOutputStream(f);
				int ch;
				String temp;
				do
				{
					temp = dataInput.readUTF();
					ch=Integer.parseInt(temp);
					if(ch != -1)
					{
						fout.write(ch);					
					}// End of if
				}while(ch != -1);
				fout.close();
				dataOutput.writeUTF("File Send Successfully");
			}// End of if
			else
			{
				return;
			}// End of else
	}// End of ReceiveFile
	
	void SendFile() throws Exception
	{		
		String filename = dataInput.readUTF();
		File dir = new File("Files");
		File fileToSend = new File(dir, filename);
		if(!fileToSend.exists())
		{
			dataOutput.writeUTF("File Not Found");
			return;
		}// End of if
		else
		{
			dataOutput.writeUTF("READY");
			FileInputStream fileInput = new FileInputStream(fileToSend);
			int ch;
			do
			{
				ch = fileInput.read();
				dataOutput.writeUTF(String.valueOf(ch));
			}// End of do
			while(ch != -1);	
			fileInput.close();	
			dataOutput.writeUTF("File Receive Successfully");							
		}// End of else
	}// End of SendFile
}// End of transferFile