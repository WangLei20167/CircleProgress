package me.bruce.circleprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bruce on 11/6/14.
 */
public class ArcProgress extends View {
    private Paint paint;
    protected Paint textPaint;

    private RectF rectF = new RectF();

    private float strokeWidth;
    private float suffixTextSize;
    private float bottomTextSize;
    private String bottomText;
    private float textSize;
    private int textColor;
    private int progress = 0;
    private int max;
    private boolean showText = true;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private float arcAngle;
    protected String suffixText = "%";

    private final int default_finished_color = Color.WHITE;
    private final int default_unfinished_color = Color.rgb(72, 106, 176);
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final float default_suffix_text_size;
    private final float default_suffix_padding;
    private final float default_bottom_text_size;
    private final float default_stroke_width;
    private final String default_suffix_text;
    private final int default_max = 100;
    private final float default_arc_angle = 360 * 0.8f;
    private float default_text_size;
    private final int min_size;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_STROKE_WIDTH = "stroke_width";
    private static final String INSTANCE_SUFFIX_TEXT_SIZE = "suffix_text_size";
    private static final String INSTANCE_BOTTOM_TEXT_SIZE = "bottom_text_size";
    private static final String INSTANCE_BOTTOM_TEXT = "bottom_text";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_SHOW_TEXT = "show_text";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_ARC_ANGLE = "arc_angle";
    private static final String INSTANCE_SUFFIX = "suffix";

    public ArcProgress(Context context) {
        this(context, null);
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_text_size = Utils.sp2px(getResources(), 18);
        min_size = (int) Utils.dp2px(getResources(), 100);
        default_text_size = Utils.sp2px(getResources(), 40);
        default_suffix_text_size = Utils.sp2px(getResources(), 15);
        default_suffix_padding = Utils.dp2px(getResources(), 4);
        default_suffix_text = "%";
        default_bottom_text_size = Utils.sp2px(getResources(), 10);
        default_stroke_width = Utils.dp2px(getResources(), 4);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_unfinished_color, default_unfinished_color);
        textColor = attributes.getColor(R.styleable.ArcProgress_arc_text_color, default_text_color);
        textSize = attributes.getDimension(R.styleable.ArcProgress_arc_text_size, default_text_size);
        arcAngle = attributes.getDimension(R.styleable.ArcProgress_arc_angle, default_arc_angle);

        int textVisible = attributes.getInt(R.styleable.ArcProgress_arc_text_visibility, ProgressTextVisibility.Visible.ordinal());
        if(textVisible != ProgressTextVisibility.Visible.ordinal()){
            showText = false;
        }

        setMax(attributes.getInt(R.styleable.ArcProgress_arc_max, default_max));
        setProgress(attributes.getInt(R.styleable.ArcProgress_arc_progress, 0));
        strokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_stroke_width, default_stroke_width);
        suffixTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_size, default_suffix_text_size);
        suffixText = TextUtils.isEmpty(attributes.getString(R.styleable.ArcProgress_arc_suffix_text)) ? default_suffix_text : attributes.getString(R.styleable.ArcProgress_arc_suffix_text);
        bottomTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_bottom_text_size, default_bottom_text_size);
        bottomText = attributes.getString(R.styleable.ArcProgress_arc_bottom_text);
    }

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(default_unfinished_color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getSuffixTextSize() {
        return suffixTextSize;
    }

    public void setSuffixTextSize(float suffixTextSize) {
        this.suffixTextSize = suffixTextSize;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getBottomTextSize() {
        return bottomTextSize;
    }

    public void setBottomTextSize(float bottomTextSize) {
        this.bottomTextSize = bottomTextSize;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
    }

    public float getArcAngle() {
        return arcAngle;
    }

    public void setArcAngle(float arcAngle) {
        this.arcAngle = arcAngle;
    }

    public String getSuffixText() {
        return suffixText;
    }

    public void setSuffixText(String suffixText) {
        this.suffixText = suffixText;
    }

    public void setTextVisibility(ProgressTextVisibility visibility) {
        if (visibility == ProgressTextVisibility.Invisible) {
            showText = false;
        } else {
            showText = true;
        }
        invalidate();
    }

    public ProgressTextVisibility getTextVisibility() {
        return showText ? ProgressTextVisibility.Visible : ProgressTextVisibility.Invisible;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return min_size;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return min_size;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        rectF.set(strokeWidth / 2f, strokeWidth / 2f, getWidth() - strokeWidth / 2f, getHeight() - strokeWidth / 2f);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startAngle = 270 - arcAngle / 2f;
        float finishedSweepAngle = progress / 100f * arcAngle;
        float finishedStartAngle = startAngle;
        paint.setColor(unfinishedStrokeColor);
        canvas.drawArc(rectF, startAngle, arcAngle, false, paint);
        paint.setColor(finishedStrokeColor);
        canvas.drawArc(rectF, finishedStartAngle, finishedSweepAngle, false, paint);

        String text = String.valueOf(getProgress());
        String suffix = "%";
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        float textHeight = textPaint.descent() + textPaint.ascent();
        float textBaseline = (getHeight() - textHeight) / 2.0f;
        canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, textBaseline, textPaint);

        textPaint.setTextSize(suffixTextSize);
        float suffixHeight = textPaint.descent() + textPaint.ascent();
        canvas.drawText(suffix, (getWidth() + textPaint.measureText(text)) / 2.0f + suffixTextSize, textBaseline + textHeight - suffixHeight, textPaint);

        textPaint.setTextSize(bottomTextSize);
        float bottomTextBaseline = getHeight() - (float) Math.tan(15/180f*Math.PI) * getWidth() / 2 - (textPaint.descent() + textPaint.ascent());
        canvas.drawText(getBottomText(), (getWidth() - textPaint.measureText(getBottomText())) / 2.0f, bottomTextBaseline, textPaint);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STROKE_WIDTH, getStrokeWidth());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_SIZE, getSuffixTextSize());
        bundle.putFloat(INSTANCE_BOTTOM_TEXT_SIZE, getBottomTextSize());
        bundle.putString(INSTANCE_BOTTOM_TEXT, getBottomText());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putBoolean(INSTANCE_SHOW_TEXT, showText);
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putFloat(INSTANCE_ARC_ANGLE, getArcAngle());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            strokeWidth = bundle.getFloat(INSTANCE_STROKE_WIDTH);
            suffixTextSize = bundle.getFloat(INSTANCE_SUFFIX_TEXT_SIZE);
            bottomTextSize = bundle.getFloat(INSTANCE_BOTTOM_TEXT_SIZE);
            bottomText = bundle.getString(INSTANCE_BOTTOM_TEXT);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            showText = bundle.getBoolean(INSTANCE_SHOW_TEXT);
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            suffixText = bundle.getString(INSTANCE_SUFFIX);
            initPainters();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
