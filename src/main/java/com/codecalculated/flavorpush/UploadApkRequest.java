package com.codecalculated.flavorpush;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Apklistings;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Commit;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Insert;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Apks.Upload;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Tracks.Update;
import com.google.api.services.androidpublisher.model.Apk;
import com.google.api.services.androidpublisher.model.ApkListing;
import com.google.api.services.androidpublisher.model.AppEdit;
import com.google.api.services.androidpublisher.model.Track;

public class UploadApkRequest {

    static final String MIME_TYPE_APK = "application/vnd.android.package-archive";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static HttpTransport HTTP_TRANSPORT;

    public static void send(String track, ProductFlavor productFlavor, String releaseNotes) {
        try {
            // Authorization.
            if (null == HTTP_TRANSPORT) {
                HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            }

            // Create the API service.
            GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(productFlavor.getServiceAccountEmail())
                .setServiceAccountScopes(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER))
                .setServiceAccountPrivateKeyFromP12File(new File(productFlavor.getKeyFile()))
                .build();

            // Set up and return API client.
            AndroidPublisher service = new AndroidPublisher.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(productFlavor.getApplicationName())
                .build();
            final Edits edits = service.edits();

            // Create a new edit to make changes.
            Insert editRequest = edits.insert(productFlavor.getPackageName(), null);
            AppEdit edit = editRequest.execute();
            final String editId = edit.getId();

            // Upload new apk to developer console
            final AbstractInputStreamContent apkFile = new FileContent(MIME_TYPE_APK, new File(productFlavor.getApkFilePath()));
            Upload uploadRequest = edits.apks().upload(productFlavor.getPackageName(), editId, apkFile);
            Apk apk = uploadRequest.execute();
            System.out.println(String.format("Application version %d has been uploaded.", apk.getVersionCode()));

            // Assign apk to track.
            List<Integer> apkVersionCodes = new ArrayList<>();
            apkVersionCodes.add(apk.getVersionCode());
            Update updateTrackRequest = edits
                .tracks()
                .update(productFlavor.getPackageName(),
                    editId,
                    track,
                    new Track().setVersionCodes(apkVersionCodes));
            Track updatedTrack = updateTrackRequest.execute();
            System.out.println(String.format("Track %s has been updated.", updatedTrack.getTrack()));

            // Update recent changes field in apk listing.
            final ApkListing newApkListing = new ApkListing();
            newApkListing.setRecentChanges(releaseNotes);

            Apklistings.Update updateRecentChangesRequest = edits.apklistings()
                .update(productFlavor.getPackageName(),
                    editId,
                    apk.getVersionCode(),
                    Locale.US.toString(),
                    newApkListing);
            updateRecentChangesRequest.execute();
            System.out.println("Recent changes has been updated.");

            // Commit changes for edit.
            Commit commitRequest = edits.commit(productFlavor.getPackageName(), editId);
            AppEdit appEdit = commitRequest.execute();
            System.out.println("Application has been committed successfully.");
        }
        catch (IOException | GeneralSecurityException ex) {
            System.out.println("An error occurred updating the APK. " + ex.toString());
        }
    }
}
