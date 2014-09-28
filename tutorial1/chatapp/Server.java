package chatapp;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import edu.berkeley.xtrace.MyXTraceContext;//added by zjw
import edu.berkeley.xtrace.reporting.Report;//added by zjw


public class Server {
	public static int PORT = 8888;
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		/* Set up a server */
		ServerSocket ss = new ServerSocket();
		try {
			ss = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("problem listening on port " + PORT);
			System.exit(1);
		}
		/* Get a connection from a client */
		System.out.println("Waiting for connection from client");
		Socket cs = null;
		try {
			cs = ss.accept();
		} catch (IOException e) {
			System.err.println("problem accepting client connection");
		}
		System.out.println("Client connection established");
		
		DataInputStream in=new DataInputStream(cs.getInputStream());
		DataOutputStream out=new DataOutputStream(cs.getOutputStream());
		
		try{
		while (true) {
			MyXTraceContext.readIdInfo(in);//read father event information from client
			Report serverRecvRep=MyXTraceContext.logStart("Server", "OP:Recv from client");//start to log a event
			String recvMsg = in.readUTF();
			MyXTraceContext.logEnd(serverRecvRep, "receive from client:"+recvMsg);//finish to log a event
			
			System.out.println("client: "+recvMsg);
			
			String sendMsg;
			if (recvMsg.contains("bye")){
				sendMsg="bye";
			} else {
				if (Math.random() < 0.3)
					sendMsg = "Yes, I see.";
				else if (Math.random() < 0.5)
					sendMsg = "That is interesting.";
				else sendMsg = "Uh huh.";
			}
			System.out.println("server: "+sendMsg);

			Report serverSendRep=MyXTraceContext.logStart("Server", "OP:send to client");//start to log a event
			MyXTraceContext.writeIdInfo(out, Long.parseLong(serverSendRep.get("StartTime").get(0)));//write father event information to client
			out.writeUTF(sendMsg);
			MyXTraceContext.logEnd(serverSendRep,  "send to client:"+sendMsg);//finish to log a event
			
			if(recvMsg.contains("bye")){
				out.close();
				in.close();
				cs.close();
				ss.close();
				break;
			}
		}
		}catch(Exception e){
			out.close();
			in.close();
			cs.close();
			ss.close();
		}
	}
}