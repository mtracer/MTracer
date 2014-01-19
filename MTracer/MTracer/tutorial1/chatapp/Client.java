package chatapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import edu.berkeley.xtrace.MyXTraceContext;//added by zjw
import edu.berkeley.xtrace.reporting.Report;//added by zjw
import edu.berkeley.xtrace.IdInfo;

public class Client{
	public static int PORT=8888;
	public static void main(String argv[]) throws IOException, ClassNotFoundException{

		/* start a trace */
		Report traceRep = MyXTraceContext.startTrace("Chatting");
		
		Report initRep = MyXTraceContext.logStart("Client", "initialization");//start to log a event
		/* Set up the connection to the server */
		String serverIP = "localhost";
		if (System.getProperty("serverAddress") != null) {
			serverIP = System.getProperty("serverAddress");
		}
		Socket s = new Socket(serverIP, PORT);
		
		DataInputStream in=new DataInputStream(s.getInputStream());
		DataOutputStream out=new DataOutputStream(s.getOutputStream());
		
		/* Setup up input from the client user */
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		String input;
		MyXTraceContext.logEnd(initRep,  "finish initialization");//finish to log a event
		
		try{
		while (true){
			System.out.print("YOU: ");
			String sendMsg=stdin.readLine();
			
			Report clientSendRep=MyXTraceContext.logStart("Client", "OP:send to server");//start to log a event
			MyXTraceContext.writeIdInfo(out, Long.parseLong(clientSendRep.get("StartTime").get(0)));//write father event information to server
			out.writeUTF(sendMsg);
			MyXTraceContext.logEnd(clientSendRep,  "send to server:"+sendMsg);//finish to log a event
			
			IdInfo idinfo=MyXTraceContext.getIdInfo();//store current id information
			MyXTraceContext.readIdInfo(in);//read father event information from server
			Report clientRecvRep=MyXTraceContext.logStart("Client", "OP:Recv from server");//start to log a event
			String recvMsg = in.readUTF();
			MyXTraceContext.logEnd(clientRecvRep, "receive from client:"+recvMsg);//finish to log a event
			MyXTraceContext.setIdInfo(idinfo);//set previous id information to current
			
			System.out.println("server: " + recvMsg);
			
			if(sendMsg.contains("bye")){
				stdin.close();
				in.close();
				out.close();
				s.close();
				break;
			}
		}
		}catch(Exception e){
			stdin.close();
			in.close();
			out.close();
			s.close();
		}
		MyXTraceContext.endTrace(traceRep);
		/*end the trace*/
		
	} 
	

}
