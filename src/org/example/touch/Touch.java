
package org.example.touch;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

/**
 *	서버와의 통신을 위한 서버아이피 및 포트를 설정하는 엑티비티이다.
 * 	서버와 연결후 어플리케이션 객체인 AppDelegate에 소캣 정보를 담아
 *  서버와 통신처리를 시작하며 다음 엑티비티로 이동한다.
 */

public class Touch extends Activity{
	private EditText ipField;
	private EditText portField;
	private AlertDialog alert;
	private AlertDialog network_alert;
	private SeekBar sensitivity;
	private boolean firstRun = true;
	
	
	private String getLocalServerIp()
	{
		try
		{
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
		    {
		        NetworkInterface intf = en.nextElement();
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
		        {
		            InetAddress inetAddress = enumIpAddr.nextElement();
		            if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress())
		            {
		            	return inetAddress.getHostAddress().toString();
		            }
		        }
		    }
		}
		catch (SocketException ex) {}
		return null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect);
		
		// 엘리먼트 가져오기
		ipField = (EditText) findViewById(R.id.EditText01);
		portField = (EditText) findViewById(R.id.EditText02);
		sensitivity = (SeekBar) findViewById(R.id.SeekBar01);

		// 기본 아이피 설정
		ipField.setText(getLocalServerIp());	
		portField.setText("5444");			// 기본포트
		
		// 연결에러시 다이얼로그
		alert = new AlertDialog.Builder(this).create();
	    alert.setTitle("서버 연결 에러");
	    alert.setMessage("서버시작후 연결해주세요.");
	    alert.setButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
	    
	    // 네트워크 에러 다이얼로그
	    network_alert = new AlertDialog.Builder(this).create();
	    network_alert.setTitle("네트워크 연결");
	    network_alert.setMessage("네트워크에 연결할수 없습니다.");
	    network_alert.setButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
	}

	@Override
	public void onResume(){
		Log.d("RESUME", "RESUMED");
		super.onResume();
		AppDelegate appDel = ((AppDelegate)getApplicationContext());
		// 연결이 되어 있지 않으면 얼렛 발생
		if(!appDel.connected && !firstRun){
			alert.show();
		}
		

		appDel.stopServer();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		firstRun = false;
	}
	
	// 이벤트 처리
	public void clickHandler(View view) {
		AppDelegate appDel = ((AppDelegate)getApplicationContext());
		int s = sensitivity.getProgress();
		// 마우스 감도 설정
		appDel.mouse_sensitivity = Math.round(s/20) + 1;
		
		if(!appDel.connected){
			String serverIp;
			int serverPort;
			
			serverIp = ipField.getText().toString();
			serverPort = Integer.parseInt(portField.getText().toString());
			// 서버 정보로 연결 처리 시도 
			appDel.createClientThread(serverIp, serverPort);
		}
		
		// 연결 연길시까지 일정 시간 딜레이 
		int x;
		for(x=0;x<4;x++){
			if(appDel.connected){
				startActivity(new Intent(view.getContext(), Controller.class));
				x = 6; // 6으로 주면 for 문 넘김
			}
			try{Thread.sleep(250);}
			catch(Exception e){}
		}
		
		// 연결되지 않았으면 얼렛 발생
		if(!appDel.connected)
			if(!appDel.network_reachable)
				network_alert.show();
			else
				alert.show();
	}
}
