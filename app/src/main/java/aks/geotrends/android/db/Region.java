package aks.geotrends.android.db;

public class Region {

	private Long id;
	private String regionShort;
	private String region;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRegionShort() {
		return regionShort;
	}

	public void setRegionShort(String regionShort) {
		this.regionShort = regionShort;
	}
}
