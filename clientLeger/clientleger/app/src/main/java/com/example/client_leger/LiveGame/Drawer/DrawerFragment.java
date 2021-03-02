package com.example.client_leger.LiveGame.Drawer;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.client_leger.CanvasView;
import com.example.client_leger.GameService;
import com.example.client_leger.LiveGame.DrawingUpdate;
import com.example.client_leger.LiveGame.Game;
import com.example.client_leger.LiveGame.GameFragment;
import com.example.client_leger.R;
import com.example.client_leger.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DrawerFragment extends Fragment {
    private GameService gameService = new GameService();
    public CanvasView mDrawingView;
    public Game currentGame = null;

    public DrawerFragment() {
        // Required empty public constructor
    }

    public static DrawerFragment newInstance() {
        return new DrawerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SocketSingleton.get(getContext()).OnStartGame(new Emitter.Listener() {
            @Override
            public void call(Object... args)
            {
                draw((Game)args[0]);
            }
        }, true);
        SocketSingleton.get(getContext()).OnStroke(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                gameService.drawStroke(mDrawingView, ((DrawingUpdate)args[0]));
            }
        });

//        String urlGif = "file:///android_asset/images/fireworks.gif";
//        ImageView imageView = (ImageView)getActivity().findViewById(R.id.imageView);
//        Uri uri = Uri.parse(urlGif);
//        Glide.with(getActivity().getApplicationContext()).load(uri).into(imageView);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeObject(view);
    }

    public void activatePenTool()
    {
        mDrawingView.setErase(false, false);
    }

    public void activateEraserTool()
    {
        mDrawingView.setErase(true, false);
    }

    public void activateStrokeEraserTool()
    {
        mDrawingView.setErase(true, true);
    }

    public void activateCircleBrush()
    {
        mDrawingView.drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void activateSquareBrush()
    {
        mDrawingView.drawPaint.setStrokeCap(Paint.Cap.SQUARE);
    }

    public void setBrushSize(int position)
    {
        mDrawingView.setBrushSize((float) Math.pow(1.7, position + 2));
    }

    public void setBrushColor(int color)
    {
        mDrawingView.changeColor(color);
    }

    private void initializeObject(View view)
    {
        mDrawingView = view.findViewById(R.id.canvas);
        mDrawingView.setTouchable(false);
    }

    private void draw(final Game game)
    {
        ((GameFragment) getParentFragment()).indicateDrawer(game);
        ((GameFragment) getParentFragment()).getColorPicker().dismissDialog();
        GameService.drawGame(mDrawingView, game);
    }
}
