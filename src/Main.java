/// processing core libraries
import processing.core.*;

import processing.video.*;
import processing.opengl.*;


public class Main extends PApplet{
	
	/// video data
	Movie theMov; 
	String videoPaths[] = {"../video/particle_fire_loop.mov","../video/station.mov","../video/particle_fire_loop.mov"};
	int videoCounter = 0;
	boolean isVideoPlaying = false;

	PImage bg;
	PImage texmap;

	int sDetail = 35;  // Sphere detail setting
	float rotationX = 0;
	float rotationY = 0;
	float velocityX = 0;
	float velocityY = 0;
	float globeRadius = 600;
	float pushBack = 0;

	float[] cx, cz, sphereX, sphereY, sphereZ;
	float sinLUT[];
	float cosLUT[];
	float SINCOS_PRECISION = 0.5f;
	int SINCOS_LENGTH = (int)(360.0f / SINCOS_PRECISION);

	double[][] coords = {
	{47.479246,19.158606},
	{41.588822,-93.620309},
	{39.755092,-104.988123},
	{47.479246,19.158606},
	{50.503887,4.469936},
	{40.010492,-105.276843},
	{47.479246,19.158606},
	{45.423494,-75.697933},
	{39.099233,-84.517486},
	{38.256065,-85.751540},
	{39.099233,-84.517486},
	{47.479246,19.158606},
	{47.479246,19.158606},
	{37.775196,-122.419204}
	};

	double[][] coords2 = {
	{33.581954,-111.899936,33.421948,-111.939932},
	{35.759573,-79.019300,35.929681,-79.034165},
	{35.929681,-79.034165,35.915333,-79.084912},
	{50.704272,12.807654,41.381448,-93.688202},
	{35.852265,-86.401010,40.756054,-73.986951},
	{-2.634111,39.651816,41.923348,-87.978221},
	{47.620973,-122.347276,34.956578,-81.048405},
	{35.759573,-79.019300,35.929681,-79.034165},
	{35.929681,-79.034165,35.915333,-79.084912},
	{50.704272,12.807654,41.381448,-93.688202},
	{35.852265,-86.401010,40.756054,-73.986951},
	{-2.634111,39.651816,41.923348,-87.978221},
	{47.620973,-122.347276,34.956578,-81.048405},
	{33.581954,-111.899936,33.421948,-111.939932}
	};
	
	
	public void setup(){
		// size(1024, 768, P3D);  
		  size(1024, 768, OPENGL);  
		  // size(1024, 768, OPENGL);  

		  texmap = loadImage("../data/PathfinderMap.jpg");
		  
		  switchVideo();

		  initializeSphere(sDetail);
	}
	
	public void draw(){    
	  background(0);            
	  renderGlobe(); 
	  if(isVideoPlaying){
		  image(theMov, 0, 0); 
	  }
	}
	

	////////////////////////////////////
	/////////// VIDEO CONTROL /////////////////
	public void switchVideo(){
		
		/// change the path of the video
		initVideo();
	}
	
	public void initVideo(){
		isVideoPlaying = true;
		
		 theMov = new Movie(this, videoPaths[videoCounter]);
		 // theMov.play();  //plays the movie once
		 theMov.loop();  //plays the movie over and over
	}
	
	
	////
	void movieEvent(Movie tm) { 
		 tm.read(); 
	} 
	
	/// switch video //////
	
	
	
	
	/////////   end video functions   ///////
	////////////////////////////////////
	public void myMark(float x, float y, float z, float len){
	  // scale(len,len,len);
	  // translate(x,y,z);
	  // sphere(len);
	  line(x,y,z,x+len,y,z);
	}
	float prad = 277;

