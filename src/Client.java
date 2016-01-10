import java.util.*;
import java.io.*;
import java.net.*;

public class Client
{
	public static void main(String args[]) throws Exception
	{
		Scanner stdin = new Scanner(System.in);
		
		try{
			//1. creating a socket to connect to the server
			System.out.println("Please Enter your IP Address");
			String ipaddress = stdin.next();
			Socket socket1 = new Socket(ipaddress, 2004);
			System.out.println("Connected to " + ipaddress + " in port 2004");
			transferfileClient transfer = new transferfileClient(socket1);
			File currentDirectory = new File(".");
			
			transfer.displayMenu();
			
			//2. get Input and Output streams
			ObjectOutputStream  output = new ObjectOutputStream(socket1.getOutputStream());
			output.flush();
			ObjectInputStream input = new ObjectInputStream(socket1.getInputStream());
		}// End of try
		
		catch(UnknownHostException unknownHost){
			System.err.println("\nYou are trying to connect to an unknown host.");
		}// End of catch
		
		catch(IOException ioException){
			ioException.printStackTrace();
		}// End of catch
	}// End of main
	
	public static void getAllFiles(File curDir) {
	    File[] filesList = curDir.listFiles();
	    for(File f : filesList){
	        if(f.isDirectory()){
	            System.out.println(f.getName());
	        }// End of if
	        if(f.isFile()){
	            System.out.println(f.getName());
	        }// End of if
	    }// End of for
	}// End of getAllFiles
}// End of Client

class transferfileClient
{
	Socket ClientSoc;
	File currentDirectory = new File("Directory1");
	DataInputStream dataInput;
	DataOutputStream dataOutput;
	BufferedReader bufferReader;
	
	public void displayMenu() throws Exception
	{
		while(true)
		{	
			System.out.println("~~ MENU ~~");
			System.out.println("1. Send File");
			System.out.println("2. Receive File");
			System.out.println("3. List all files");
			System.out.println("4. Disconnect from server");
			System.out.print("\nEnter Choice: ");
			int choice;
			choice = Integer.parseInt(bufferReader.readLine());
			if(choice == 1)
			{
				dataOutput.writeUTF("SEND");
				SendFile();
			}// End of if
			else if(choice == 2)
			{
				dataOutput.writeUTF("GET");
				ReceiveFile();
			}// End of else if
			else if(choice == 3)
			{
				
				Client.getAllFiles(currentDirectory);
				//8Thread.sleep(1000);
			}// End of else if
			else
			{
				dataOutput.writeUTF("DISCONNECT");
				System.exit(1);
			}// End of else
		}// End of while
	}// End of displayMenu
	
	transferfileClient(Socket socket1)
	{
		try
		{
			ClientSoc = socket1;
			dataInput = new DataInputStream(ClientSoc.getInputStream());
			dataOutput = new DataOutputStream(ClientSoc.getOutputStream());
			bufferReader = new BufferedReader(new InputStreamReader(System.in));
		}
		catch(Exception ex)
		{
		}		
	}

	void ReceiveFile() throws Exception
	{
		String fileName;
		System.out.print("\nEnter File Name: ");
		fileName = bufferReader.readLine();
		dataOutput.writeUTF(fileName);
		String msgFromServer = dataInput.readUTF();
		
		if(msgFromServer.compareTo("File Not Found") == 0)
		{
			System.out.println("\nFile not found on Server ...");
			return;
		}// End of if
		else if(msgFromServer.compareTo("READY") == 0)
		{
			System.out.println("\nReceiving File ...");
			File f=new File(fileName);
			if(f.exists())
			{
				String Option;
				System.out.println("\nFile Already Exists. \n\tDo you want to OverWrite it? (Y/N)");
				Option = bufferReader.readLine();			
				if(Option == "N")	
				{
					dataOutput.flush();
					return;	
				}// End of if
			}// End of if
			FileOutputStream fileOutput = new FileOutputStream(f);
			int ch;
			String temp;
			do
			{
				temp = dataInput.readUTF();
				ch = Integer.parseInt(temp);
				if(ch != -1)
				{
					fileOutput.write(ch);					
				}
			}while(ch != -1);
			fileOutput.close();
			System.out.println(dataInput.readUTF());
				
		}// End of else if
	}// End of ReceiveFile
	
	void SendFile() throws Exception
	{	
		
		String filename;
		System.out.print("\nEnter File Name: ");
		filename = bufferReader.readLine();
			
		File f = new File(filename);
		if(!f.exists())
		{
			System.out.println("\nFile not Exists...");
			dataOutput.writeUTF("\nFile not found");
			return;
		}// End of if
		
		dataOutput.writeUTF(filename);
		
		String msgFromServer = dataInput.readUTF();
		if(msgFromServer.compareTo("\nFile Already Exists")==0)
		{
			String Option;
			System.out.println("\nFile Already Exists. \n\tDo you want to OverWrite it?(Y/N)");
			Option = bufferReader.readLine();			
			if(Option == "Y")	
			{
				dataOutput.writeUTF("Y");
			}// End of if
			else
			{
				dataOutput.writeUTF("N");
				return;
			}// End of else
		}// End of if
	
		System.out.println("\nSending File ...");
		FileInputStream fileInput = new FileInputStream(f);
		int ch;
		do
		{
			ch=fileInput.read();
			dataOutput.writeUTF(String.valueOf(ch));
		}
		while(ch != -1);
		fileInput.close();
		System.out.println(dataInput.readUTF());
	}// End of SendFile
}// End of transferfileClient