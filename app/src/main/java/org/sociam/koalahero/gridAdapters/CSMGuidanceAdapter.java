package org.sociam.koalahero.gridAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.sociam.koalahero.R;
import org.sociam.koalahero.csm.CSMParentalGuidance;
import org.sociam.koalahero.csm.GuidanceCategory;

import java.util.ArrayList;


public class CSMGuidanceAdapter extends ArrayAdapter {

    Context context;

    public CSMGuidanceAdapter(Context context, ArrayList<CSMParentalGuidance.Guidance> guidances) {
        super(context, 0, guidances);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CSMParentalGuidance.Guidance guidance = (CSMParentalGuidance.Guidance) getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parental_guidance_list_item_view, parent, false);
        }

        TextView categoryName = (TextView) convertView.findViewById(R.id.guidanceTitleTextView);
        TextView categoryRating = (TextView) convertView.findViewById(R.id.guidanceCategoryRatingTextView);

        categoryName.setText(this.getGuidanceCategoryString(guidance.category));
        categoryRating.setText(String.valueOf(guidance.rating));


        return convertView;
    }


    public String getGuidanceCategoryString(GuidanceCategory category) {
        switch (category) {
            case SEX:
                return this.context.getResources().getString(R.string.sex_guidance_title);

            case DRUGS:
                return this.context.getResources().getString(R.string.drugs_guidance_title);

            case LANGUAGE:
                return this.context.getResources().getString(R.string.language_guidance_title);

            case VIOLENCE:
                return this.context.getResources().getString(R.string.violence_guidance_title);

            case EDUCATION:
                return this.context.getResources().getString(R.string.education_guidance_title);

            case CONSUMERISM:
                return this.context.getResources().getString(R.string.consumerism_guidance_title);

            case PLAYABILITY:
                return this.context.getResources().getString(R.string.playability_guidance_title);

            default:
                return "Unknown Category";
        }
    }
}
