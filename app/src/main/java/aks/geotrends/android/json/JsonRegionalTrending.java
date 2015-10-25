package aks.geotrends.android.json;

import java.util.List;

public class JsonRegionalTrending {
	
	private JsonRegion region;
	
	private List<JsonKeyword> trending;

	public JsonRegion getRegion() {
		return region;
	}

	public void setRegion(JsonRegion region) {
		this.region = region;
	}

	public List<JsonKeyword> getTrending() {
		return trending;
	}

	public void setTrending(List<JsonKeyword> trending) {
		this.trending = trending;
	}

	@Override
	public String toString() {
		return "JsonRegionalTrending [region=" + region + ", trending=" + trending + "]";
	}
	
}
