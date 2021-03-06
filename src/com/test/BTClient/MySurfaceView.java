package com.test.BTClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.BTClient.Protocol;

//import com.test.BTClient.BTClient;
//Runnable接口方法创建线程，匿名类
@SuppressLint("NewApi")
public class MySurfaceView extends SurfaceView  implements Callback, Runnable {

	private Thread th;
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint paint;
	private boolean flag;
	
	//BTClient btApp;
	
	//
	private float SCREEN_WIDTH=940;
	private float SCREEN_HEIGHT=520;
	
	private float LEFT_CENTERX=150;
	private float LEFT_CENTERY=150;
	private float RIGHT_CENTERX=SCREEN_WIDTH-LEFT_CENTERX;
	private float RIGHT_CENTERY=150;
	
	private float BACK_RECT_SIZE=140;//
	//固定摇杆背景圆形的X,Y坐标以及半径
	private float RockerCircleX = LEFT_CENTERX;
	private float RockerCircleY= LEFT_CENTERY;
	private float RockerCircleR;
	//固定遥
	private float BackRectLeft=LEFT_CENTERX-BACK_RECT_SIZE;
	private float BackRectTop=LEFT_CENTERY-BACK_RECT_SIZE;
	private float BackRectRight=LEFT_CENTERX+BACK_RECT_SIZE;
	private float BackRectButtom=LEFT_CENTERY+BACK_RECT_SIZE;
	//摇杆的X,Y坐标以及摇杆的半径
	public float SmallRockerCircleX = LEFT_CENTERX;
	public float SmallRockerCircleY = LEFT_CENTERY;
	private float SmallRockerCircleR = 20;
	
	//固定摇杆背景圆形的X,Y坐标以及半径
	private float RockerCircleX2 = RIGHT_CENTERX;
	private float RockerCircleY2 = RIGHT_CENTERY;
	private float RockerCircleR2;
	//固定遥
	private float BackRectLeft2=RIGHT_CENTERX-BACK_RECT_SIZE;
	private float BackRectTop2=RIGHT_CENTERY-BACK_RECT_SIZE;
	private float BackRectRight2=RIGHT_CENTERX+BACK_RECT_SIZE;
	private float BackRectButtom2=RIGHT_CENTERY+BACK_RECT_SIZE;
	//摇杆的X,Y坐标以及摇杆的半径
	public float SmallRockerCircleX2 = RIGHT_CENTERX;
	public float SmallRockerCircleY2 = RIGHT_CENTERY;
	private float SmallRockerCircleR2 = 20;
	public float stickHomeY1=-1;
	public int altCtrlMode=0;
	//
	public float leftTouchStartX=LEFT_CENTERX,leftTouchStartY=LEFT_CENTERY,rightTouchStartX=RIGHT_CENTERX,rightTouchStartY=RIGHT_CENTERY;
	//锁定yaw
	static final int YAW_STOP_CONTROL=0;
	
	
	public boolean leftTouching=false,rightTouching=false;
	private int leftTouchIndex=0,rightTouchIndex=0;

	public boolean touchReadyToSend=false;
	
	public MySurfaceView(Context context, AttributeSet attrs) {
		  	super(context, attrs);
		  	Log.v("Himi", "MySurfaceView");
			//this.setKeepScreenOn(true);
			sfh = this.getHolder();
		 	sfh.addCallback(this);
			paint = new Paint();
			paint.setAntiAlias(true);
			setFocusable(true);
			setFocusableInTouchMode(true);  
			
			
			try{
				
				//决定了摇杆的大小，the layout_height(px) determine the size of stick,
				String heightS = attrs.getAttributeValue("http://schemas.android.com/apk/res/android","layout_height");//"http://schemas.android.com/apk/res/android", 
				String widthS=attrs.getAttributeValue("http://schemas.android.com/apk/res/android","layout_width"); 
				heightS=heightS.substring(0, heightS.indexOf("p")-2);//150px
				widthS=widthS.substring(0, widthS.indexOf("p")-2); 
				int height= Integer.parseInt(heightS);
				int width= Integer.parseInt(widthS);
			//	 attrs.getA
				Log.v("viewSize","height:"+ heightS + "  Width:"+widthS);
				//Log.v("viewSize","height:"+ Integer.toString(height) + "  Width:"+Integer.toString(width));
				stickSizeInit(width,height);	//Obain the height of this view
			}
			catch(Exception e){ 
				Log.e("err","No px" + e.getMessage()); 
			}
		  // TODO Auto-generated constructor stub
		 }  

