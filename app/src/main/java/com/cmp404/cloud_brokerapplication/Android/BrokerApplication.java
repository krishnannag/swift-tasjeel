package com.cmp404.cloud_brokerapplication.Android;

import android.app.Application;

import com.cmp404.cloud_brokerapplication.Entities.Fine;
import com.cmp404.cloud_brokerapplication.Entities.Insurance;
import com.cmp404.cloud_brokerapplication.Entities.TestingCenter;
import com.cmp404.cloud_brokerapplication.Entities.User;
import com.cmp404.cloud_brokerapplication.Helpers.DatabaseHelper;
import com.cmp404.cloud_brokerapplication.Helpers.WebServicesIO;

import org.json.JSONObject;

import java.util.ArrayList;

public class BrokerApplication extends Application {
    public final String BASE_URL = "http://brokerserver-env.zmb8q5sd4a.eu-west-1.elasticbeanstalk.com/broker-server/";
    public final String INSURANCE_PATH = "insurance/";
    public final String TESTING_CENTER_PATH = "testing-center/";
    public final String DUBAI_POLICE_PATH = "dubai-police/";
    public final String RTA_PATH = "rta/";
    public final String INSURANCE_GET_PACKAGES = "get-packages";
    public final String INSURANCE_GET_PLAN = "get-plan/";
    public final String INSURANCE_RENEW_PLAN = "renew-plan/";
    public final String INSURANCE_REGISTER_PACKAGE = "register-package/";
    public final String TESTING_CENTER_GET_TIMINGS = "get-timings";
    public final String TESTING_CENTER_BOOK_TIMING = "book-timing/";
    public final String TESTING_CENTER_BOOKED_TIMING = "booked-timing/";
    public final String DUBAI_POLICE_GET_FINES = "get-fines/";
    public final String DUBAI_POLICE_PAYMENT = "payment/";
    public final String RTA_RENEWAL_FEES = "renewal-fees/";
    public final String RTA_RENEW_REGISTRATION = "renew-registration/";

    public User currentUser;
    public ArrayList<Insurance> insuranceCompanies;
    public ArrayList<TestingCenter> testingCenters;
    public ArrayList<Fine> trafficFines;
    public DatabaseHelper database;
    public WebServicesIO externalInterface;

    public BrokerApplication(){
    }

    public void initDB(){
        database = new DatabaseHelper(this);
        externalInterface = new WebServicesIO(this);
    }

    public void loadProfile(JSONObject user){
        currentUser = new User(this, user);
    }
}
