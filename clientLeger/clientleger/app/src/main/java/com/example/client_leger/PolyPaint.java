package com.example.client_leger;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

public class PolyPaint extends Activity {
    private CanvasView mDrawingView = null;
    private MenuItem imgPen, imgErase, brushIncrease, brushDecrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_page);
        initializeObject();
        SocketSingleton.get(getApplicationContext()).OnLaunchParty(OnLaunchParty);
    }

    Emitter.Listener OnLaunchParty = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("LAUNCH", "BRUHs ");
                    JSONObject data;
                    try {
                        data = new JSONObject((String) args[0]);

                        Log.i("LAUNCH", data + " ");

                        try {

                        } catch (Exception e) {
                            return;
                        }

                    } catch (Exception e) {}
                }
            });
        }
    };

    private void initializeObject() {
        imgPen = (MenuItem) findViewById(R.id.imgPen);
        imgErase = (MenuItem) findViewById(R.id.imgErase);
        mDrawingView = (CanvasView) findViewById(R.id.canvas);
        brushIncrease = (MenuItem) findViewById(R.id.brushIncrease);
        brushDecrease = (MenuItem) findViewById(R.id.brushDecrease);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paint_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        MenuItem eraser = menu.findItem(R.id.imgErase);
        eraser.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mDrawingView.setErase(true, false);
                mDrawingView.setBrushSize(15);
                return true;
            }
        });
        MenuItem pen = menu.findItem(R.id.imgPen);
        pen.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mDrawingView.setErase(false, false);
                mDrawingView.setBrushSize(15);
                mDrawingView.setLastBrushSize(15);
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//            Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.circle_brush:
                SocketSingleton.get(getApplicationContext()).LaunchParty();
                mDrawingView.setErase(false, false);
                mDrawingView.drawPaint.setStrokeCap(Paint.Cap.ROUND);
                return true;
            case R.id.square_brush:
                mDrawingView.setErase(false, false);
                mDrawingView.drawPaint.setStrokeCap(Paint.Cap.SQUARE);
                return true;
            case R.id.color_black:
                mDrawingView.changeColor(Color.BLACK);
                return true;
            case R.id.color_blue:
                mDrawingView.changeColor(Color.BLUE);
                return true;
            case R.id.color_red:
                mDrawingView.changeColor(Color.RED);
                return true;
            case R.id.color_green:
                mDrawingView.changeColor(Color.GREEN);
                return true;
            case R.id.imgErase:
                return true;
            case R.id.brushIncrease:
                mDrawingView.increaseBrushWidth();
                return true;
            case R.id.brushDecrease:
                mDrawingView.decreaseBrushWidth();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

