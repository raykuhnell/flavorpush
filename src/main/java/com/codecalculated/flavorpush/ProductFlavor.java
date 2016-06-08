package com.codecalculated.flavorpush;

/**
 * Created by raykuhnell on 5/12/16.
 */
public class ProductFlavor {

    private String name;
    private String applicationName;
    private String packageName;
    private String serviceAccountEmail;
    private String keyFile;
    private String apkFilePath;

    public String getName() {
        return name;
    }

    public void setName(String key) {
        this.name = key;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getServiceAccountEmail() {
        return serviceAccountEmail;
    }

    public void setServiceAccountEmail(String serviceAccountEmail) {
        this.serviceAccountEmail = serviceAccountEmail;
    }

    public String getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }
}
