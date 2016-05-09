import java.util.*;
import java.net.*;
import java.io.*;

class Client {
	public static void main(String[] args) throws Exception {
		String host = "68.181.61.48";
		int port = 8888;
		Socket client = new Socket(host, port);
		Writer writer = new OutputStreamWriter(client.getOutputStream());
		
		if(args.length == 0) {
			writer.write("admin\n");
		} else if(args[0].equals("producer")) {
			writer.write("producer\n");
			writer.write(args[1] + "\n");
			writer.write("eof\n");
		} else if(args[0].equals("consumer")) {
			writer.write("consumer\n");
		} 
		writer.flush();
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String temp;
	
		while((temp = br.readLine()) != null) {
			if(temp.equals("eof")) {
				break;			
			}	
			sb.append(temp);
		}
		System.out.println("From server : " + sb);
		writer.close();
		br.close();
		client.close();

	}
	
}