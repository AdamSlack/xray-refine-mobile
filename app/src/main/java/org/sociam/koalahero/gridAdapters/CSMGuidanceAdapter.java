package org.sociam.koalahero.gridAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        ImageView guidanceRatingIcon = (ImageView) convertView.findViewById(R.id.guidanceRatingIcon);
        TextView ratingLabelDescription = (TextView) convertView.findViewById(R.id.ratingLabelDescription);

        Integer rating = guidance.rating;
        String description = guidance.description;

        categoryName.setText(this.getGuidanceCategoryString(guidance.category));
        categoryRating.setText(String.valueOf(rating));

        if(rating == 5) {
            guidanceRatingIcon.setImageDrawable(convertView.getContext().getDrawable(R.drawable.happy_face));
            ratingLabelDescription.setText("Looks Great!");
        }
        else if(rating == 4) {
            guidanceRatingIcon.setImageDrawable(convertView.getContext().getDrawable(R.drawable.smile_icon));
            ratingLabelDescription.setText("Seems Good.");
        }
        else if(rating == 3) {
            guidanceRatingIcon.setImageDrawable(convertView.getContext().getDrawable(R.drawable.neutral_icon));
            ratingLabelDescription.setText("It's Okay.");
        }
        else if(rating == 2) {
            guidanceRatingIcon.setImageDrawable(convertView.getContext().getDrawable(R.drawable.sad_icon));
            ratingLabelDescription.setText("Not Great.");
        }
        else if(rating == 1) {
            guidanceRatingIcon.setImageDrawable(convertView.getContext().getDrawable(R.drawable.sad_face));
            ratingLabelDescription.setText("Oh No!");
        }
        else if(rating == 0 && !description.equals("No Rating")) {
            guidanceRatingIcon.setImageDrawable(convertView.getContext().getDrawable(R.drawable.thinking_face));
            ratingLabelDescription.setText("Be Wary.");
        }
        else {
            guidanceRatingIcon.setImageDrawable(convertView.getContext().getDrawable(R.drawable.question_mark_icon));
            ratingLabelDescription.setText("No Data.");
        }

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
