package org.example.touch;

import android.app.Application;
import android.util.Log;
import java.net.*;

/**
 *  서버 통신용 어플리케이션 
 * 세션과 같은 역확을 하는 어플리케이션 객체이다.
 * 엑티비티가 종료되도 다른엑티비티에서 연결상태가 유지될수 
 * 있도록 어플리케이션 으로 구현하였다.
 */
public class AppDelegate extends Application {
	
	private ClientThread client;
	public int mouse_sensitivity = 1;
	public boolean connected = false;
	public boolean network_reachable = true;
	
	public void onCreate(){
		super.onCreate();
	}
	
    // 쓰레드 생성
	public void createClientThread(String ipAddress, int port){
		client = new ClientThread(ipAddress, port);
		
		Thread cThread = new Thread(client);
	    cThread.start();
	}
	
    // 메시지 전송
	public void sendMessage(String message){
		client.sendMessage(message);
	}
	
    // 서버가 연결되어 있으면 닫기
	public void stopServer(){
		if(connected){
			client.closeSocket();
		}
	}
	
	// 쓰레드 구현 부분
     public class ClientThread implements Runnable {
    	
    	private InetAddress serverAddr;
    	private int serverPort;
    	private DatagramSocket socket;
    	byte[] buf = new byte[1000];
    	
    	public ClientThread(String ip, int port){
    		try{
                // 서버 정보 
    			serverAddr = InetAddress.getByName(ip);
    		}
    		catch (Exception e){
    			Log.e("ClientActivity", "C: Error", e);
    		}
    		serverPort = port;
    	}

        public void run() {
            try {
                // UDP 연결 생성
                socket = new DatagramSocket();
                socket.setSoTimeout(1000);
                connected = testConnection();
                if(connected)
                	surveyConnection();
            }
            catch (Exception e) {
                Log.e("ClientActivity", "Client Connection Error", e);
            }
        }
        
        public void sendMessage(String message){
    		try {
                buf = message.getBytes();
                DatagramPacket out = new DatagramPacket(buf, buf.length, serverAddr, serverPort);
                socket.send(out);
                Log.d("ClientActivity", "Sent." + message);
                network_reachable = true;
            }
    		catch (Exception e){ 
    			Log.e("ClientActivity", "Client Send Error:");
    			if(e.getMessage().equals("Network unreachable")){
    				Log.e("ClientActivity", "Netork UNREACHABLE!!!!:");
    				network_reachable = false;
    			}
    			closeSocketNoMessge();
    		}
        }
        
        public void closeSocketNoMessge(){
        	socket.close();
        	connected = false;
        }
        
        // udp 소캣 닫기
        public void closeSocket(){
        	sendMessage(new String("Close"));
        	socket.close();
        	connected = false;
        }
        
        // 연결처리 부분
        private boolean testConnection(){
	        	try {
		        	 Log.d("Testing", "Sending");
		        	 
		        	 if(!connected)buf = new String("Connectivity").getBytes();
		        	 else buf = new String("Connected").getBytes();
		        	 
		             DatagramPacket out = new DatagramPacket(buf, buf.length, serverAddr, serverPort);
		             socket.send(out);
		             Log.d("Testing", "Sent");
		        	}
	        	catch(Exception e){return false;}
	        	
	        	try{
	        		Log.d("Testing", "Receiving");
	        		DatagramPacket in = new DatagramPacket(buf, buf.length);
	        		socket.receive(in);
	        		Log.d("Testing", "Received");
	        		return true;
	        	}
	        	catch(Exception e){return false;}
        }
        
        // 최대 3회까지 연결후 안되면 연결 종료
        private void surveyConnection(){
        	int count = 0;
        	while(connected){
        		try{Thread.sleep(1000);}
	        	catch(Exception e){}
	        	
        		if(!testConnection())
        			count++;
        		else
        			count = 0;
        		
        		if(count == 3){
        			closeSocket();
        			return;
        		}
        	}
        }
         
    }
}