	public void renderGlobe(){
	  pushMatrix();
	  translate(width/2.0f, height/2.0f, pushBack);
	  pushMatrix();
	  noFill();
	  stroke(255,200);
	  strokeWeight(2);
	  smooth();
	  popMatrix();
	  lights();    
	  pushMatrix();
	  rotateX( radians(-rotationX) );  
	  rotateY( radians(270 - rotationY) );
	  fill(200,200,100);
	  stroke(255,255,0);
	  // noStroke();
	  sphereDetail(6);

	  float m = millis();
	  float mRatio = ((long) m % (30*1000)) / (30*1000.0f);
	  int animIdx = (int) ((1-mRatio) * coords.length);
	  int n = 0;

	  strokeWeight(2);
	  for (int i = 0; i < coords.length; ++i){
	      float lat = (float)coords[i][0];
	      float lon = (float)coords[i][1];
	      int elapsed = animIdx - i;
	      if (elapsed < 0 || elapsed > 100)
	        continue;

	      pushMatrix();
	      rotateY( radians(lon));
	      rotateZ( radians(-lat));
	      
	 
	      float r = (animIdx-i)/100.0f;
	      r = 1 - r;
	      stroke(255-r*128,255-r*128,r*128);
	      myMark(prad, 0, 0, r* 100);
	      
	      popMatrix();
	      ++n;
	  }

	//// this draws lines
	  stroke(255,0,0);
	  noFill();
	  float anim2IdxF = (1-mRatio) * coords2.length;
	  int anim2Idx = (int) anim2IdxF;
	  bezierDetail(30);
	  strokeWeight(2);
	  
	  for (int i = 0; i < coords2.length; ++i){
	      int elapsed = anim2Idx - i;
	      if (elapsed < 0 || elapsed > 10)
	        continue;

	      float lat = (float)coords2[i][0];
	      float lon = (float)coords2[i][1];
	      float lat2 = (float)coords2[i][2];
	      float lon2 = (float)coords2[i][3];
	      // int elapsed = animIdx - i;
	      
	      float r = -cos( radians(-lat) ) * prad;
	      float y = sin( radians(-lat) )*prad;
	      float x = -cos( radians(lon) )*r;      
	      float z = sin( radians(lon) )*r;      

	      float r2 = -cos( radians(-lat2) ) * prad;
	      float y2 = sin( radians(-lat2) )*prad;
	      float x2 = -cos( radians(lon2) )*r2;      
	      float z2 = sin( radians(lon2) )*r2;      

	      r = (anim2IdxF-i)/10.0f;
	      r = 1 - r;
	      int li = (int) min(30,r*60);
	      int fi = (int) max(0, r*60-30);

	      stroke(255,0,0);
	      // fill(255,255,128);
	      
	      /// this draws the connecting lines
	      beginShape();
	      for (int j = fi; j < li; ++j) {
	        float xp =  (float)bezierPoint(x,x+x/10,x2+x2/10,x2,j/30.0f);
	        float yp =  (float)bezierPoint(y,y+y/10,y2+y2/10,y2,j/30.0f);
	        float zp =  (float)bezierPoint(z,z+z/10,z2+z2/10,z2,j/30.0f);
	        vertex(xp,yp,zp);
	        // line(xp,yp,zp, xp+1,yp+1,zp+1);
	      }
	      endShape();
	      stroke(255,0,0);
	/*      bezier(x,y,z, 
	            x+x/10,y+y/10,z+z/10, 
	            x2+x2/10,y2+y2/10,z2+z2/10, 
	            x2,y2,z2);
	*/
	      ++n;
	  }


	  // println(animIdx + " " + n + " rendered");
	  fill(255);
	  noStroke();

	  textureMode(IMAGE);  
	  // int r = int((float) (Math.random()*Math.random()*nbrTextures));
	  texturedSphere(globeRadius, texmap);

	  popMatrix();  
	  popMatrix();
	  rotationX += velocityX;
	  rotationY += velocityY;
	  velocityX *= 0.95;
	  velocityY *= 0.95;
	  
	  // Implements mouse control (interaction will be inverse when sphere is  upside down)
	  if(mousePressed){
	    velocityX += (mouseY-pmouseY) * 0.01;
	    velocityY -= (mouseX-pmouseX) * 0.01;
	  }
	}

