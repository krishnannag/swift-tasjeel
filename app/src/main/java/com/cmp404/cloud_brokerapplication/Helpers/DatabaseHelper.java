package com.cmp404.cloud_brokerapplication.Helpers;


import android.util.Log;

import com.cmp404.cloud_brokerapplication.Android.BrokerApplication;
import com.cmp404.cloud_brokerapplication.Entities.Insurance;
import com.cmp404.cloud_brokerapplication.Entities.TestingCenter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DatabaseHelper {
    private BrokerApplication application;
    final StitchAppClient client =
            Stitch.initializeDefaultAppClient("cloudbrokerapplication-cnhwv");
    final RemoteMongoClient mongoClient =
            client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
    final RemoteMongoCollection<Document> userCollection =
            mongoClient.getDatabase("cloud-computing").getCollection("broker-user");
    final RemoteMongoCollection<Document> entitiesCollection =
            mongoClient.getDatabase("cloud-computing").getCollection("broker-registered-entity");

    public DatabaseHelper(BrokerApplication application) {
        this.application = application;
        client.getAuth().loginWithCredential(new AnonymousCredential());
    }

    public JSONObject exists(final String email) {
        final ArrayList<Document> documents = new ArrayList<Document>();
        RemoteFindIterable<Document> iterable = userCollection.find();

        Task task = iterable.into(documents);
        try {
            Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Document d : documents) {
            if (d.get("email").equals(email)) {
                return new JSONObject(d);
            }
        }
        return null;
    }

    public JSONObject login(final String email, final String password) {
        JSONObject result = exists(email);
        if (result != null) {
            try {
                if (result.get("email").equals(email) && !result.get("password").equals(password)) {
                    result = new JSONObject();
                    result.put("valid", "false");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("DatabaseHelper_login", result.toString());
        return result;
    }

    public JSONObject signup(final String email,
                             final String password,
                             final String name,
                             final String registrationNo,
                             final String licenseNo,
                             final String creditCard) {

        if (exists(email) == null) {
            Document document = new Document();
            document.put("name", name);
            document.put("email", email);
            document.put("password", password);
            document.put("registration-no", registrationNo);
            document.put("license-no", licenseNo);
            document.put("credit-card", creditCard);
            document.put("insurance", false);
            document.put("testing", false);
            document.put("fines", false);
            document.put("renewal", false);
            document.put("insurance-ref", "");
            document.put("testing-ref", "");
            userCollection.insertOne(document);

            Log.d("DatabaseHelper_signup", new JSONObject(document).toString());
            return new JSONObject(document);


        } else {
            JSONObject result = new JSONObject();
            try {
                result.put("valid", "false");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    public void initInsuranceCompanies() {
        final ArrayList<Document> documents = new ArrayList<Document>();
        RemoteFindIterable<Document> iterable = entitiesCollection.find();

        Task task = iterable.into(documents);
        try {
            Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        application.insuranceCompanies = new ArrayList<>();
        for (Document d : documents) {
            if (d.getString("type").equals("insurance")) {
                d.append("id", d.get("_id").toString());
                d.remove("_id");
                application.insuranceCompanies.add(new Insurance(new JSONObject(d)));
            }
        }
    }

    public void initTestingCenters() {
        final ArrayList<Document> documents = new ArrayList<Document>();
        RemoteFindIterable<Document> iterable = entitiesCollection.find();

        Task task = iterable.into(documents);
        try {
            Tasks.await(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        application.testingCenters = new ArrayList<>();
        for (Document d : documents) {
            if (d.getString("type").equals("testing-center")) {
                d.append("id", d.get("_id").toString());
                d.remove("_id");
                application.testingCenters.add(new TestingCenter(new JSONObject(d)));
            }
        }
    }

    public void updateUserStrings(String key, String value) {
        Bson filter = new Document("email", application.currentUser.getContact());
        Bson newValue = new Document(key, value);
        Bson updateOperationDocument = new Document("$set", newValue);
        userCollection.updateOne(filter, updateOperationDocument);
    }

    public void updateUserBooleans(String key, boolean value) {
        Bson filter = new Document("email", application.currentUser.getContact());
        Bson newValue = new Document(key, value);
        Bson updateOperationDocument = new Document("$set", newValue);
        userCollection.updateOne(filter, updateOperationDocument);
    }
}
