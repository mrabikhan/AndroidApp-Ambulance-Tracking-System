package com.example.ambulancetrackingmodule.presenter;

public class showLoading {
    private final UpdateUI mUpdateUIListener;

    public showLoading(UpdateUI mUpdateUIListener) {
        this.mUpdateUIListener = mUpdateUIListener;
    }

    public interface UpdateUI {

        void showProgressBar();

        void hideProgressBar();

    }
}
