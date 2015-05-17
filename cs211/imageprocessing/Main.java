package imageprocessing;

import javax.xml.datatype.DatatypeFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import tests.HScrollBar;

<<<<<<< HEAD:cs211/imageprocessing/Main.java
/**
 * Created by sydney on 11.05.15.
 */
public class Main extends PApplet {
    final int WINDOW_HEIGHT = 1000;
    final int WINDOW_WIDTH = 1500;

    final int BACKGROUND_HEIGHT = 150;
=======
public class ImageProcessing extends PApplet {
    PImage img;
    HScrollBar thresholdBar;

    public void setup() {
        size(800, 600);
        //thresholdBar = new HScrollbar(this, 0, 580, 800, 20);
        img = loadImage("cs11/board1.jpg");
        noLoop();
    }
    public void draw() {
        // f = 700
        background(color(0,0,0));
>>>>>>> origin/week8:src/cs11/ImageProcessing.java

        PImage selecHueImg = selectHue(img, 115, 135); // 100 - 135 et webcam : 98, 112
        PImage blurImg = gaussianBlur(selecHueImg);
        PImage thresholdImg = thresholdImg(blurImg, 0.05);
        //		print(thresholdBar.getPos());
        PImage sobelImg = sobel(thresholdImg, 0.5);
        //image(selecHueImg, 0, 0);
        hough(sobelImg);
        //		thresholdBar.display();
        //		thresholdBar.update();
    }
    private PImage thresholdImg(PImage img, double threashold){
        PImage result = createImage(width, height, RGB); // create a new, initially transparent, 'result' image
        double threshold = threashold * 255;
        for(int i = 0; i < img.width * img.height; i++) {
            result.pixels[i] = brightness(img.pixels[i])  > threshold? color(255, 255, 255): color(0, 0, 0);
        }
        return result;
    }

    public PImage sobel(PImage img, double max) {
        float[][] hKernel = {
                { 0, 1, 0 },
                { 0, 0, 0 },
                { 0, -1, 0}};
        float[][] vKernel = {
                { 0, 0, 0 },
                { 1, 0, -1},
                { 0, 0, 0 }};

        PImage result = createImage(img.width, img.height, ALPHA);

        // clear the image
        for (int i = 0; i < img.width * img.height; i++) {
            result.pixels[i] = color(0);
        }

        for (int y=2; y < result.height-2; y++) {
            for (int x=2; x < result.width-2; x++) {
                int convH = 0;
                int convV = 0;

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        convH += brightness(img.get(x+i-1, y+j-1)) * hKernel[i][j];
                        convV += brightness(img.get(x+i-1, y+j-1)) * vKernel[i][j];
                    }
                }
                if (sqrt(convH*convH + convV*convV) > (int)(max* 255 * 0.3f)) { // 30% of the max
                    result.set(x, y, color(255));
                } else {
                    result.set(x, y, color(0));
                }


            }
        }
        return result;
    }

