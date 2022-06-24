package es.uv.adiez.endpoints;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.uv.adiez.domain.File;
import es.uv.adiez.domain.FileSQL;
import es.uv.adiez.domain.User;
import es.uv.adiez.security.TokenProvider;
import es.uv.adiez.services.FileService;
import es.uv.adiez.services.UserService;


@RestController
@RequestMapping("/api/files")
public class FileController {

	@Autowired FileService fs;
	@Autowired UserService us;
	@Autowired TokenProvider tp;
	
	@PostMapping()
	//@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity createPost(@RequestParam MultipartFile file, @RequestParam String title, 
										   @RequestParam String description, @RequestParam List<String> keywords,
										   @RequestParam int size,
										   HttpServletRequest request) throws IOException {
		String token = tp.getTokenFromHeader(request.getHeader(AUTHORIZATION));
		String username = tp.getUsernameFromToken(token);
		Optional<User> u = us.findByEmail(username);
		//Comprobamos que no se supere el maximo
		if(u != null && !fs.isMaxFilesReached(u.get())) {
			String content = new String(file.getBytes(), StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			List<Object> data = mapper.readValue(content, mapper.getTypeFactory().constructCollectionType(List.class, Object.class));
			File f = fs.create(new File(title, description, keywords, data, size));
	
			FileSQL fSQL = fs.createSQL(new FileSQL(f.getId(), null, u != null ? u.get() : null, 0, 0));
			
			return new ResponseEntity<>(f, HttpStatus.OK);
		}
		return new ResponseEntity<>("Max file quantity excedeed", null, HttpStatus.UNAUTHORIZED);
	} 
	
	@GetMapping("/producer")
	public List<File> getByOwner(HttpServletRequest request) {
		String token = tp.getTokenFromHeader(request.getHeader(AUTHORIZATION));
		String username = tp.getUsernameFromToken(token);
		Optional<User> u = us.findByEmail(username);
		if(u != null) {
			List<File> files = fs.findByOwner(u.get());
			return files;
		}
		return null;
	}
	
	@PutMapping("/{id}")
	public ResponseEntity updateFile(@PathVariable("id") String id, @RequestBody @Valid File file, HttpServletRequest request) {
		String token = tp.getTokenFromHeader(request.getHeader(AUTHORIZATION));
		String username = tp.getUsernameFromToken(token);
		Optional<User> u = us.findByEmail(username);
		if(u != null) {
			List<String> files = fs.findIdsByOwner(u.get());
			if(files.contains(id)) {
				return new ResponseEntity<>(fs.updateFile(id, file), HttpStatus.OK);
			}
			else return new ResponseEntity<>("File doesn't belong to producer", null, HttpStatus.UNAUTHORIZED);

		}
		return new ResponseEntity<>("User not found", null, HttpStatus.UNAUTHORIZED);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity deleteFile(@PathVariable("id") String id, HttpServletRequest request) {
		String token = tp.getTokenFromHeader(request.getHeader(AUTHORIZATION));
		String username = tp.getUsernameFromToken(token);
		Optional<User> u = us.findByEmail(username);
		if(u != null) {
			List<String> files = fs.findIdsByOwner(u.get());
			if(files.contains(id)) {
				fs.deleteFileSQL(id);
				fs.deleteFile(id);
				return new ResponseEntity<>("File deleted "+ id, HttpStatus.OK);
			}
			else return new ResponseEntity<>("File doesn't belong to producer", null, HttpStatus.UNAUTHORIZED);

		}
		return new ResponseEntity<>("User not found", null, HttpStatus.UNAUTHORIZED);
	}
	
	/*@GetMapping()
	public ResponseEntity<List<File>> getAll(HttpServletRequest request) {
		List<File> files = fs.findAll();
		return new ResponseEntity<>(files, HttpStatus.OK);
	}*/
	
}