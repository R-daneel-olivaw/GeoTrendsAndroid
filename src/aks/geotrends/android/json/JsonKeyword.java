package aks.geotrends.android.json;

import java.util.Date;

public class JsonKeyword {

	private String keyword;
	private Date addedDate;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	@Override
	public String toString() {
		return "JsonKeyword [keyword=" + keyword + ", addedDate=" + addedDate + "]";
	}
}
