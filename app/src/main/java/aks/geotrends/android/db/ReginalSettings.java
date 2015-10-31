package aks.geotrends.android.db;

import java.util.Date;

/**
 * Created by a77kumar on 2015-10-31.
 */
public class ReginalSettings {

    private String regionCode;
    private Long id;
    private Date refreshDate;
    private boolean isFavorite;
    private boolean isDisplayed;

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRefreshDate() {
        return refreshDate;
    }

    public void setRefreshDate(Date refreshDate) {
        this.refreshDate = refreshDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public void setIsDisplayed(boolean isDisplayed) {
        this.isDisplayed = isDisplayed;
    }
}
