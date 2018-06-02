package com.sociam.refine;
/**
 * Graph Data Model - Singleton
 *
 * Holds data used to populate MPAndroidCharts that required some extensive processing to build.
 *
 * hostDataHorizontalDataset is used in the main activity view of all hosts and usage time.
*/

import com.github.mikephil.charting.data.BarData;

public class GraphDataModel {

    private static GraphDataModel INSTANCE = null;

    public BarData hostDataHorizontalDataSet;

    private GraphDataModel() {

    }

    public static GraphDataModel getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GraphDataModel();
        }
        return INSTANCE;
    }
}
