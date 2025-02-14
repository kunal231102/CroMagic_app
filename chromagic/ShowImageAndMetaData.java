package com.example.chromagic;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowImageAndMetaData extends Dialog {

    private final Uri uri;
    private final String metadata;
    ImageView currImage;
    TextView metaData;

    public ShowImageAndMetaData(Context context, Uri uri, String metadata) {
        super(context);
        this.uri = uri;
        this.metadata = metadata;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_and_meta_data);

        currImage = findViewById(R.id.current_image);
        metaData = findViewById(R.id.meta_data);

        if (uri != null) {
            currImage.setImageURI(uri);
        }
        metaData.setText(metadata != null ? metadata : "No image details available");
    }
}
