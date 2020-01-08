package com.titanarchers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FragmentScoreCard extends Fragment {

    private ArrowPointViewModel model;

    WebView wv_ScoreCard;
    Button btn_share;
    String html;

    private String fn, scoreCardHTML;
    private static final int CREATE_REQUEST_CODE = 40;

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        model = ViewModelProviders.of(this.getActivity()).get(ArrowPointViewModel.class);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            if (model != null) {


                List<ArrowPoint> apList = model.getArrowPoints().getValue();
                final StringBuilder sb_HTML = new StringBuilder();

                //Create Header of ScoreCard which will be a table with 7 columns and 6 rows
                //with last row being for score total
                html = "<table border='1' bordercolor='green'> <tr><b><th>Arrow1</th><th>Arrow2</th><th>Arrow3</th><th>Arrow4</th><th>Arrow5</th><th>Arrow6</th><th>Total</th></b></tr>";
                sb_HTML.append(html);
                sb_HTML.append("<tr>");

                int subtotal = 0;
                int total = 0;
                ArrowPoint ap;

                for (int i = 0; i < apList.size(); i++) {
                    ap = apList.get(i);
                    subtotal += (ap.score == 11) ? 10 : ap.score;
                    total += (ap.score == 11) ? 10 : ap.score;
                    String tempStr;

                    tempStr = "<td>" + apList.get(i).score + "</td>";
                    sb_HTML.append(tempStr);

                    if ((i + 1) % 6 == 0) {
                        tempStr = "<td>" + subtotal + "</td>";
                        sb_HTML.append(tempStr);
                        sb_HTML.append("</tr>");
                        subtotal = 0;
                    }

                }
                sb_HTML.append("<tr> " +
                        "<td colspan='7'>Total = " + total + "<td></tr>");
                sb_HTML.append("</table></br>");
                html = sb_HTML.toString();
                wv_ScoreCard.loadData(html, "text/html; charset=UTF-8", null);
                scoreCardHTML = html;
            }
        }
    }

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_scorecard, container);

        if (retView != null) {
            wv_ScoreCard = retView.findViewById(R.id.wv_ScoreCard);
            btn_share = retView.findViewById(R.id.btn_share);

            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intentShareFile = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intentShareFile.addCategory(Intent.CATEGORY_OPENABLE);
                    intentShareFile.setType("text/html; charset=UTF-8");

                    String timeStr = DateFormat.format("dd_MM_yyyy_hh_mm_ss", System.currentTimeMillis()).toString();
                    fn = "TitanScoreCard_" + timeStr + ".html";

                    intentShareFile.putExtra(Intent.EXTRA_TITLE, fn);
                    startActivityForResult(intentShareFile, CREATE_REQUEST_CODE);
                }
            });

        }
        return retView;

    }



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
                    writeFileContent(currentUri);
                }
            }
        }
    }

    private void writeFileContent(Uri uri)
    {
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream outFile = new FileOutputStream(pfd.getFileDescriptor());

            scoreCardHTML = "<h2>" + fn + "</h2></br>" + scoreCardHTML;
            outFile.write(scoreCardHTML.getBytes());
            outFile.close();
            pfd.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("Share", "shareHtmlFile() FILE NOT FOUND ERROR " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Share", "shareHtmlFile() IO ERROR " + e.getMessage());
        }
    }
}
