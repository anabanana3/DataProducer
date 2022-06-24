package es.uv.adiez.domain;

public class FileSQL {
	private String fileId;
	private User validator;
	private User owner;
	private int previews;
	private int downloads;
	
	public FileSQL() {}
	
	public FileSQL(String id, User validator, User owner, int previews, int downloads) {
		this.fileId = id;
		this.validator = validator;
		this.owner = owner;
		this.previews = previews;
		this.downloads = downloads;
	}
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public User getValidator() {
		return validator;
	}
	public void setValidator(User validator) {
		this.validator = validator;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public int getPreviews() {
		return previews;
	}
	public void setPreviews(int previews) {
		this.previews = previews;
	}
	public int getDownloads() {
		return downloads;
	}
	public void setDownloads(int downloads) {
		this.downloads = downloads;
	}
}
