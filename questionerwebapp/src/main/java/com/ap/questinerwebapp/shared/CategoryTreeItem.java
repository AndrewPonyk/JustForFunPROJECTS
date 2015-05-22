package com.ap.questinerwebapp.shared;

import java.util.LinkedHashMap;
import java.util.Map;

public class CategoryTreeItem {
	private String name;
	private Integer nofquestions;
	private Integer nofsubcategories;
	private String filename;
	private Map<String,CategoryTreeItem> children = new LinkedHashMap<String, CategoryTreeItem>();

	public Map<String, CategoryTreeItem> getChildren() {
		return children;
	}

	public CategoryTreeItem(String name, Integer nofquestions,
			Integer nofsubcategories, String filename) {
		super();
		this.name = name;
		this.nofquestions = nofquestions;
		this.nofsubcategories = nofsubcategories;
		this.filename = filename;
	}

	@Override
	public String toString() {
		return name + "[ nofquestions=" + nofquestions
				+ ", nofsubcategories=" + nofsubcategories  +"]";
	}
	public String getName() {
		return name;
	}
	public Integer getNofquestions() {
		return nofquestions;
	}
	public Integer getNofsubcategories() {
		return nofsubcategories;
	}
	public String getFilename() {
		return filename;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setNofquestions(Integer nofquestions) {
		this.nofquestions = nofquestions;
	}

	public void setNofsubcategories(Integer nofsubcategories) {
		this.nofsubcategories = nofsubcategories;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setChildren(Map<String, CategoryTreeItem> children) {
		this.children = children;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((nofquestions == null) ? 0 : nofquestions.hashCode());
		result = prime
				* result
				+ ((nofsubcategories == null) ? 0 : nofsubcategories.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryTreeItem other = (CategoryTreeItem) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nofquestions == null) {
			if (other.nofquestions != null)
				return false;
		} else if (!nofquestions.equals(other.nofquestions))
			return false;
		if (nofsubcategories == null) {
			if (other.nofsubcategories != null)
				return false;
		} else if (!nofsubcategories.equals(other.nofsubcategories))
			return false;
		return true;
	}

}
