package com.titanarchers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class FragmentOptions extends Fragment {
    private ArrowPointViewModel model;

    private View v_target;
    private String fn;
    private static final int CREATE_REQUEST_CODE = 40;

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        model = ViewModelProviders.of(this.getActivity()).get(ArrowPointViewModel.class);
    }

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_options, container);

        // Get Fragment belonged Activity
        final FragmentActivity fragmentBelongActivity = getActivity();

        if(retView!=null)
        {

            final Button androidButton = retView.findViewById(R.id.fOptionAndroid);
            final Button iosButton = retView.findViewById(R.id.fOptionIos);
            final Button windowsButton = retView.findViewById(R.id.fOptionWindows);

            // Click this button to REDO Last ArrowPoint placed
            androidButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model != null) {
                        if (model.getListSize() <= 0){
                            Toast.makeText(fragmentBelongActivity, "Nothing to redo", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //delete last ArrowPoint added
                        int pos = model.getListSize() - 1;
                        if (pos >= 0) {
                            model.deleteArrowPoint(pos);
                        }

                        // Do not use fragmentBelongActivity.getFragmentManager() method which is not compatible with older android os version. .
                        FragmentManager fragmentManager = fragmentBelongActivity.getSupportFragmentManager();

                        // Get scores Fragment object.
                        Fragment scoreFragment = fragmentManager.findFragmentById(R.id.fragmentScores);

                        // Get the TextView object in right Fragment.
                        //TODO; use Objects.requireNonNull() but need API 19 or greater - currently API17
                        String frScoreCell = "frS" + (pos + 1);
                        String PACKAGE_NAME = getActivity().getPackageName();
                        int temp;
                        if (PACKAGE_NAME != null) {
                            temp = getResources().getIdentifier(frScoreCell, "id", PACKAGE_NAME);
                            final TextView rightFragmentTextView = (TextView) scoreFragment.getView().findViewById(temp);
                            // Set text in right Fragment TextView.
                            rightFragmentTextView.setText("00");
                        } else {
                            Log.d("MyINFO", "Error getting current score TextView in FragmentScores.java");
                        }

                        Toast.makeText(fragmentBelongActivity, "Redo last arrow", Toast.LENGTH_LONG).show();
                    }
                }
            });

            // Click this button will show a Toast popup - TODO Indevelopment - Enter Distance????.
            iosButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(fragmentBelongActivity, "In development", Toast.LENGTH_SHORT).show();
                }
            });

            // Click this button will show ScoreCard (WebView with PNG img & HTML)
            windowsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model != null && model.getListSize() > 0) {
                        // Do not use fragmentBelongActivity.getFragmentManager() method which is not compatible with older android os version. .
                        FragmentManager fragmentManager = fragmentBelongActivity.getSupportFragmentManager();

                        // Get scores Fragment object.
                        Fragment scorecard = fragmentManager.findFragmentById(R.id.fragmentScoreCard);

                        FragmentTransaction ft = fragmentBelongActivity.getSupportFragmentManager().beginTransaction();
                        if (scorecard.isHidden()) {
                            ft.show(scorecard);
                            //windowsButton.setText("Hide Card");
                        } else {
                            ft.hide(scorecard);
                            //windowsButton.setText("Show Card");
                        }
                        ft.commit();
                    } else {
                        Toast.makeText(fragmentBelongActivity, "Nothing to show.", Toast.LENGTH_LONG).show();

                    }
/*                    AlertDialog alertDialog = new AlertDialog.Builder(fragmentBelongActivity).create();
                    alertDialog.setMessage("You click Windows button.");
                    alertDialog.show();*/
                }
            });
        }

        return retView;
    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode,resultCode,resultData);

        Uri currentUri;

        if (resultCode == Activity.RESULT_OK)
        {

            if (requestCode == CREATE_REQUEST_CODE)
            {
                if (resultData != null) {
                    currentUri = resultData.getData();
                    //writeViewImage(currentUri);
                    takeScreenShot(v_target,currentUri);
                }
            }
        }
    }
*/
    /*

    public void sendScreenShot(File imageFile) {
        Uri uri = Uri.fromFile(imageFile);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //String email = "test@gmail.com";
        //shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Screenshot");
        //shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
	    startActivity(Intent.createChooser(shareIntent, "Share image"));
    }*/
/*
    public void takeScreenShot(View v1, Uri uri){
        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + fn;

        // create bitmap screen capture
        Bitmap bitmap;
        //v1 = findViewById(R.id.fragmentTarget);
        v1.setDrawingCacheEnabled(true);

        // bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        bitmap = loadBitmapFromView(v1, v1.getWidth(), v1.getHeight());
        v1.setDrawingCacheEnabled(false);
        //OutputStream fout = null;

        try {
            //OutputStream outFile = getActivity().getContentResolver().openOutputStream(uri);
            //bm.compress(Bitmap.CompressFormat.JPEG, 50,outFile);
            OutputStream fout = getActivity().getContentResolver().openOutputStream(uri);
            //File imageFile = new File(mPath);

            //fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //sendScreenShot(imageFile);
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, width, height);

        //Get the view’s background
        Drawable bgDrawable =v.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(c);
        else
            //does not have background drawable, then draw white background on the canvas
            c.drawColor(Color.WHITE);
        v.draw(c);
        return b;
    }
*/
}