	public void surfaceCreated(SurfaceHolder holder) { 
		th = new Thread(this);
		flag = true;
		th.start();
	}

	//Stick Size init
	private void stickSizeInit(int screenWidth,int screenHeight)
	{
		 LEFT_CENTERX=screenHeight/2;
		 LEFT_CENTERY=screenHeight/2;
		 RIGHT_CENTERX=screenWidth-LEFT_CENTERX;
		 RIGHT_CENTERY=screenHeight/2;
		 BACK_RECT_SIZE=screenHeight/2-20;
		 
		 RockerCircleX  = LEFT_CENTERX;
		 RockerCircleY  = LEFT_CENTERY; 
		 RockerCircleR=(float) ((BackRectRight-BackRectLeft)/2 * 1.41421);
			//固定摇杆背景圆形的X,Y坐标以及半径
		 RockerCircleX2 = RIGHT_CENTERX;
		 RockerCircleY2 = RIGHT_CENTERX; 
		 RockerCircleR2= RockerCircleR;
		 
		 BackRectLeft =LEFT_CENTERX-BACK_RECT_SIZE;
		 BackRectTop =LEFT_CENTERY-BACK_RECT_SIZE;
		 BackRectRight =LEFT_CENTERX+BACK_RECT_SIZE;
		  BackRectButtom =LEFT_CENTERY+BACK_RECT_SIZE;
			//摇杆的X,Y坐标以及摇杆的半径
		  SmallRockerCircleX = LEFT_CENTERX;
		  SmallRockerCircleY = BackRectButtom;
		  SmallRockerCircleR = 100; 
			//固定遥
			 BackRectLeft2=RIGHT_CENTERX-BACK_RECT_SIZE;
			  BackRectTop2=RIGHT_CENTERY-BACK_RECT_SIZE;
			 BackRectRight2=RIGHT_CENTERX+BACK_RECT_SIZE;
			 BackRectButtom2=RIGHT_CENTERY+BACK_RECT_SIZE;
			//摇杆的X,Y坐标以及摇杆的半径
			 SmallRockerCircleX2 = RIGHT_CENTERX;
			 SmallRockerCircleY2 = RIGHT_CENTERY;
			SmallRockerCircleR2 = 100;
			
	}
	/***
	 * 得到两点之线与x轴的弧度
	 */
	public double getRad(float px1, float py1, float px2, float py2) {
		//得到两点X的距离
		float x = px2 - px1;
		//得到两点Y的距离
		float y = py1 - py2;
		//算出斜边长
		float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		//得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
		float cosAngle = x / xie;
		//通过反余弦定理获取到其角度的弧度
		float rad = (float) Math.acos(cosAngle);
		//注意：当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
		if (py2 < py1) {
			rad = -rad;
		}
		return rad;
	}
	//采用简化的方案来实现双摇杆控制
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	try{
		int pointNum=event.getPointerCount();
		//TextView textView1=new TextView("Pos");
		
		float x1,y1,x2,y2;	//Touch Positon
		float leftX=0,leftY=0,rightX=0,rightY=0;
		//boolean leftTouch=false,rightTouch=false;

		final float DIVIDE_X=(LEFT_CENTERX+RIGHT_CENTERX)/2;
		
		/*Release touch*/
		switch ( (event.getAction() & MotionEvent.ACTION_MASK)) 
		{	
		case MotionEvent.ACTION_UP:	//the last release
			Log.v("TouchPos","ACTION_UP");
			Log.v("TouchPos","PointNum:"+Integer.toString(event.getPointerCount()) + ",actionIndex:"+Integer.toString(event.getActionIndex()) );
			Log.v("TouchPos","X:"+Float.toString(event.getX()) + ";Y:"+Float.toString(event.getY())    );
			leftTouching=false;rightTouching=false; 
			//放手归位
			SmallRockerCircleX = LEFT_CENTERX;//SmallRockerCircleY = LEFT_CENTERY;//油门不回中
//			if(stickHomeY1>=0)
//				SmallRockerCircleY=stickHomeY1; 	//油门回中由模式决定
			if(altCtrlMode==1)	//定高爬升
				SmallRockerCircleY= LEFT_CENTERY;
			
			SmallRockerCircleX2 = RIGHT_CENTERX; 
			SmallRockerCircleY2 = RIGHT_CENTERY;
			
			leftTouchStartX=LEFT_CENTERX;
			if(altCtrlMode==1)
				leftTouchStartY=LEFT_CENTERY;
			
			rightTouchStartX=RIGHT_CENTERX;
			rightTouchStartY= RIGHT_CENTERY;
			
			break;
		case MotionEvent.ACTION_POINTER_UP://first release if two finger is touching
			if(event.getX(event.getActionIndex())<DIVIDE_X)
			{
				leftTouching=false;
				SmallRockerCircleX = LEFT_CENTERX;
			//	SmallRockerCircleY = LEFT_CENTERY; //油门不回中
				if(altCtrlMode==1)	//定高爬升
					SmallRockerCircleY= LEFT_CENTERY;
				
				//	if(stickHomeY1>=0)
				//		SmallRockerCircleY=stickHomeY1; 	//油门回中由模式决定
				
				leftTouchStartX=LEFT_CENTERX;
				leftTouchStartY=LEFT_CENTERY;
				
				rightTouchIndex=0;
			}
			else
			{
				rightTouching=false; 
				SmallRockerCircleX2 = RIGHT_CENTERX;
				SmallRockerCircleY2 = RIGHT_CENTERY;
				rightTouchStartX=RIGHT_CENTERX;
				rightTouchStartY= RIGHT_CENTERY;
			}
			
			/*两指按下后，初指释放再按下出错问题未解决
			if(event.getActionIndex()==leftTouchIndex)	//first release  
			{
				leftTouching=false;
				SmallRockerCircleX = LEFT_CENTERX;
				//SmallRockerCircleY = LEFT_CENTERY; //油门不回中
				rightTouchIndex=0;
			}
			else
			{
				rightTouching=false;
				SmallRockerCircleX2 = RIGHT_CENTERX;
				SmallRockerCircleY2 = RIGHT_CENTERY;
				leftTouchIndex=0;
			}*/
			Log.v("TouchPos","ACTION_POINTER_UP");
			Log.v("TouchPos","PointNum:"+Integer.toString(event.getPointerCount()) + ",actionIndex:"+Integer.toString(event.getActionIndex()) );
			break;
	/*	case MotionEvent.ACTION_DOWN:
			Log.v("TouchPos","ACTION_DOWN");
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.v("TouchPos","ACTION_POINTER_DOWN");
			break;
			  */
		} 
		
		/*get touch*/
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE  || (event.getAction() & MotionEvent.ACTION_MASK)==MotionEvent.ACTION_POINTER_DOWN) 
			{
			/* //	两指按下后，初指释放再按下出错问题未解决
				if((event.getAction() & MotionEvent.ACTION_MASK)==MotionEvent.ACTION_DOWN)//first down
				{
					x1=event.getX(event.getActionIndex());y1=event.getY(event.getActionIndex());
					if(leftTouching==false && rightTouching==false)
					{
						if(x1<DIVIDE_X)
						{
						 	leftTouchIndex=event.getActionIndex();
							leftTouching=true; 
						}
						else
						{
						 rightTouchIndex=event.getActionIndex();
							rightTouching=true; 
						}
						 
					}
					Log.v("TouchPos","ACTION_DOWN,"+Integer.toString(event.getActionIndex())+",X: "+Float.toString(x1)+ "Y:"+Float.toString(y1));
				}
				if((event.getAction() & MotionEvent.ACTION_MASK)==MotionEvent.ACTION_POINTER_DOWN)
				{
					
					x2=event.getX(event.getActionIndex());y2=event.getY(event.getActionIndex());
					if(leftTouching==false && x2<DIVIDE_X)
					{ 
						 	leftTouchIndex=event.getActionIndex();
							leftTouching=true; 	 
					}
					if(rightTouching==false && x2>=DIVIDE_X)
					{ 
						 	rightTouchIndex=event.getActionIndex();
							rightTouching=true;  
					}
					Log.v("TouchPos","ACTION_POINTER_DOWN,"+Integer.toString(event.getActionIndex())+",X: "+Float.toString(x2)+ "Y:"+Float.toString(y2));
				} */
				/*	//Judge left or right BUG:当两指触住后，再放开第一个手指（第二手指保持按住,index->0），再按下一第一个手指，index会反转回复(index反转)，因此显示出错。
					x1=event.getX();y1=event.getY();
					if(pointNum==1)//first finger touches
					{
						if(leftTouching==false && rightTouching==false)
						{
							if(x1<DIVIDE_X)
							{
								leftTouchIndex=0;
								leftTouching=true;
							}
							else
							{
								rightTouchIndex=0;
								rightTouching=true;
							}
						}
					}
					if(pointNum>1)//second finger touches
					{
						x2=event.getX(1);y2=event.getY(1);
						if(leftTouching==false && x2<DIVIDE_X)
						{ 
								leftTouchIndex=1;
								leftTouching=true; 	
						}
						if(rightTouching==false && x2>=DIVIDE_X)
						{ 
								rightTouchIndex=1;
								rightTouching=true; 
						}
					}
						if(leftTouching==true)
					{
						leftX=event.getX(leftTouchIndex);
						leftY=event.getY(leftTouchIndex);
					
						
					}
					if(rightTouching==true)
					{	
						rightX=event.getX(rightTouchIndex);
						rightY=event.getY(rightTouchIndex);
					
					}	
					*/
					if(pointNum==1)
					{
						x1=event.getX();y1=event.getY();
						if(x1<DIVIDE_X)
						{	
							leftX=x1;leftY=y1;
							if(leftTouching==false)
							{
								leftTouchStartX=leftX;
								leftTouchStartY=leftY;
								leftTouching=true;
							}
							
						}
						else if(x1>=DIVIDE_X)
						{
							rightX=x1;rightY=y1;
							if(rightTouching==false)
							{	
								rightTouchStartX=rightX;
								rightTouchStartY=rightY;
								rightTouching=true;
							}
							
						} 
					}
					else if(pointNum>1)
					{
						x1=event.getX();y1=event.getY(); x2=event.getX(1);y2=event.getY(1);
						if(x1<x2 )
						{
							if(x1<DIVIDE_X)
							{
								leftX=x1;leftY=y1;
								if(leftTouching==false)
								{
									leftTouchStartX=leftX;
									leftTouchStartY=leftY;
									leftTouching=true;
								}
							
							}
							if(x2>DIVIDE_X)
							{
								rightX=x2;rightY=y2;
								if(rightTouching==false)
								{	
									rightTouchStartX=rightX;
									rightTouchStartY=rightY;
									rightTouching=true;
								}
							}
						}
						else
						{
							if(x2<DIVIDE_X)
							{leftX=x2;leftY=y2;
								if(leftTouching==false)
								{
									leftTouchStartX=leftX;
									leftTouchStartY=leftY;
									leftTouching=true;
								}
							}
							if(x1>DIVIDE_X)
							{rightX=x1;rightY=y1;
								if(rightTouching==false)
								{	
									rightTouchStartX=rightX;
									rightTouchStartY=rightY;
									rightTouching=true;
								}
							}
						}
					} 
					//Log.v("TouchFinger", "left: "+ Integer.toString(leftTouchIndex)+ "  right: "+Integer.toString(rightTouchIndex));	

					/**Process movement**/
					if(leftTouching==true )//Left Stick is touched
					{
 						Log.v("TouchPos", "leftX: "+Float.toString(leftX)+ "  leftY: "+Float.toString(leftY));
				//		if(leftX>=BackRectLeft && leftX<=BackRectRight && leftY>=BackRectTop && leftY<=BackRectButtom)
				//		{
							SmallRockerCircleX = leftX;
							SmallRockerCircleY = leftY;
				//		}
				//		else
						{//限定在矩形内
					/*		if(leftX>BackRectRight)
								SmallRockerCircleX=BackRectRight;
							else if( leftX<BackRectLeft )
								SmallRockerCircleX=BackRectLeft;
							else
								SmallRockerCircleX=leftX;
							if(leftY>BackRectButtom )
								SmallRockerCircleY=BackRectButtom;
							else if(leftY<BackRectTop)
								SmallRockerCircleY=BackRectTop;
							else
								SmallRockerCircleY=leftY;*/
						} 
						Log.v("RightStickY",Float.toString(SmallRockerCircleY) + " " + Float.toString(BackRectButtom));
						
						//Protocol.throttle=(int)SmallRockerCircleY;
						
						
						//Protocol.yaw=(int);
					} 
					//Right Stick is touched
					if(rightTouching==true )
					{
 						Log.v("TouchPos", "rightX: "+Float.toString(rightX)+ "  rightY: "+Float.toString(rightY));
				//		if(rightX>=BackRectLeft2 && rightX<=BackRectRight2 && rightY>=BackRectTop2 && rightY<=BackRectButtom2)
				//		{
							SmallRockerCircleX2 = rightX;
							SmallRockerCircleY2 = rightY;
				//		}
				//		else
						{//限定在矩形内
						/*	if(rightX>BackRectRight2)
								SmallRockerCircleX2=BackRectRight2;
							else if( rightX<BackRectLeft2 )
								SmallRockerCircleX2=BackRectLeft2;
							else
								SmallRockerCircleX2=rightX;
							if(rightY>BackRectButtom2 )
								SmallRockerCircleY2=BackRectButtom2;
							else if(rightY<BackRectTop2)
								SmallRockerCircleY2=BackRectTop2;
							else
								SmallRockerCircleY2=rightY;*/
						} 
						Log.v("RightStickY",Float.toString(SmallRockerCircleY2) + " " + Float.toString(BackRectButtom2));
					//	byte[] ad={0xa,0xd};
					//	btApp.btSendBytes(ad);
					}
					
					
			} 
		Log.i("touch",Integer.toString((int)leftTouchStartX)+" " + Integer.toString((int)leftTouchStartY) +" " 
				+ Integer.toString((int)rightTouchStartX) + " "+Integer.toString((int)rightTouchStartY));
		
