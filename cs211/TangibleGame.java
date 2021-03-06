import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;
import processing.video.Capture;
import processing.video.Movie;

import java.util.*;

/**
 * Created by sydney on 11.05.15.
 */
public class TangibleGame extends PApplet {
    final int WINDOW_HEIGHT = 900;
    final int WINDOW_WIDTH = 1500;

    final int DATA_HEIGHT = 150;

    final int BOARDWIDTH = 400;
    final int BOARDLENGTH = 400;
    final int BOARDHEIGHT = 20;

    final float ROTY_COEFF = PI/64;
    final float DEFAULT_TILT_COEFF = 0.01f;
    final float MAX_TILT_COEFF = 1.5f*DEFAULT_TILT_COEFF;
    final float MIN_TILT_COEFF = 0.2f*DEFAULT_TILT_COEFF;
    final float TILT_MAX = PI/3;
    final float UP_TILT = -PI/6;

    final float BALL_RADIUS = 25;

    float tilt_coeff = DEFAULT_TILT_COEFF;

    float rotation = 0.0f;
    float tiltX = 0.0f;
    float tiltZ = 0.0f;
    float tiltXBackup = 0.0f;
    float tiltZBackup = 0.0f;
    float rotationBackup = 0.0f;
    
    boolean boardBoundariesDefined = false;
	float boardLeftX;
	float boardRightX;
	float boardUpY;
	float boardDownY;

    Mover mover;

    boolean addingCylinderMode = false;

    private int SCORE_SQUARE = 130;
    PGraphics dataBackground;
    PGraphics topView;
    PGraphics scoreboard;
    PGraphics barChart;
    HScrollbar scrollBar;
    Data data;


    // ========= CHOOSE HERE ===========
    Movie cam;
    //Capture cam;

    // ========= CHOOSE HERE ===========


    PImage img;

    ImageProcessing imgProcessor;
    TwoDThreeD corresponder;

    HScrollbar infHue;
    HScrollbar supHue;
    HScrollbar infSat;
    HScrollbar supSat;
    HScrollbar infBr;
    HScrollbar supBr;

    Queue<PVector> queuedRotations;
    PVector meanRotations;

