package com.codecalculated.flavorpush;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
	    int count = 0;
        HashMap<String, ProductFlavor> flavors;

        System.out.println("Flavorpush");
        System.out.println("Reading configuration...");

        try {
            flavors = loadConfiguration();
        }
        catch (FileNotFoundException ex) {
            System.out.println("Configuration file not found.");
            return;
        }

        String defaultPath = "";
        try
        {
            defaultPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        }
        catch (URISyntaxException ex)
        {
            System.out.println("An error occurred determining the path to the executable.");
        }

        System.out.println("Enter path to signed APK. (" + defaultPath + ")");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        if (path.isEmpty()) {
            path = defaultPath;
        }

        System.out.println("Scanning directory for APK files...");
        File[] files = new File(path).listFiles();
        if (files.length < 1) {
            System.out.println("No APK files found.");
            return;
        }

        for (final File fileEntry : files) {
            String fileName = fileEntry.getName();
            if (fileName.endsWith(".apk")) {
                String flavor = fileName.substring(fileName.indexOf("-") + 1, fileName.lastIndexOf("-"));
                if (flavors.containsKey(flavor)) {
                    System.out.println("Product flavor " + flavor + " found.");
                    flavors.get(flavor).setApkFilePath(fileEntry.getAbsolutePath());
                    count++;
                }
            }
        }

        System.out.println("Track to deploy (alpha, beta, production): (alpha)");
        String track = scanner.nextLine();
        if (track.isEmpty()) {
            track = "alpha";
        }

        System.out.println("Enter the release notes:");
        String releaseNotes = scanner.nextLine();

        System.out.println("Deploy " + count + " application(s) to Google Play? (y/n)");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {

            for (ProductFlavor productFlavor : flavors.values()) {
                if (!productFlavor.getApkFilePath().isEmpty()) {
                    System.out.println("Submitting " + productFlavor.getApplicationName() + "...");
                    UploadApkRequest.send(track, productFlavor, releaseNotes);
                }
            }
        }
    }

    private static HashMap<String, ProductFlavor> loadConfiguration() throws FileNotFoundException {
        HashMap<String, ProductFlavor> flavors = new HashMap<>();

        String json = new Scanner(new File("src/config.json")).useDelimiter("\\Z").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            ProductFlavor flavor = new ProductFlavor();
            flavor.setApplicationName(obj.getString("applicationName"));
            flavor.setKeyFile(obj.getString("keyFile"));
            flavor.setName(obj.getString("name"));
            flavor.setPackageName(obj.getString("packageName"));
            flavor.setServiceAccountEmail(obj.getString("serviceAccountEmail"));
            flavors.put(flavor.getName(), flavor);
        }

        return flavors;
    }
}
