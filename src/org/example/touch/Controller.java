package org.example.touch;

import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;


/**
 *	컨트롤러 엑티비티로 서버와 연결후 키보드 이벤트 및 마우스 이벤트를 
 * 	받아 서버로 넘겨주는 엑티비티이다.
 * 	소캣통신은 어플리케이션 객체인 AppDelegate를 통해서 통신한다.
 *  실제 마우스 및 키보드역활을 하는 엑티비티이다.
 */
public class Controller extends TabActivity implements OnTouchListener, OnKeyListener{
	
	int lastXpos = 0;
	int lastYpos = 0;
	boolean keyboard = false;
	Thread checking;

	// 여러 이벤트를 서버에 전송할때 구분값이다.
	String delim = new String("!!");
	
	private class pptListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			AppDelegate appDel = ((AppDelegate)getApplicationContext());
			if(v.getId()==R.id.next_button){
				Log.d("ello", ""+KeyEvent.KEYCODE_DPAD_RIGHT);
				
				try{
	        		// 서버로 입력값 전송 
	        		char c='→';
					appDel.sendMessage("KEY"+delim+c);
	        	}
	        	catch(Exception e){}
				
			}
			else if(v.getId()==R.id.prev_button){
				Log.d("ello", ""+KeyEvent.KEYCODE_DPAD_LEFT);
				
				try{
				char c = '←';
			 	appDel.sendMessage("KEY"+delim+c);
				}
	        	catch(Exception e){}
			}
			else if(v.getId()==R.id.home){
				Log.d("ello", ""+KeyEvent.KEYCODE_HOME);
				try{
					String str = "home";
				 	appDel.sendMessage("ppt"+delim+str);
					}
		        	catch(Exception e){}
			}
			else if(v.getId()==R.id.f5){
				Log.d("ello", ""+135);	
				try{
					String str = "f5";
				 	appDel.sendMessage("ppt"+delim+str);
					}
		        	catch(Exception e){}
			}
			else if(v.getId()==R.id.shift){
				Log.d("ello", ""+KeyEvent.KEYCODE_SHIFT_LEFT);	
				try{
					String str = "shiftf5";
				 	appDel.sendMessage("ppt"+delim+str);
					}
		        	catch(Exception e){}
			}
			else if(v.getId()==R.id.end){
				Log.d("ello", ""+KeyEvent.KEYCODE_ENDCALL);	
				try{
					String str = "end";
				 	appDel.sendMessage("ppt"+delim+str);
					}
		        	catch(Exception e){}
			}
			else if(v.getId()==R.id.esc){
				Log.d("ello", ""+111);	
				try{
					String str = "esc";
				 	appDel.sendMessage("ppt"+delim+str);
					}
		        	catch(Exception e){}
			}
		}
	}
	// 엑티비티 시작시
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.control);
	    
	    TabHost mTabHost = getTabHost();
	    
	    ImageView tabwidget01 = new ImageView(this);
        tabwidget01.setImageResource(R.drawable.tab_button);
        ImageView tabwidget02 = new ImageView(this);
        tabwidget02.setImageResource(R.drawable.tab_button2);
        
        mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
          .setIndicator(tabwidget01)
          .setContent(R.id.TouchPad));
        mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
          .setIndicator(tabwidget02)
          .setContent(R.id.pptTouch));

        mTabHost.setCurrentTab(0);
		
		// 화면 크기를 구해서
	 	Display display = getWindowManager().getDefaultDisplay(); 
	 	int width = display.getWidth();
	 	
	 	Button left = (Button) findViewById(R.id.LeftClickButton);
	 	Button right =  (Button) findViewById(R.id.RightClickButton);
	 	
	 	Button prev_button=(Button) findViewById(R.id.prev_button);
	 	Button next_button=(Button) findViewById(R.id.next_button);
	 	
	 	Button home = (Button) findViewById(R.id.home);
	 	Button f5 = (Button) findViewById(R.id.f5);
	 	Button shift = (Button) findViewById(R.id.shift);
	 	Button end = (Button) findViewById(R.id.end);
	 	Button esc = (Button) findViewById(R.id.esc);
	 	
	 	prev_button.setOnClickListener(new pptListener());
	 	next_button.setOnClickListener(new pptListener());
	 	home.setOnClickListener(new pptListener());
	 	f5.setOnClickListener(new pptListener());
	 	shift.setOnClickListener(new pptListener());
	 	end.setOnClickListener(new pptListener());
	 	esc.setOnClickListener(new pptListener());
	 	
	 	// 버튼 위취를 설정해준다.
	 	left.setWidth(width/2);
	 	right.setWidth(width/2);
	 	
	 	// 화면 터치시 위치값을 계산하기 위해 뷰터치 이벤트를 수신한다.
	    View touchView = (View) findViewById(R.id.TouchPad);
	    touchView.setOnTouchListener(this);
	    
	    View pptView = (View) findViewById(R.id.pptTouch);
	    pptView.setOnTouchListener(this);
	    
	    // 키보드를 띄우기 위해ㅓ 에디트 박스 설정
	    EditText editText = (EditText) findViewById(R.id.KeyBoard);
	    //editText.setVisibility(View.GONE);
	    editText.setOnKeyListener(this);
	    editText.addTextChangedListener(new TextWatcher(){
	    	// 키보드를 누른후에는 입력값 초기화
		    public void  afterTextChanged (Editable s){
		    	Log.d("seachScreen", ""+s);
		    	s.clear();	
	        } 
	        public void  beforeTextChanged  (CharSequence s, int start, int count, int after){ 
	                Log.d("seachScreen", "beforeTextChanged"); 
	        } 
	        public void  onTextChanged  (CharSequence s, int start, int before, int count) {
	        	AppDelegate appDel = ((AppDelegate)getApplicationContext());
	        	
	        	try{
	        		// 서버로 입력값 전송 
	        		char c = s.charAt(start);
	        		appDel.sendMessage("KEY"+delim+c);
	        	}
	        	catch(Exception e){}
	        }
	    });
	    
	}
	
	// 엑티비티 시작시
	public void onStart(){
		super.onStart();
		// 서버 쓰레드 loop 가동 
		AppDelegate appDel = ((AppDelegate)getApplicationContext());
		sendToAppDel(new String("Mouse Sensitivity!!"+appDel.mouse_sensitivity));
		
			new Thread(new Runnable(){
				AppDelegate appDel = ((AppDelegate)getApplicationContext());
				public void run(){
					while(appDel.connected){
						try{Thread.sleep(1000);}
						catch(Exception e){};
						if(!appDel.connected){
							finish();
						}
					}
				}
			}).start();
	}

	// 마우스 터치시 	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 이벤트 전송
		mousePadHandler(event);
	 	return true;
	}
	
	// 키보드 입력시
	// 서버로 전송

	public boolean onKey(View v, int c, KeyEvent event){
		Log.d("ello", ""+event.getKeyCode());
		AppDelegate appDel = ((AppDelegate)getApplicationContext());
		
	 	appDel.sendMessage("S_KEY"+delim+event.getKeyCode());
		return false;
	}

	// 서버로 메시지 전달
	private void sendToAppDel(String message){
		AppDelegate appDel = ((AppDelegate)getApplicationContext());
		if(appDel.connected){
			appDel.sendMessage(message);
		}
		else{
			finish();
		}
	}

	// 마우스 이벤트에 따라 입력값 스트링 조립 
    private void mousePadHandler(MotionEvent event) {
 	   StringBuilder sb = new StringBuilder();
 	   
 	   int action = event.getAction();
 	   int touchCount = event.getPointerCount();
 	   
	   // if a single touch
	   // send movement based on action
 	   if(touchCount == 1){
 	   		// 마우스의 움직임에 따라 
			switch(action){
				case 0: sb.append("DOWN"+delim);
						sb.append((int)event.getX()+delim);
						sb.append((int)event.getY()+delim);
						break;
				
				case 1: sb.append("UP"+delim);
						sb.append(event.getDownTime()+delim);
						sb.append(event.getEventTime());
						break;
				
				case 2: sb.append("MOVE"+delim);
						sb.append((int)event.getX()+delim);
						sb.append((int)event.getY());
						break;
						
				default: break;
			}
 	   }

	   // 투터치시는 스크롤로 간주한다.
 	   else if(touchCount == 2){		// 터치이벤트가 2개일시 즉 투터치시
 		   sb.append("SCROLL"+delim);
 		   if(action == 2){
 			  sb.append("MOVE"+delim);
 			  sb.append((int)event.getX()+delim);
			  sb.append((int)event.getY());
 		   }
 		   else
 			   sb.append("DOWN");
 	   }
 	   
 	  sendToAppDel(sb.toString());
 	}
    
    // 왼쪽 마우스 버튼 클릭 효과 
    public void LeftButtonClickHandler(View v){
    	Log.d("eloo", "CLICKED");
    	sendToAppDel("CLICK"+delim+"LEFT");
    }
    
    // 오른쪽 마우스 클릭 효과 
    public void RightButtonClickHandler(View v){
    	sendToAppDel("CLICK"+delim+"RIGHT");
    }
    
	// 가상 키보드 숨김 및 보이기
    public void keyClickHandler(View v){
    	EditText editText = (EditText) findViewById(R.id.KeyBoard);
    	InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	
    	if(keyboard){
    		mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    		keyboard = false;
    	}
    	else{
    		mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    		keyboard = true;
    	}
    		
    	Log.d("SET", "Foucs");
    }
}