    public void setup() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);
        noStroke();
        mover = new Mover(this, BALL_RADIUS, BOARDLENGTH, BOARDHEIGHT, BOARDWIDTH);
        
        dataBackground = createGraphics(WINDOW_WIDTH, DATA_HEIGHT, P2D);
        topView = createGraphics(SCORE_SQUARE + 20, SCORE_SQUARE + 20, P2D);
        scoreboard = createGraphics(SCORE_SQUARE + 20, SCORE_SQUARE + 20, P2D);
        barChart = createGraphics(WINDOW_WIDTH - topView.width - scoreboard.width - 10, SCORE_SQUARE - 20, P2D);
        scrollBar = new HScrollbar(this, 0, 0, barChart.width/2, 15, true);
        data = new Data(dataBackground,topView, scoreboard, barChart, scrollBar, mover, SCORE_SQUARE, BOARDWIDTH, BOARDLENGTH, BALL_RADIUS);
        imgProcessor = new ImageProcessing(this);
        queuedRotations = new LinkedList<>();
        meanRotations = new PVector(0,0,0);

        for(int i = 0; i < 4; i++) queuedRotations.add(new PVector(0,0,0));

        infHue = new HScrollbar(this, WINDOW_WIDTH-250, 0, 250, 20, false);
        supHue = new HScrollbar(this, WINDOW_WIDTH-250, 40, 250, 20, false);
        infSat = new HScrollbar(this, WINDOW_WIDTH-250, 80, 250, 20, false);
        supSat = new HScrollbar(this, WINDOW_WIDTH-250, 120, 250, 20, false);
        infBr = new HScrollbar(this, WINDOW_WIDTH-250, 160, 250, 20, false);
        supBr = new HScrollbar(this, WINDOW_WIDTH-250, 200, 250, 20, false);

        Tower.loadShape(this);

        // ============= CHOOSE HERE ===============
        /*String[] cameras = Capture.list();
        if (cameras.length == 0) {
            println("There are no cameras available for capture.");
            exit();
        } else {
            println("Available cameras:");
            for (int i = 0; i < cameras.length; i++) {
                println(i + ": " + cameras[i]);
            }
            
            print("Choose your camera [1-100] : ");
            Scanner keyboard = new Scanner(System.in);
            int camOpt = keyboard.nextInt();
            cam = new Capture(this, cameras[camOpt]);
            cam.start();
        }*/

        cam = new Movie(this, "/home/sydney/Documents/Uni/Assignments/VisualComputing/CS11_Project/cs211/testvideo.ogg");
        cam.loop();
        // ================ CHOOSE HERE ================

        corresponder = new TwoDThreeD(640, 480);
    }

    public void draw() {
        infHue.update();
        supHue.update();
        infSat.update();
        supSat.update();
        infBr.update();
        supBr.update();

        background(200);

        //the default camera of Processing to use for the data info
        camera(width / 2.0f, height / 2.0f, (float) ((height / 2.0) / Math.tan(PI * 30.0 / 180.0)), width / 2.0f, height / 2.0f, 0, 0, 1, 0);

        if(!addingCylinderMode) {
            pushMatrix();
            infHue.display();
            supHue.display();
            infSat.display();
            supSat.display();
            infBr.display();
            supBr.display();

            textAlign(RIGHT, BOTTOM);
            text("Hue down : " + infHue.getPos()*255, WINDOW_WIDTH-260, 20);
            text("Hue up : " + supHue.getPos()*255,WINDOW_WIDTH-260, 60);
            text("Saturation down : " + infSat.getPos()*255,WINDOW_WIDTH-260, 100);
            text("Saturation up : " + supSat.getPos()*255,WINDOW_WIDTH-260, 140);
            text("Brightness down : " + infBr.getPos()*255,WINDOW_WIDTH-260, 180);
            text("Brightness up : " + supBr.getPos()*255,WINDOW_WIDTH-260, 220);

            popMatrix();

            if(cam.available()) cam.read();
            img = cam.get();

            // ============ CHOOSE HERE ============
            PImage processedImg = imgProcessor.process(img, 108, 127, 80, 255, 50, 255);
            //PImage processedImg = imgProcessor.process(img, infHue.getPos()*255, supHue.getPos()*255, infSat.getPos()*255, supSat.getPos()*255, infBr.getPos()*255, supBr.getPos()*255);
            // ============ CHOOSE HERE ============

            List<PVector> corners = imgProcessor.getCorners();
            corners = TwoDThreeD.sortCorners(corners);
            PVector rotations = corresponder.get3DRotations(corners);
            rotations.y *= -1;

            queuedRotations.remove();
            queuedRotations.add(rotations);

            float totalX = 0.0f;
            float totalY = 0.0f;
            for(PVector p : queuedRotations) {
                totalX += p.x;
                totalY += p.y;
            }

            meanRotations.x = totalX/queuedRotations.size();
            meanRotations.y = totalY/queuedRotations.size();

        	pushMatrix();
        	translate(0,WINDOW_HEIGHT-DATA_HEIGHT);
        	data.display(this);
            popMatrix();

            pushMatrix();
            if(processedImg.width > 0 && processedImg.height > 0)
                processedImg.resize(0, 150);
            image(processedImg, 0, 0);

            for(PVector p : corners) {
                fill(255, 128, 0);
                ellipse(p.x*(150/(float)img.height), p.y*(150/(float)img.height), 10, 10);
            }
            popMatrix();
        }
        
        directionalLight(255, 255, 255, 0, 1, -1);
        ambientLight(102, 102, 102);

//        dataBackground.beginDraw();
//        dataBackground.background(255, 255, 200);
//        dataBackground.endDraw();
//        image(dataBackground, 0, WINDOW_HEIGHT-BACKGROUND_HEIGHT);
        
        // Based on which mode we are, camera is placed nearer the board
        if(addingCylinderMode)
            camera(width/2, height/2, 400, width/2, height/2, 0, 0, 1, 0);
        else
            camera(width/2, height/2, 600, width/2, height/2, 0, 0, 1, 0);

        // Place the coordinate system
        translate(width/2, height/2, 0);
        if(!addingCylinderMode) rotateX(meanRotations.x + UP_TILT);
        else rotateX(-PI/2);
        rotateZ(meanRotations.y);
        rotateY(rotation);

        //If we are in adding cylinder mode, place a cylinder
        if(addingCylinderMode) {
        	if(!boardBoundariesDefined){
        		boardLeftX = screenX(-BOARDLENGTH/2f, 0, 0);
        		boardRightX = screenX(BOARDLENGTH/2f, 0, 0);
        		boardUpY = screenY(0, 0, -BOARDWIDTH/2f);
        		boardDownY = screenY(0, 0, BOARDWIDTH/2f);
        		boardBoundariesDefined = true;
        	}
        	mover.placeTower(clampMap(mouseX, boardLeftX, boardRightX, -BOARDLENGTH/2, BOARDLENGTH/2), clampMap(mouseY, boardUpY, boardDownY, -BOARDWIDTH/2, BOARDWIDTH/2));
        }
        else {
            // update and display environnement here
            //System.out.println("rotations is " + ((rotations == null) ? "null":"not null"));
            mover.update(meanRotations.x, meanRotations.y);
            mover.display();
            //image(img, 0, 0);
        }
    }

    public void keyPressed() {
        if(key == CODED) {
            if(keyCode == SHIFT && !addingCylinderMode) {
                addingCylinderMode = true;
                mover.setAddingTowerMode(true);
                tiltXBackup = tiltX; // Needed to restore the tilt after adding cylinder(s)
                tiltZBackup = tiltZ; // Same here
                rotationBackup = rotation; // Same here
                rotation = 0; // Clear rotation

                tiltX = -PI/2; // Rotate the board in front of the camera
                tiltZ = 0;
            }
            if(!addingCylinderMode) {
                if(keyCode == LEFT) {
                    rotation += ROTY_COEFF;
                }
                else if(keyCode == RIGHT) {
                    rotation -= ROTY_COEFF;
                }
            }
        }
    }

    public void keyReleased() {
        if(key == CODED) {
            if(keyCode == SHIFT) {
                addingCylinderMode = false;
                mover.setAddingTowerMode(false);
                tiltX = tiltXBackup;
                tiltZ = tiltZBackup;
                rotation = rotationBackup;
            }
        }
    }

    public void mouseDragged() {
        if(!addingCylinderMode && mouseY < WINDOW_HEIGHT - DATA_HEIGHT) {
            float tiltXIncrement = -tilt_coeff*(mouseY - pmouseY);
            float tiltZIncrement = tilt_coeff*(mouseX - pmouseX);

            if(abs(tiltX + tiltXIncrement) < TILT_MAX)
                tiltX += tiltXIncrement;
            if(abs(tiltZ + tiltZIncrement) < TILT_MAX)
                tiltZ += tiltZIncrement;
        }
    }

    public void mousePressed() {
        mover.addTower(clampMap(mouseX, boardLeftX, boardRightX, -BOARDLENGTH/2, BOARDLENGTH/2), clampMap(mouseY, boardUpY, boardDownY, -BOARDWIDTH/2, BOARDWIDTH/2));
    }

    public void mouseWheel(MouseEvent event) {
        float newTilt = tilt_coeff + -event.getCount()*0.1f*DEFAULT_TILT_COEFF;

        if(newTilt > MIN_TILT_COEFF && newTilt < MAX_TILT_COEFF)
            tilt_coeff = newTilt;
    }
    
    //same as PApplet.map but clamp value outside of range
    public final float clampMap(float value, float start1, float stop1, float start2, float stop2) {
    	if(value < start1) 
    		return start2;
    	else if (value > stop1)
    		return stop2;
    	else
    		return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }
}
