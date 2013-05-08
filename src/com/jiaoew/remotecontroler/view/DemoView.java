/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by ICOMSYSTECH Co.,Ltd.
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    AFreeChart: http://code.google.com/p/afreechart/
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -----------------
 * ChartView.java
 * -----------------
 * (C) Copyright 2010, by ICOMSYSTECH Co.,Ltd.
 *
 * Original Author:  Niwano Masayoshi (for ICOMSYSTECH Co.,Ltd);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 19-Nov-2010 : Version 0.0.1 (NM);
 * 14-Jan-2011 : renamed method name
 * 14-Jan-2011 : Updated API docs
 */ 

package com.jiaoew.remotecontroler.view;

import org.afree.ui.RectangleInsets;
import org.afree.chart.ChartTouchListener;
import org.afree.chart.ChartRenderingInfo;
import org.afree.chart.AFreeChart;
import org.afree.chart.event.ChartChangeEvent;
import org.afree.chart.event.ChartChangeListener;
import org.afree.chart.event.ChartProgressEvent;
import org.afree.chart.event.ChartProgressListener;
import org.afree.graphics.geom.Dimension;
import org.afree.graphics.geom.RectShape;
import org.afree.graphics.SolidColor;

import java.util.EventListener;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;


public class DemoView extends View 
    implements ChartChangeListener, ChartProgressListener{
    
    /** The user interface thread handler. */
    private Handler mHandler;

    public DemoView(Context context) {
        super(context);
        mHandler = new Handler();
        this.initialize();
    }
    
    public DemoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler = new Handler();
        this.initialize();
    }

    /**
     * initialize parameters
     */
    private void initialize() {
        this.chartMotionListeners = new CopyOnWriteArrayList<ChartTouchListener>();
        this.info = new ChartRenderingInfo();
        this.minimumDrawWidth = DEFAULT_MINIMUM_DRAW_WIDTH;
        this.minimumDrawHeight = DEFAULT_MINIMUM_DRAW_HEIGHT;
        this.maximumDrawWidth = DEFAULT_MAXIMUM_DRAW_WIDTH;
        this.maximumDrawHeight = DEFAULT_MAXIMUM_DRAW_HEIGHT;
        new SolidColor(Color.BLUE);
        new SolidColor(Color.argb(0, 0, 255, 63));
    }
    
    /**
     * Default setting for buffer usage.  The default has been changed to
     * <code>true</code> from version 1.0.13 onwards, because of a severe
     * performance problem with drawing the zoom RectShape using XOR (which
     * now happens only when the buffer is NOT used).
     */
    public static final boolean DEFAULT_BUFFER_USED = true;

    /** The default panel width. */
    public static final int DEFAULT_WIDTH = 680;

    /** The default panel height. */
    public static final int DEFAULT_HEIGHT = 420;
    
    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 10;

    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 10;

    /** The default limit above which chart scaling kicks in. */
    public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 1024;

    /** The default limit above which chart scaling kicks in. */
    public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 1000;
    
    /** The minimum size required to perform a zoom on a RectShape */
    public static final int DEFAULT_ZOOM_TRIGGER_DISTANCE = 10;

    /** The minimum size required to perform a move on a RectShape */
    public static final int DEFAULT_MOVE_TRIGGER_DISTANCE = 10;

    /** The chart that is displayed in the panel. */
    private AFreeChart chart;
    
    /** Storage for registered (chart) touch listeners. */
    private transient CopyOnWriteArrayList<ChartTouchListener> chartMotionListeners;

    /** The drawing info collected the last time the chart was drawn. */
    private ChartRenderingInfo info;

    /** The scale factor used to draw the chart. */
    private double scaleX;

    /** The scale factor used to draw the chart. */
    private double scaleY;

    
    private RectangleInsets insets = null;
    

    private int minimumDrawWidth;

    private int minimumDrawHeight;

    private int maximumDrawWidth;

    private int maximumDrawHeight;

    private Dimension size = null;

    /** The chart anchor point. */
    private PointF anchor;
  
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        this.insets = new RectangleInsets(0,0,0,0);
        this.size = new Dimension(w, h);
    }

    private RectangleInsets getInsets() {
        return this.insets;
    }

    /**
     * Returns the X scale factor for the chart.  This will be 1.0 if no
     * scaling has been used.
     *
     * @return The scale factor.
     */
    public float getScaleX() {
        return (float) this.scaleX;
    }

    public float getScaleY() {
        return (float) this.scaleY;
    }

    public void setChart(AFreeChart chart) {

        // stop listening for changes to the existing chart
        if (this.chart != null) {
            this.chart.removeChangeListener(this);
            this.chart.removeProgressListener(this);
        }

        // add the new chart
        this.chart = chart;
        if (chart != null) {
            this.chart.addChangeListener(this);
            this.chart.addProgressListener(this);
        }
        repaint();

    }

       public int getMinimumDrawWidth() {
        return this.minimumDrawWidth;
    }

    public void setMinimumDrawWidth(int width) {
        this.minimumDrawWidth = width;
    }
    
    public int getMaximumDrawWidth() {
        return this.maximumDrawWidth;
    }

     public void setMaximumDrawWidth(int width) {
        this.maximumDrawWidth = width;
    }

    public int getMinimumDrawHeight() {
        return this.minimumDrawHeight;
    }

    public void setMinimumDrawHeight(int height) {
        this.minimumDrawHeight = height;
    }

    public int getMaximumDrawHeight() {
        return this.maximumDrawHeight;
    }

    public void setMaximumDrawHeight(int height) {
        this.maximumDrawHeight = height;
    }

    public ChartRenderingInfo getChartRenderingInfo() {
        return this.info;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paintComponent(canvas);
    }

    public void paintComponent(Canvas canvas) {

        // first determine the size of the chart rendering area...
        Dimension size = getSize();
        RectangleInsets insets = getInsets();
        RectShape available = new RectShape(insets.getLeft(), insets.getTop(),
                size.getWidth() - insets.getLeft() - insets.getRight(),
                size.getHeight() - insets.getTop() - insets.getBottom());

        double drawWidth = available.getWidth();
        double drawHeight = available.getHeight();
        this.scaleX = 1.0;
        this.scaleY = 1.0;

        if (drawWidth < this.minimumDrawWidth) {
            this.scaleX = drawWidth / this.minimumDrawWidth;
            drawWidth = this.minimumDrawWidth;
        }
        else if (drawWidth > this.maximumDrawWidth) {
            this.scaleX = drawWidth / this.maximumDrawWidth;
            drawWidth = this.maximumDrawWidth;
        }

        if (drawHeight < this.minimumDrawHeight) {
            this.scaleY = drawHeight / this.minimumDrawHeight;
            drawHeight = this.minimumDrawHeight;
        }
        else if (drawHeight > this.maximumDrawHeight) {
            this.scaleY = drawHeight / this.maximumDrawHeight;
            drawHeight = this.maximumDrawHeight;
        }

        RectShape chartArea = new RectShape(0.0, 0.0, drawWidth,
                drawHeight);

        this.chart.draw(canvas, chartArea, this.anchor, this.info);
        
        this.anchor = null;
    }

    public Dimension getSize() {
        return this.size;
    }
  
    public PointF getAnchor() {
        return this.anchor;
    }

    public ChartRenderingInfo getInfo() {
        return info;
    }

    protected void setAnchor(PointF anchor) {
        this.anchor = anchor;
    }
	
    public AFreeChart getChart() {
        return this.chart;
    }
    
    public void addChartTouchListener(ChartTouchListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.chartMotionListeners.add(listener);
    }

    public void removeChartTouchListener(ChartTouchListener listener) {
        this.chartMotionListeners.remove(listener);
    }

    public EventListener[] getListeners() {
        return this.chartMotionListeners.toArray(new ChartTouchListener[0]);
    }

    public void repaint() {
        mHandler.post(new Runnable() {
            public void run() {
                invalidate();
            }
        });
    }
    
    @Override
    public void chartChanged(ChartChangeEvent event) {
        repaint();
    }

    @Override
    public void chartProgress(ChartProgressEvent event) {
    }
}
