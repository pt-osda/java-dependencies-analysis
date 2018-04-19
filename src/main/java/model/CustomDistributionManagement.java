package model;

public class CustomDistributionManagement {
    private CustomDeploymentRepository repository;
    private CustomDeploymentRepository snapshotRepository;
    private CustomSite site;
    private String downloadUrl;
    private CustomRelocation relocation;
    private String status;

    public CustomDeploymentRepository getRepository() {
        return repository;
    }

    public CustomDeploymentRepository getSnapshotRepository() {
        return snapshotRepository;
    }

    public CustomSite getSite() {
        return site;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public CustomRelocation getRelocation() {
        return relocation;
    }

    public String getStatus() {
        return status;
    }

    public void setRepository(CustomDeploymentRepository repository) {
        this.repository = repository;
    }

    public void setSnapshotRepository(CustomDeploymentRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    public void setSite(CustomSite site) {
        this.site = site;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setRelocation(CustomRelocation relocation) {
        this.relocation = relocation;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}