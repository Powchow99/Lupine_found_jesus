package org.firstinspires.ftc.teamcode.Vision;

import org.firstinspires.ftc.teamcode.StaticStuff.Side;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;
import static org.firstinspires.ftc.teamcode.Vision.DashVision.DEBUG_MODE;
import static org.firstinspires.ftc.teamcode.Vision.DashVision.blueLeftXvalue;
import static org.firstinspires.ftc.teamcode.Vision.DashVision.blueRightXvalue;
import static org.firstinspires.ftc.teamcode.Vision.DashVision.redLeftXvalue;
import static org.firstinspires.ftc.teamcode.Vision.DashVision.redRightXvalue;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2HSV;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.core.Core.inRange;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.rectangle;


public class DetectionPipeline extends OpenCvPipeline {

    private Mat output = new Mat();
    private  Mat modifiedYellow = new Mat();


    List<MatOfPoint> yellowContours = new ArrayList<>();
    Mat yellowHierarchy = new Mat();
    Scalar green = new Scalar(0,255,0);


    @Override
    public Mat processFrame(Mat input){


        input.copyTo(output);
        input.copyTo(modifiedYellow);
        cvtColor(modifiedYellow,modifiedYellow,COLOR_RGB2HSV);

        Scalar minYellowHSV = new Scalar(20,100,100);
        Scalar maxYellowHSV = new Scalar(30,255,255);
        inRange(modifiedYellow,minYellowHSV,maxYellowHSV,modifiedYellow);

        yellowContours = new ArrayList<>();

        findContours(modifiedYellow,yellowContours,yellowHierarchy,RETR_TREE,CHAIN_APPROX_SIMPLE);

        List<Rect> yellowRects = new ArrayList<>();

        for(int cnt_index = 0; cnt_index < yellowContours.size(); cnt_index++){
            drawContours(output,yellowContours,cnt_index,green,2);

            Rect rect = boundingRect(yellowContours.get(cnt_index));

            yellowRects.add(rect);






        }



        if(yellowRects.size()==0){
            return output;
        }

        Rect largestYellowRect = VisionUtils.sortRectsByMaxOption(1,VisionUtils.RECT_OPTION.AREA,yellowRects).get(0);

        rectangle(output,largestYellowRect,green,2);




        if(Side.blue){




            if(largestYellowRect.x >= blueRightXvalue){

                DuckPosition.duckPos = 3;
            }else if (largestYellowRect.x <= blueLeftXvalue){

                DuckPosition.duckPos = 1;
            } else{

                DuckPosition.duckPos = 2;
            }



        }else if(Side.red){


            if(largestYellowRect.x >= redRightXvalue){
                DuckPosition.duckPos = 3;
            }else if (largestYellowRect.x <= redLeftXvalue){
                DuckPosition.duckPos = 1;
            } else{
                DuckPosition.duckPos = 2;
            }




        }

        if(DEBUG_MODE){
            return modifiedYellow;
        }

        return output;
    }

}
