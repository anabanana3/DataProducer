package es.uv.adiez.services;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.uv.adiez.domain.File;
import es.uv.adiez.domain.FileSQL;
import es.uv.adiez.domain.User;

@Service
public class FileService {
	
	@Value("${enpoint.usersAPI}")
	private String usersURL;
	@Value("${enpoint.filesAPI}")
	private String filesURL;
	private Gson gson = new Gson();
	
	
	/**************************************************************/
	/*							MONGO							  */
	/**************************************************************/
	
	public File create(File file) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+filesURL+"/files";
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new File();
		}
	     
	    ResponseEntity<String> result = restTemplate.postForEntity(uri, file, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 201) {
		   return gson.fromJson(result.getBody(), File.class);
	   }
	   return new File();
	}
	
	public List<File>  findByIds(List<String> ids) {
		List<File> files = new ArrayList<File>();
		RestTemplate restTemplate = new RestTemplate();
		String items = String.join(",", ids);
	     
	    final String baseUrl = "http://"+filesURL+"/files/ids/"+items;
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return files;
		}
	     
	    ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 200) {
		   Type fileListType = new TypeToken<ArrayList<File>>(){}.getType();

		   files = (List<File>) gson.fromJson(result.getBody(), fileListType);
	   }
	   return files;
	}
	
	public File  findById(String id) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+filesURL+"/files/"+id;
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	     
	    ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 200) {
		   return gson.fromJson(result.getBody(), File.class);
	   }
	   return null;
	}
	
	public List<String> findIdsByOwner(User u)
	{
		List<FileSQL> files = findByOwnerSQL(u);
		List<String> ids = files.stream().map(f -> f.getFileId())
                .collect(Collectors.toList());
		return ids;
	}	
	public List<File> findByOwner(User u) {
		List<String> ids = findIdsByOwner(u);
		return findByIds(ids);
	}
	
	public File updateFile(String id, File file) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+filesURL+"/files";
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new File();
		}
	    File f = findById(id);
	    f.setTitle(file.getTitle());
	    f.setKeywords(file.getKeywords());
	    f.setDescription(file.getDescription());
	    ResponseEntity<String> result = restTemplate.postForEntity(uri, f, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 201) {
		   return gson.fromJson(result.getBody(), File.class);
	   }
	   return new File();
	}
	public Boolean deleteFile(String id) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+filesURL+"/files/"+id;
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	    restTemplate.delete(uri);
	     
	   return true;
	}
	
	/**************************************************************/
	/*							SQL								  */
	/**************************************************************/
	
	public FileSQL createSQL(FileSQL file) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+usersURL+"/fileAPI";
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new FileSQL();
		}
	     
	    ResponseEntity<String> result = restTemplate.postForEntity(uri, file, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 201) {
		   return gson.fromJson(result.getBody(), FileSQL.class);
	   }
	   return new FileSQL();
	}
	
	public Boolean deleteFileSQL(String id) {
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+usersURL+"/fileAPI/"+id;
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	    restTemplate.delete(uri);
	     
	   return true;
	}
	
	public void createAll(List<File> posts) {
		//this.fr.saveAll(posts);
	}
	
	
	public List<FileSQL>  findByOwnerSQL(User u) {
		List<FileSQL> files = new ArrayList<FileSQL>();
		RestTemplate restTemplate = new RestTemplate();
	     
	    final String baseUrl = "http://"+usersURL+"/fileAPI/owner/"+u.getNif();
	    URI uri;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return files;
		}
	     
	    ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
	     
	    //Verify request succeed
	   if(result.getStatusCodeValue() == 200) {
		   Type fileListType = new TypeToken<ArrayList<FileSQL>>(){}.getType();

		   files = (List<FileSQL>) gson.fromJson(result.getBody(), fileListType);
	   }
	   return files;
	}
	
	public Boolean isMaxFilesReached(User u) {
		List<FileSQL> files = findByOwnerSQL(u);
		if(files.size() >= u.getQuantity()) return true;
		else return false;
	}
	
}

