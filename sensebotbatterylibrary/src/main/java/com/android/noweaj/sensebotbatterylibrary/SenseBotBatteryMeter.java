package com.android.noweaj.sensebotbatterylibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class SenseBotBatteryMeter extends View {

    private Paint finishedPaint;
    private Paint unfinishedPaint;
    private Paint innerCirclePaint;

    protected Paint titleTextPaint;
    protected Paint percentTextPaint;

    private RectF finishedOuterRect = new RectF();
    private RectF unfinishedOuterRect = new RectF();

    private boolean showTitleText;
    private boolean showPercentText;

    private float finishedStrokeWidth;
    private float unfinishedStrokeWidth;

    private int finishedStrokeColor;
    private int unfinishedStrokeColor;

    private int titleBoxColor;
    private int productBoxColor;

    private int titleTextColor;
    private int percentTextColor;

    private float progress = 0;
    private int maxProgress;

    private String prefixText = "";
    private String suffixText = "%";
    private String titleText = null;
    private String percentText = null;

    private int startingDegree;
    private final int default_startingDegree = 270;
    private final int default_maxProgress = 100;

    private int attributeResourceId = 0;

    private final int min_size;
    private final float default_stroke_width;

    private final float default_title_text_size;
    private final float default_percent_text_size;

    private float titleTextSize;
    private float percentTextSize;

    private final float default_title_box_height;
    private final float default_product_box_height;

    private final int default_finished_color = Color.rgb(0, 188, 229); // RGB(0, 188, 229) , #00BCE5
    private final int default_unfinished_color = Color.rgb(186, 186, 186);
    //private final int default_unfinished_color = Color.parseColor("#BABABA");

    private final int default_title_box_color = Color.rgb(127, 127, 127);
    private final int default_product_box_color = Color.rgb(255, 255, 255);

    private final int default_title_text_color = Color.rgb(255, 255, 255);
    private final int default_percent_text_color = Color.rgb(134, 134, 134);

    private int product_image;
    private int product_image_ratio;

    private float title_box_height;
    private float product_box_height;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TITLE_TEXT_COLOR = "title_text_color";
    private static final String INSTANCE_TITLE_TEXT_SIZE = "title_text_size";
    private static final String INSTANCE_TITLE_TEXT = "title_text";
    private static final String INSTANCE_PERCENT_TEXT_COLOR = "percent_text_color";
    private static final String INSTANCE_PERCENT_TEXT_SIZE = "percent_text_size";
    private static final String INSTANCE_PERCENT_TEXT = "percent_text";
    private static final String INSTANCE_PREFIX = "prefix";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_FINISHED_STROKE_WIDTH = "finished_stroke_width";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_WIDTH = "unfinished_stroke_width";
    private static final String INSTANCE_MAX_PROGRESS = "max_progress";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_STARTING_DEGREE = "starting_degree";
    private static final String INSTANCE_INNER_DRAWABLE = "inner_drawable";


    public SenseBotBatteryMeter(Context context){
        this(context, null);
    }

    public SenseBotBatteryMeter(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public SenseBotBatteryMeter(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);

        /**
         * need to modify this part
         */
        default_stroke_width = Utils.dp2px(getResources(), 20); // CHANGE THIS
        default_title_text_size = Utils.sp2px(getResources(), 13);
        default_percent_text_size = Utils.sp2px(getResources(), 18);
        min_size = (int) Utils.dp2px(getResources(), 100);

        default_title_box_height = Utils.dp2px(getResources(), 25);
        default_product_box_height = Utils.dp2px(getResources(), 155);

        maxProgress = 100;
        showPercentText = true;
        showTitleText = true;

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SenseBotBatteryMeter, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initPainters(){

        if(showTitleText){
            titleTextPaint = new TextPaint();
            titleTextPaint.setColor(titleTextColor);
            titleTextPaint.setTextSize(titleTextSize);
            titleTextPaint.setAntiAlias(true);
        }

        if(showPercentText){
            percentTextPaint = new TextPaint();
            percentTextPaint.setColor(percentTextColor);
            percentTextPaint.setTextSize(percentTextSize);
            percentTextPaint.setAntiAlias(true);
        }

        finishedPaint = new Paint();
        finishedPaint.setColor(finishedStrokeColor);
        finishedPaint.setStyle(Paint.Style.STROKE);
        finishedPaint.setAntiAlias(true);
        finishedPaint.setStrokeWidth(finishedStrokeWidth);

        unfinishedPaint = new Paint();
        unfinishedPaint.setColor(unfinishedStrokeColor);
        unfinishedPaint.setStyle(Paint.Style.STROKE);
        unfinishedPaint.setAntiAlias(true);
        unfinishedPaint.setStrokeWidth(unfinishedStrokeWidth);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(productBoxColor); // need to set default titleBoxColor
        innerCirclePaint.setAntiAlias(true);
    }

    protected void initByAttributes(TypedArray attributes){

        finishedStrokeColor = attributes.getColor(R.styleable.SenseBotBatteryMeter_battery_meter_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.SenseBotBatteryMeter_battery_meter_unfinished_color, default_unfinished_color);
        showTitleText = attributes.getBoolean(R.styleable.SenseBotBatteryMeter_battery_meter_show_title_text, true);
        showPercentText = attributes.getBoolean(R.styleable.SenseBotBatteryMeter_battery_meter_show_percent_text, true);

        attributeResourceId = attributes.getResourceId(R.styleable.SenseBotBatteryMeter_battery_meter_inner_drawable, 0);

        setMaxProgress(attributes.getInt(R.styleable.SenseBotBatteryMeter_battery_meter_max, default_maxProgress));
        setProgress(attributes.getFloat(R.styleable.SenseBotBatteryMeter_battery_meter_progress, 0));

        finishedStrokeWidth = attributes.getDimension(R.styleable.SenseBotBatteryMeter_battery_meter_finished_stroke_width, default_stroke_width);
        unfinishedStrokeWidth = attributes.getDimension(R.styleable.SenseBotBatteryMeter_battery_meter_unfinished_stroke_width, default_stroke_width);

        if(showTitleText){

            if(attributes.getString(R.styleable.SenseBotBatteryMeter_battery_meter_title_text) != null){
                titleText = attributes.getString(R.styleable.SenseBotBatteryMeter_battery_meter_title_text);
            }

            titleTextColor = attributes.getColor(R.styleable.SenseBotBatteryMeter_battery_meter_title_text_color, default_title_text_color);
            titleTextSize = attributes.getDimension(R.styleable.SenseBotBatteryMeter_battery_meter_title_text_size, default_title_text_size);
        }

        if(showPercentText){

            if(attributes.getString(R.styleable.SenseBotBatteryMeter_battery_meter_show_percent_text) != null){
                percentText = attributes.getString(R.styleable.SenseBotBatteryMeter_battery_meter_show_percent_text);
            }

            percentTextColor = attributes.getColor(R.styleable.SenseBotBatteryMeter_battery_meter_percent_text_color, default_percent_text_color);
            percentTextSize = attributes.getDimension(R.styleable.SenseBotBatteryMeter_battery_meter_percent_text_size, default_percent_text_size);
        }

        startingDegree = attributes.getInt(R.styleable.SenseBotBatteryMeter_battery_meter_starting_degree, default_startingDegree);

        title_box_height = attributes.getFloat(R.styleable.SenseBotBatteryMeter_battery_meter_title_box_height, default_title_box_height);
        product_box_height = attributes.getFloat(R.styleable.SenseBotBatteryMeter_battery_meter_product_box_height, default_product_box_height);

        titleBoxColor = attributes.getColor(R.styleable.SenseBotBatteryMeter_battery_meter_title_box_color, default_title_box_color);
        productBoxColor = attributes.getColor(R.styleable.SenseBotBatteryMeter_battery_meter_product_box_color, default_product_box_color);

        product_image = attributes.getInt(R.styleable.SenseBotBatteryMeter_battery_meter_product_image_src, 0);
        //attributeResourceId = attributes.getInt(R.styleable.SenseBotBatteryMeter_battery_meter_product_image, 0);
    }

    @Override
    public void invalidate(){
        initPainters();
        super.invalidate();
    }

    public boolean isShowTitleText(){
        return showTitleText;
    }

    public void setShowTitleText(boolean showTitleText){
        this.showTitleText = showTitleText;
    }

    public boolean isShowPercentText(){
        return showPercentText;
    }

    public void setShowPercentText(boolean showPercentText){
        this.showPercentText = showPercentText;
    }

    public float getFinishedStrokeWidth(){
        return finishedStrokeWidth;
    }

    public float getUnfinishedStrokeWidth(){
        return unfinishedStrokeWidth;
    }

    public void setFinishedStrokeWidth(float finishedStrokeWidth){
        this.finishedStrokeWidth = finishedStrokeWidth;
        this.invalidate();
    }

    public void setUnfinishedStrokeWidth(float unfinishedStrokeWidth){
        this.unfinishedStrokeWidth = unfinishedStrokeWidth;
        this.invalidate();
    }

    public void setStrokeWidth(float strokeWidth){
        finishedStrokeWidth = strokeWidth;
        unfinishedStrokeWidth = strokeWidth;
        this.invalidate();
    }

    public float getProgress(){
        return progress;
    }

    public void setProgress(float progress){
        this.progress = progress;
        if(this.progress > getMaxProgress()){
            this.progress %= getMaxProgress();
        }
        invalidate();
    }

    public int getMaxProgress(){
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress){
        if(maxProgress > 0){
            this.maxProgress = maxProgress;
            invalidate();
        }
    }

    public int getStartingDegree(){
        return startingDegree;
    }

    public void setStartingDegree(int startingDegree){
        this.startingDegree = startingDegree;
        this.invalidate();
    }

    private float getProgressAngle(){
        return getProgress() / (float) maxProgress * 360f;
    }

    public int getTitleTextColor(){
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor){
        this.titleTextColor = titleTextColor;
        this.invalidate();
    }

    public int getPercentTextColor(){
        return percentTextColor;
    }

    public void setPercentTextColor(int percentTextColor){
        this.percentTextColor = percentTextColor;
        this.invalidate();
    }

    public float getTitleTextSize(){
        return titleTextSize;
    }

    public void setTitleTextSize(float titleTextSize){
        this.titleTextSize = titleTextSize;
        this.invalidate();
    }

    public float getPercentTextSize(){
        return percentTextSize;
    }

    public void setPercentTextSize(float percentTextSize){
        this.percentTextSize = percentTextSize;
        this.invalidate();
    }

    public String getPrefixText(){
        return prefixText;
    }

    public String getSuffixText(){
        return suffixText;
    }

    public String getTitleText(){
        return titleText;
    }

    public void setTitleText(String titleText){
        this.titleText = titleText;
        this.invalidate();
    }

    public String getPercentText(){
        return percentText;
    }

    public void setPercentText(String percentText){
        this.percentText = percentText;
        this.invalidate();
    }

    public int getFinishedStrokeColor(){
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor){
        this.finishedStrokeColor = finishedStrokeColor;
        this.invalidate();
    }

    public int getUnfinishedStrokeColor(){
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor){
        this.unfinishedStrokeColor = unfinishedStrokeColor;
        this.invalidate();
    }

    /*
    public float getTitleTextSize(){
        return title_text_size;
    }
    */


    public int getProductImage(){
        return product_image;
    }

    public void setProductImage(int src){
        this.product_image = src;
    }

    public int getAttributeResourceId(){
        return attributeResourceId;
    }

    public void setAttributeResourceId(int attributeResourceId){
        this.attributeResourceId = attributeResourceId;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));

    }

    private int measure(int measureSpec){
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        } else {
            result = min_size;
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(result, size);
            }
        }
        return result;
    }



    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        /*
        float delta = Math.max(finishedStrokeWidth, unfinishedStrokeWidth);
        finishedOuterRect.set(delta, delta, getWidth()-delta, getHeight()-delta);
        unfinishedOuterRect.set(delta, delta, getWidth()-delta, getHeight()-delta);
        */
        float innerCircleRadius = (getWidth() - Math.min(finishedStrokeWidth, unfinishedStrokeWidth) + Math.abs(finishedStrokeWidth - unfinishedStrokeWidth)) / 2f;
        product_box_height = getHeight()-innerCircleRadius-(finishedStrokeWidth/2);
        title_box_height = (float) (product_box_height * 15)/100;
        finishedOuterRect.set(finishedStrokeWidth/2, product_box_height-innerCircleRadius, getWidth()-(finishedStrokeWidth/2), product_box_height+innerCircleRadius);
        unfinishedOuterRect.set(unfinishedStrokeWidth/2, product_box_height-innerCircleRadius, getWidth()-(unfinishedStrokeWidth/2), product_box_height+innerCircleRadius);
        // 1. Draw upper rectangle for title
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.parseColor("#7F7F7F"));
        canvas.drawRect(0, 0, getWidth(), title_box_height, titlePaint);
        // 2. Draw mid rectangle for product image
        Paint productPaint = new Paint();
        productPaint.setColor(Color.parseColor("#FFFFFF"));
        //canvas.drawRect(0, title_box_height, getWidth(), product_box_height, productPaint);
        canvas.drawRect(0, title_box_height, getWidth(), product_box_height, productPaint);
        // 3. Draw lower circle for battery meter
        canvas.drawCircle(getWidth()/2, product_box_height, innerCircleRadius, innerCirclePaint);
        // 4. Arc
        if(progress>=0 && progress<=33){
            finishedPaint.setColor(Color.rgb(233, 41, 36));
        } else if(progress>33 && progress<=67){
            finishedPaint.setColor(Color.rgb(232, 157, 40));
        } else if(progress>67 && progress<=100){
            finishedPaint.setColor(Color.rgb(102, 132, 194));
        }
        canvas.drawArc(unfinishedOuterRect, 0, 360, false, unfinishedPaint);
        canvas.drawArc(finishedOuterRect, getStartingDegree(), getProgressAngle(), false, finishedPaint);

        /*
        Bitmap pBitmap = BitmapFactory.decodeResource(getResources(), product_image);
        product_image_ratio = 75;
        float imageAreaWidth = getWidth() * product_image_ratio / 100;
        float imageAreaHeight = product_box_height * product_image_ratio / 100;
        System.out.println(imageAreaWidth + "/" + imageAreaHeight);
        pBitmap = resizeBitmap(pBitmap, imageAreaWidth, imageAreaHeight);
        */

        product_image_ratio = 75;
        Bitmap pBitmap = BitmapFactory.decodeResource(getResources(), product_image);
        if(pBitmap != null){
            int bHeight = pBitmap.getHeight();
            int bWidth = pBitmap.getWidth();
            System.out.println(bHeight + " / " + bWidth + " / " + getWidth() + " / " + (getWidth() * product_image_ratio / 100));
            Bitmap resized = null;
            while(bHeight > (getWidth() * product_image_ratio / 100)){
                resized = Bitmap.createScaledBitmap(pBitmap, (bWidth * (getWidth() * product_image_ratio / 100))/bHeight, (getWidth() * product_image_ratio / 100), true);
                bHeight = resized.getHeight();
                bWidth = resized.getWidth();
            }

            pBitmap.recycle();
            //canvas.drawBitmap(resized, 0, title_box_height, null);
            canvas.drawBitmap(resized, (getWidth()-resized.getWidth())/2, title_box_height+(product_box_height-resized.getHeight())/2-innerCircleRadius, null);
        }

        if(showTitleText){
            if(!TextUtils.isEmpty(titleText)){
                float textHeight = titleTextPaint.descent() + titleTextPaint.ascent();
                titleTextPaint.setTextSize(titleTextSize);
                canvas.drawText(titleText, (getWidth()/2)-(titleTextPaint.measureText(titleText)/2), (title_box_height/2)-(textHeight/2), titleTextPaint);
            }
        }

        if(showPercentText){
            String str = String.format("%.0f", progress);
            String text = this.percentText != null ? this.percentText : prefixText + str + suffixText;
            if(!TextUtils.isEmpty(text)){
                float textHeight = percentTextPaint.descent() + percentTextPaint.ascent();
                canvas.drawText(text, (getWidth()-percentTextPaint.measureText(text))/2, product_box_height-(textHeight/2), percentTextPaint);
            }
        }

        if(attributeResourceId != 0){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), attributeResourceId);
            canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2.0f, (getHeight() - bitmap.getHeight()) / 2.0f, null);
        }

    }

    private Bitmap resizeBitmap(Bitmap src, float pb_width, float pb_height){

        return Bitmap.createScaledBitmap(src, (int) pb_width, (int) pb_height, true);
    }

    @Override
    protected Parcelable onSaveInstanceState(){
        final Bundle bundle = new Bundle();

        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());

        bundle.putInt(INSTANCE_TITLE_TEXT_COLOR, getTitleTextColor());
        bundle.putInt(INSTANCE_PERCENT_TEXT_COLOR, getPercentTextColor());
        bundle.putFloat(INSTANCE_TITLE_TEXT_SIZE, getTitleTextSize());
        bundle.putFloat(INSTANCE_PERCENT_TEXT_SIZE, getPercentTextSize());

        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putFloat(INSTANCE_FINISHED_STROKE_WIDTH, getFinishedStrokeWidth());
        bundle.putFloat(INSTANCE_UNFINISHED_STROKE_WIDTH, getUnfinishedStrokeWidth());

        bundle.putInt(INSTANCE_MAX_PROGRESS, getMaxProgress());
        bundle.putInt(INSTANCE_STARTING_DEGREE, getStartingDegree());

        bundle.putString(INSTANCE_TITLE_TEXT, getTitleText());
        bundle.putString(INSTANCE_PERCENT_TEXT, getPercentText());

        bundle.putInt(INSTANCE_INNER_DRAWABLE, getAttributeResourceId());

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state){

        if(state instanceof Bundle){
            final Bundle bundle = (Bundle) state;

            titleTextColor = bundle.getInt(INSTANCE_TITLE_TEXT_COLOR);
            percentTextColor = bundle.getInt(INSTANCE_PERCENT_TEXT_COLOR);
            titleTextSize = bundle.getFloat(INSTANCE_TITLE_TEXT_SIZE);
            percentTextSize = bundle.getFloat(INSTANCE_PERCENT_TEXT_SIZE);
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            finishedStrokeWidth = bundle.getFloat(INSTANCE_FINISHED_STROKE_WIDTH);
            unfinishedStrokeWidth = bundle.getFloat(INSTANCE_UNFINISHED_STROKE_WIDTH);

            attributeResourceId = bundle.getInt(INSTANCE_INNER_DRAWABLE);

            initPainters();

            setMaxProgress(bundle.getInt(INSTANCE_MAX_PROGRESS));
            setStartingDegree(bundle.getInt(INSTANCE_STARTING_DEGREE));
            setProgress(bundle.getFloat(INSTANCE_PROGRESS));
            prefixText = bundle.getString(INSTANCE_PREFIX);
            suffixText = bundle.getString(INSTANCE_SUFFIX);
            titleText = bundle.getString(INSTANCE_TITLE_TEXT);
            percentText = bundle.getString(INSTANCE_PERCENT_TEXT);

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
