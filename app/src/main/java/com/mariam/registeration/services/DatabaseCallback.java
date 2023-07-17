package com.mariam.registeration.services;

public interface DatabaseCallback {
    void onDataFetched(String result);
    void onDataFetchError(String errorMessage);
}

