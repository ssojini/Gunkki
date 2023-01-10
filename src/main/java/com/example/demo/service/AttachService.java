package com.example.demo.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.interfaces.AttachRepository;
import com.example.demo.vo.Attach;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AttachService {
	@Autowired
	private AttachRepository repo;
	
	public boolean saveAttach(HttpServletRequest request, MultipartFile[] files, Integer fbnum) {
		String savePath = request.getServletContext().getRealPath("WEB-INF/files/");
		try {
			for (int i = 0; i < files.length; i++) {
				String fname = files[i].getOriginalFilename();
				long fsize = files[i].getSize();
				Attach attach = new Attach();
				attach.setFbnum(fbnum);
				attach.setAname(fname);
				attach.setAsize(fsize);
				Attach saveAttach = repo.save(attach);
				files[i].transferTo(new File(savePath + "/" + saveAttach.getAnum() + "_" + saveAttach.getAname()));
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public ResponseEntity<Resource> donwloadAttach(HttpServletRequest request, Resource resource) {
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		String filename = resource.getFilename();
		filename = filename.substring(filename.indexOf("_")+1,filename.length());
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").body(resource);
	}
	
	public List<Attach> getListAttach(Integer fbnum) {
		List<Attach> listAttach = repo.findAllByFbnum(fbnum);
		return listAttach;
	}
}
