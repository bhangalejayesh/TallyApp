package com.example.RestApi.entities;


public class ManageProperties {
	private static ManageProperties instance;
    private final AppProperties appProperties;
    
	public ManageProperties() {
		super();
		//initialize appproperties
		this.appProperties = new AppProperties();
	}

	// to get the singleton instance
	public static ManageProperties getInstance() {
        if (instance == null) {
            synchronized (ManageProperties.class) {
                if (instance == null) {
                    instance = new ManageProperties();
                }
            }
        }
        return instance;
    }
	
	// Method to get the AppProperties instance
    public AppProperties getAppProperties() {
        return this.appProperties;
    }
}
