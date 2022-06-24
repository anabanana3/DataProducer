package es.uv.adiez.domain;

import java.util.Date;
import java.util.List;


public class File {

	private String id;
	private String title;
	private String description;
	private List<String> keywords;
	private List<Object> data;
	private Date timestamp;
	private Status status;
	private int size;
	
	public File() {}
	public File(String title, String description, List<String> keywords, List<Object> data) {
		this.title = title;
		this.description = description;
		this.keywords = keywords;
		this.data = data;
		this.timestamp = new Date();
		this.status = Status.pending;
		this.size = data.size();
	}
	public File(String title, String description, List<String> keywords, List<Object> data, int size) {
		this.title = title;
		this.description = description;
		this.keywords = keywords;
		this.data = data;
		this.timestamp = new Date();
		this.status = Status.pending;
		this.size = size;
	}
	public File(String title, String description, List<String> keywords, List<Object> data, Status status) {
		this.title = title;
		this.description = description;
		this.keywords = keywords;
		this.data = data;
		this.timestamp = new Date();
		this.status = status;
		this.size = data.size();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public List<Object> getData() {
		return data;
	}
	public void setData(List<Object> data) {
		this.data = data;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	//pendiente de revisión, en preparación, erróneo o publicado
	public enum Status {
		pending,
		preparing,
		error,
		published
	}
}