		if(YAW_STOP_CONTROL==1)
			SmallRockerCircleX=LEFT_CENTERX;	//暂不控制yaw，避免控油门时误点乱转
	//	Protocol.throttle=(int)(1000+1000*(BackRectButtom-SmallRockerCircleY)/(BackRectButtom-BackRectTop));
	//	Protocol.yaw=(int)(1000+1000*(SmallRockerCircleX-BackRectLeft)/(BackRectRight-BackRectLeft));
	//	Protocol.pitch=(int)(1000+1000*(BackRectButtom2-SmallRockerCircleY2)/(BackRectButtom2-BackRectTop2));
	//	Protocol.roll=(int)(1000+1000*(SmallRockerCircleX2-BackRectLeft2)/(BackRectRight2-BackRectLeft2));
		
	//	Protocol.yaw=(int)(1500+1000*(SmallRockerCircleX-LEFT_CENTERX)/(BackRectRight-BackRectLeft));
	//	Protocol.pitch=(int)(1500+1000*(RIGHT_CENTERY-SmallRockerCircleY2)/(BackRectButtom2-BackRectTop2));
	//	Protocol.roll=(int)(1500+1000*(SmallRockerCircleX2-RIGHT_CENTERX)/(BackRectRight2-BackRectLeft2));
		//落手为起点,油门除外
		if(altCtrlMode==0)
			Protocol.throttle=(int)(1000+1000*(BackRectButtom-SmallRockerCircleY)/(BackRectButtom-BackRectTop));
		else
			Protocol.throttle=(int)(1500-1000*(SmallRockerCircleY-leftTouchStartY)/(BackRectButtom-BackRectTop));
		Protocol.yaw=(int)(1500+1000*((SmallRockerCircleX-leftTouchStartX))/(BackRectRight-BackRectLeft));
		Protocol.pitch=(int)(1500+1000*(0-(SmallRockerCircleY2-rightTouchStartY))/(BackRectButtom2-BackRectTop2));
		Protocol.roll=(int)(1500+1000*((SmallRockerCircleX2-rightTouchStartX))/(BackRectRight2-BackRectLeft2));
		
