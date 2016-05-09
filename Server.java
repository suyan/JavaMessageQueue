import java.util.*;
import java.net.*;
import java.io.*;

class Server {
	public static void main(String[] args) throws Exception{
		new MessageQueueManager();
	}
}

class MessageQueueManager {
	public MessageQueueManager() throws Exception {
		int port = 8888;
		ServerSocket server = new ServerSocket(port);
		MessageQueue messageQueue = new MessageQueue();
		while (true) {
			Socket socket = server.accept(); // wait socket connection
			// producer or consumer
			new Thread(new Task(socket, messageQueue)).start();
		}
	}
}

class Task implements Runnable {
	private Socket socket;
	private MessageQueue messageQueue;
	public Task(Socket socket, MessageQueue messageQueue) {
		this.socket = socket;
		this.messageQueue = messageQueue;
	}
	
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String temp = br.readLine();
			Writer writer = new OutputStreamWriter(socket.getOutputStream());
			if (temp.equals("producer")) {
				while ((temp = br.readLine()) != null) {
					if (temp.equals("eof")) {
						break;
					} else {
						sb.append(temp);
					}
				}
				messageQueue.push(sb.toString());
				writer.write("success\n");
			} else if (temp.equals("consumer")) {
				String result = messageQueue.pop();
				writer.write("success\n");
				writer.write(result);
				writer.write("eof\n");
			} else if (temp.equals("admin")) {
				writer.write("success\n");
				writer.write("Queue size: " + messageQueue.size());
				writer.write("eof\n");
			}
			writer.flush();
			writer.close();
			br.close();
			socket.close();	
		} catch (Exception e) {
		}
	}
}

class MessageQueue {
	private Queue<String> messageQueue;
	private boolean isWrite;
	private int maxSize;
	public MessageQueue() {
		messageQueue = new LinkedList<String>();
		isWrite = false;
		maxSize = 100;
	}
	
	public synchronized void multiPush(ArrayList<String> messages) throws Exception {
		isWrite = true;
		for (String message : messages) {
			while (messageQueue.size() == maxSize) {
				wait();
			}
			messageQueue.add(message);
			notifyAll();
		}
		isWrite = false;
	}
	
	public synchronized void push(String message) throws Exception {
		while (isWrite == true && messageQueue.size() == maxSize) {
			wait();
		}

		messageQueue.add(message);
		notifyAll();
	}
	
	public synchronized String pop() throws Exception {
		while (messageQueue.size() == 0) {
			wait();
		}
		String result = messageQueue.poll();
		notifyAll();
		
		return result;
	}
	
	public synchronized int size() {
		return messageQueue.size();
	}
}
