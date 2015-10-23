package aks.geotrends.android.db;

public class Keyword {

	private Long id;
	private String keyword;
	private String addedDate;
	private String regionShort;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(String addedDate) {
		this.addedDate = addedDate;
	}

	public String getRegionShort() {
		return regionShort;
	}

	public void setRegionShort(String regionShort) {
		this.regionShort = regionShort;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