	public void initializeSphere(int res){
	  sinLUT = new float[SINCOS_LENGTH];
	  cosLUT = new float[SINCOS_LENGTH];

	  for (int i = 0; i < SINCOS_LENGTH; i++) {
	    sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
	    cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
	  }

	  float delta = (float)SINCOS_LENGTH/res;
	  float[] cx = new float[res];
	  float[] cz = new float[res];
	  
	  // Calc unit circle in XZ plane
	  for (int i = 0; i < res; i++) {
	    cx[i] = -cosLUT[(int) (i*delta) % SINCOS_LENGTH];
	    cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
	  }
	  
	  // Computing vertexlist vertexlist starts at south pole
	  int vertCount = res * (res-1) + 2;
	  int currVert = 0;
	  
	  // Re-init arrays to store vertices
	  sphereX = new float[vertCount];
	  sphereY = new float[vertCount];
	  sphereZ = new float[vertCount];
	  float angle_step = (SINCOS_LENGTH*0.5f)/res;
	  float angle = angle_step;
	  
	  // Step along Y axis
	  for (int i = 1; i < res; i++) {
	    float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
	    float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
	    for (int j = 0; j < res; j++) {
	      sphereX[currVert] = cx[j] * curradius;
	      sphereY[currVert] = currY;
	      sphereZ[currVert++] = cz[j] * curradius;
	    }
	    angle += angle_step;
	  }
	  sDetail = res;
	}

	// Generic routine to draw textured sphere
	public void texturedSphere(float r, PImage t){
	  int v1,v11,v2;
	  r = (r + 240 ) * 0.33f;
	  beginShape(TRIANGLE_STRIP);
	  texture(t);
	  float iu=(float)(t.width-1)/(sDetail);
	  float iv=(float)(t.height-1)/(sDetail);
	  float u=0,v=iv;
	  for (int i = 0; i < sDetail; i++) {
	    vertex(0, -r, 0,u,0);
	    vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
	    u+=iu;
	  }
	  vertex(0, -r, 0,u,0);
	  vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
	  endShape();   
	  
	  // Middle rings
	  int voff = 0;
	  for(int i = 2; i < sDetail; i++) {
	    v1=v11=voff;
	    voff += sDetail;
	    v2=voff;
	    u=0;
	    beginShape(TRIANGLE_STRIP);
	    texture(t);
	    for (int j = 0; j < sDetail; j++) {
	      vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r, u, v);
	      vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r, u, v+iv);
	      u+=iu;
	    }
	  
	    // Close each ring
	    v1=v11;
	    v2=voff;
	    vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r, u, v);
	    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v+iv);
	    endShape();
	    v+=iv;
	  }
	  u=0;
	  
	  // Add the northern cap
	  beginShape(TRIANGLE_STRIP);
	  texture(t);
	  for (int i = 0; i < sDetail; i++) {
	    v2 = voff + i;
	    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
	    vertex(0, r, 0,u,v+iv);    
	    u+=iu;
	  }
	  vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
	  endShape();
	  
	}	
	
	////////keyboard input
	public void keyPressed() {
		if (key == '-') {
			
			// targetZoom = max(targetZoom - 0.1f, 0.5f);
		}
		if (key == '=') {
			// targetZoom = min(targetZoom + 0.1f, 1.9f);
		}
		if (key == 'l') {
			// showLabels = !showLabels;
		}
		if(key == 'm'){
			
		}
		if(key == 't'){
			
		}
		/// this does nothing
		if (key == 'd') {
			println("COUNTER: " + videoCounter + " " + videoPaths.length + videoPaths[videoCounter]);
			videoCounter ++;
			if(videoCounter >= videoPaths.length){
				videoCounter = 0;
				println("COUNTER: " + videoCounter + " " + videoPaths[videoCounter]);
			}
			
			switchVideo();
		}
		if (key == 'f') {
			
		}
	}

	
	
	
//// end main
}