<<<<<<< HEAD:cs211/imageprocessing/Main.java
    private int SCORE_SQUARE = 130;
    PGraphics dataBackground;
    PGraphics topView;
    PGraphics scoreboard;
    PGraphics barChart;
    Data data;

    public void setup() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);
        noStroke();
        mover = new Mover(this, BALL_RADIUS, BOARDLENGTH, BOARDHEIGHT, BOARDWIDTH);
        dataBackground = createGraphics(WINDOW_WIDTH, BACKGROUND_HEIGHT, P2D);
        topView = createGraphics(SCORE_SQUARE + 20, SCORE_SQUARE + 20, P2D);
        scoreboard = createGraphics(SCORE_SQUARE + 20, SCORE_SQUARE + 20, P2D);
        barChart = createGraphics(WINDOW_WIDTH - topView.width - scoreboard.width - 10, SCORE_SQUARE - 20, P2D);
        data = new Data(dataBackground, topView, scoreboard, barChart, mover, SCORE_SQUARE, BOARDWIDTH, BOARDLENGTH, BALL_RADIUS);
    }

    public void draw() {
        background(200);
        //Default camera of processing
        camera(width/2, height/2, (float)((height/2.0) / Math.tan(PI*30.0 / 180.0)), width/2, height/2, 0, 0, 1, 0);

        if(!addingCylinderMode) {
        	pushMatrix();
       		translate(0,WINDOW_HEIGHT-150,0);
       		data.display(this);
        	popMatrix();
        }
        
        directionalLight(255, 255, 255, 0, 1, -1);
        ambientLight(102, 102, 102);

        // Based on which mode we are, camera is placed nearer the board
        if(addingCylinderMode)
            camera(width/2, height/2, 400, width/2, height/2, 0, 0, 1, 0);
        else
            camera(width/2, height/2, 600, width/2, height/2, 0, 0, 1, 0);

        // Place the coordinate system
        translate(width/2, height/2, 0);
        if(!addingCylinderMode) rotateX(tiltX + UP_TILT);
        else rotateX(tiltX);
        rotateZ(tiltZ);
        rotateY(rotation);


        // Optional : show Axis
        if(showAxis) {
            //Axe X
            stroke(0, 255, 0);
            line(-longueurAxes/2, 0, 0, longueurAxes/2, 0, 0);
            //Axe Y
            stroke(255, 0, 0);
            line(0, -longueurAxes/2, 0, 0, longueurAxes/2, 0);
            //Axe Z
            stroke(0,0,255);
            line(0, 0, -longueurAxes/2, 0, 0, longueurAxes/2);
        }
=======
    public PImage convolute(PImage img) {
        float[][] kernel = {
                { 0, 0, 0 },
                { 0, 2, 0 },
                { 0, 0, 0 }};

        return transformAlgo(img, kernel, 1.f);
    }

    public PImage gaussianBlur(PImage img) {
        float[][] kernel = {
                { 9, 12, 9 },
                { 12, 15, 12 },
                { 9, 12, 9 }};
>>>>>>> origin/week8:src/cs11/ImageProcessing.java


        return transformAlgo(img, kernel, 99);
    }

    private PImage selectHue(PImage img, float infHue, float supHue){
        // create a greyscale image (type: ALPHA) for output
        PImage result = createImage(img.width, img.height, ALPHA);

        for (int y =0; y<img.height; y++) {
            for (int x=0; x<img.width; x++) {
                float hue = hue(img.get(x, y));
                if(infHue<hue && hue<supHue){
                    result.set(x, y, img.get(x, y));
                }
                else {
                    result.set(x, y, color(0));
                }
            }
        }
        return result;
    }

    private PImage transformAlgo(PImage img, float[][] kernel, float weight) {

        // create a greyscale image (type: ALPHA) for output
        PImage result = createImage(img.width, img.height, ALPHA);

        for (int y =0; y<img.height; y++) {
            for (int x=0; x<img.width; x++) {

                float conv = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if(!(x+i-1 < 0 || x+i-1 > img.width-1 || y+j-1 < 0 || y+j-1 > img.height-1)){

                            float g = green(img.get(x+i-1, y+j-1));
                            float r = red(img.get(x+i-1, y+j-1));
                            float b = blue(img.get(x+i-1, y+j-1));

                            conv += (r+g+b)/3 * kernel[i][j];
                            println(img.get(x+i-1, y+j-1));

                        }
                    }
                }
                result.set(x, y, color((int) (conv/weight)));
            }
        }

        image(result, 0, 0);

        return result;
    }

    public void hough(PImage edgeImg) {

        float discretizationStepsPhi = 0.01f;
        float discretizationStepsR = 2.5f;


        // dimensions of the accumulator
        int phiDim = (int) (Math.PI / discretizationStepsPhi);
        int rDim = (int) (((edgeImg.width + edgeImg.height) * 2 + 1) / discretizationStepsR);
        // our accumulator (with a 1 pix margin around)
        final int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];


        // Fill the accumulator: on edge points (ie, white pixels of the edge
        // image), store all possible (r, phi) pairs describing lines going
        // through the point.
        for (int y = 0; y < edgeImg.height; y++) {
            for (int x = 0; x < edgeImg.width; x++) {
                // Are we on an edge?
                if (brightness(edgeImg.pixels[y * edgeImg.width + x]) != 0) {
                    for(int phiIdx=0; phiIdx<phiDim; ++phiIdx){
                        double phi = discretizationStepsPhi * phiIdx;
                        double r = x*Math.cos(phi) + y*Math.sin(phi);

                        int rIdx = (int) ( (r/discretizationStepsR) + 0.5*(rDim-1) + 1) ;
                        accumulator[(phiIdx+1)*(rDim + 2) + rIdx] += 1;
                    }
                }
            }
        }




        ArrayList<Integer> bestCandidates = new ArrayList<Integer>();


        // size of the region we search for a local maximum
        int neighbourhood = 10;

        // only search around lines with more that this amount of votes
        // (to be adapted to your image)
        int minVotes = 340;
        for (int accR = 0; accR < rDim; accR++) {
            for (int accPhi = 0; accPhi < phiDim; accPhi++) {

                // compute current index in the accumulator
                int idx = (accPhi + 1) * (rDim + 2) + accR + 1;
                if (accumulator[idx] > minVotes) {

                    boolean bestCandidate = true;

                    // iterate over the neighbourhood
                    for(int dPhi=-neighbourhood/2; dPhi < neighbourhood/2+1; dPhi++) {

                        // check we are not outside the image
                        if( !(accPhi+dPhi < 0 || accPhi+dPhi >= phiDim)){
                            for(int dR=-neighbourhood/2; dR < neighbourhood/2 +1; dR++) {

                                // check we are not outside the image
                                if(!(accR+dR < 0 || accR+dR >= rDim)){
                                    int neighbourIdx = (accPhi + dPhi + 1) * (rDim + 2) + accR + dR + 1;
                                    // the current idx is not a local maximum!
                                    if(accumulator[idx] < accumulator[neighbourIdx]) {
                                        bestCandidate=false;
                                        break;
                                    }
                                }

                            }
                            if(!bestCandidate) break;
                        }

                    }
                    if(bestCandidate) {
                        // the current idx *is* a local maximum
                        bestCandidates.add(idx);
                    }
                }
            }
        }

        Collections.sort(bestCandidates, new Comparator<Integer>(){
            @Override
            public int compare(Integer l1, Integer l2) {
                if (accumulator[l1] > accumulator[l2]
                        || (accumulator[l1] == accumulator[l2] && l1 < l2)) return -1;
                return 1;
            }
        });


        List<PVector> lines = new ArrayList<>();

        for (int i = 0; i < bestCandidates.size(); i++) {
            int idx = bestCandidates.get(i);
            int accPhi = (int) (idx / (rDim + 2)) - 1;
            int accR = idx - (accPhi + 1) * (rDim + 2) - 1;
            float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
            float phi = accPhi * discretizationStepsPhi;

            lines.add(new PVector(r, phi));


            // Cartesian equation of a line: y = ax + b
            // in polar, y = (-cos(phi)/sin(phi))x + (r/sin(phi))
            // => y = 0 : x = r / cos(phi)
            // => x = 0 : y = r / sin(phi)
            // compute the intersection of this line with the 4 borders of // the image
            int x0 = 0;
            int y0 = (int) (r / sin(phi));
            int x1 = (int) (r / cos(phi));
            int y1 = 0;
            int x2 = edgeImg.width;
            int y2 = (int) (-cos(phi) / sin(phi) * x2 + r / sin(phi)); int y3 = edgeImg.width;
            int x3 = (int) (-(y3 - r / sin(phi)) * (sin(phi) / cos(phi)));
            // Finally, plot the lines
            stroke(204,102,0); if (y0 > 0) {
                if (x1 > 0)
                    line(x0, y0, x1, y1);
                else if (y2 > 0)
                    line(x0, y0, x2, y2);
                else
                    line(x0, y0, x3, y3);
            }
            else {
                if (x1 > 0) {
                    if (y2 > 0)
                        line(x1, y1, x2, y2); else
                        line(x1, y1, x3, y3);
                }
                else
                    line(x2, y2, x3, y3);
            }

        }
        getIntersections(lines);

    }



    public ArrayList<PVector> getIntersections(List<PVector> lines) {
        ArrayList<PVector> intersections = new ArrayList<PVector>();
        for (int i = 0; i < lines.size() - 1; i++) {
            PVector line1 = lines.get(i);
            for (int j = i + 1; j < lines.size(); j++) {
                PVector line2 = lines.get(j);
                // compute the intersection and add it to 'intersections'
                // draw the intersection
                float d = cos(line2.y)*sin(line1.y) - cos(line1.y)*sin(line2.y);
                float x = (line2.x*sin(line1.y) - line1.x*sin(line2.y)) / d;
                float y = (-line2.x*cos(line1.y) + line1.x*cos(line2.y)) / d;

                ellipse(x, y, 10, 10);
                fill(255, 128, 0);
                intersections.add(new PVector(x, y));
            }
        }
        return intersections;
    }
}