		Protocol.throttle=constrainRange(Protocol.throttle,1000,2000);
		Protocol.yaw=constrainRange(Protocol.yaw,1000,2000);
		Protocol.pitch=constrainRange(Protocol.pitch,1000,2000);
		Protocol.roll=constrainRange(Protocol.roll,1000,2000);
		
		
		Log.i("Debug",Integer.toString(Protocol.yaw)+Integer.toString(Protocol.throttle)+Integer.toString(Protocol.pitch)+Integer.toString(Protocol.roll));
		touchReadyToSend=true;
	}
	catch(Exception e){//stick turn out error
		Log.e("stickError","stickError");
		SmallRockerCircleX=LEFT_CENTERX;SmallRockerCircleY=LEFT_CENTERY;
		SmallRockerCircleX2=RIGHT_CENTERX;SmallRockerCircleY2=RIGHT_CENTERY;
		leftTouching=false;
		rightTouching=false;
		leftTouchIndex=0;
		rightTouchIndex=0;
	}
		return true;
	}
	
	public int constrainRange(int x,int min,int max)
	{
		if(x<min) x=min;
		if(x>max) x=max;
		
		return x;
		
	}
	
	public int rc2StickPosY(int rc)
	{
		int posY=0;
		posY=(int)(BackRectButtom-(BackRectButtom-BackRectTop)*(rc-1000)/1000.0);
		return posY;
	}
	/**
	 * 
	 * @param R
	 *            圆周运动的旋转点
	 * @param centerX
	 *            旋转点X
	 * @param centerY
	 *            旋转点Y
	 * @param rad
	 *            旋转的弧度
	 */
	public void getXY(float centerX, float centerY, float R, double rad) {
		//获取圆周运动的X坐标 
		SmallRockerCircleX = (float) (R * Math.cos(rad)) + centerX;
		//获取圆周运动的Y坐标
		SmallRockerCircleY = (float) (R * Math.sin(rad)) + centerY;
	}
	
	

	public void draw() {
		try {
			canvas = sfh.lockCanvas();
			canvas.drawColor(Color.BLACK);
			//设置颜色
			//绘制摇杆背景
		 	///paint.setColor(Color.YELLOW); 
		 	///canvas.drawCircle(RockerCircleX, RockerCircleY, RockerCircleR, paint);
			paint.setColor(Color.WHITE); 
			canvas.drawRect(BackRectLeft,BackRectTop,BackRectRight,BackRectButtom,paint);///
			//绘制摇杆
			paint.setColor(0x4F94CD00); 
			canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY, SmallRockerCircleR, paint);
			//Draw another Right one
			paint.setColor(Color.WHITE); 
			canvas.drawRect(BackRectLeft2,BackRectTop2,BackRectRight2,BackRectButtom2,paint);///
			//paint.setColor(0x70ff0000); 
			paint.setColor(0x4F94CD00); 
			canvas.drawCircle(SmallRockerCircleX2, SmallRockerCircleY2, SmallRockerCircleR2, paint);
		
		
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (canvas != null)
					sfh.unlockCanvasAndPost(canvas);
			} catch (Exception e2) {

			}
		}
	}
//-----复写 线程的run操作，当surface被创建后，线程开启
	public void run() {
		// TODO Auto-generated method stub
		//
		while (flag) {	
			draw();
			try {
				Thread.sleep(50);	//线程休眠50ms
			} catch (Exception ex) {
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v("Himi", "surfaceChanged");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
		Log.v("Himi", "surfaceDestroyed");
	}
}