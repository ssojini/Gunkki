package com.example.demo.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.AdminBoardSerivce;
import com.example.demo.service.FreeBoardService;
import com.example.demo.service.mypageService;
import com.example.demo.vo.FreeBoard;
import com.example.demo.vo.UserJoin;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/health")
@Slf4j
public class healthController {
	@Autowired HttpSession session;

	/* 다루한 */
	@Autowired
	private FreeBoardService fs;

	@GetMapping("/freeBoard")
	public String freeBoard(Model m, String bname) {
		if (bname != null) {
			List<FreeBoard> listFreeBoard = fs.getFreeBoardList(bname);
			if (bname != null)
				m.addAttribute("bname",bname);
			m.addAttribute("listFreeBoard", listFreeBoard);
		}
		return "html/freeBoard";
	}

	@PostMapping("/getListMap")
	@ResponseBody
	public List<Map<String, Object>> getListMap(Model m, String bname) {
		List<Map<String, Object>> listMap = fs.getListFreeBoardToListMap(bname);
		return listMap;
	}

	@GetMapping("/addFreeBoard")
	public String addFreeBoard(Model m, String bname) {
		m.addAttribute("bname", bname);
		return "html/addFreeBoard";
	}
	@PostMapping("/addFreeBoard")
	@ResponseBody
	public Map<String,Object> addFreeBoard(Model m, FreeBoard freeBoard) {
		Map<String,Object> map = new HashMap<>();
		System.out.println("freeBoard:"+freeBoard);
		map.put("result", fs.addFreeBoard(session,freeBoard));
		return map;
	}

	/* 다루한 */

	/* 현주 */
	@Autowired
	private mypageService mp_svc;

	@Autowired
	ResourceLoader resourceLoader;

	@GetMapping("/")
	@ResponseBody
	public String userlist() {
		return mp_svc.userlist().toString();

		// return svc.userinfo(userid).toString();
	}

	@GetMapping("/useredit/{userid}")
	public String addboardform(@PathVariable(value = "userid", required = false) String userid, Model m) {
		m.addAttribute("user", mp_svc.userinfo(userid));
		return "html/EditUser";
	}

	@PostMapping("/userEdit")
	@ResponseBody
	public Map<String, Object> useredit(@RequestParam("file") MultipartFile[] mfiles, HttpServletRequest request,
			UserJoin userjoin) {
		ServletContext context = request.getServletContext();
		String savePath = context.getRealPath("/WEB-INF/user_profile");
		log.info("savePath:   " + savePath);
		List<String> list = new ArrayList<>();

		log.info(mfiles[0].getOriginalFilename());

		try {
			for (int i = 0; i < mfiles.length; i++) {
				mfiles[i].transferTo(new File(savePath + "/" + mfiles[i].getOriginalFilename()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fname = mfiles[0].getOriginalFilename();
		log.info("fname:  " + fname);
		userjoin.setProfile(fname);
		Map<String, Object> map = new HashMap<>();
		// System.out.println("userid: "+ userid);
		map.put("edited", mp_svc.useredit(userjoin));
		System.out.println("SYSTEM:  " + mp_svc.useredit(userjoin));
		return map;

	}

	@GetMapping("/deleteuser/{userid}")
	public String deleteuser(@PathVariable(value = "userid", required = false) String userid, Model m) {
		m.addAttribute("user", mp_svc.userinfo(userid));
		return "html/DeleteUser";
	}
	/* 현주 */

	/* 종빈 */
	@GetMapping("/test")
	public String test(Model m) {
		return "html/test/test";
	}

	/* 종빈 */
	@GetMapping("/main")
	public String main1(Model m) {
		return "html/mainPage";
	}
	/* 종빈 */

	/*엘라 */
	@Autowired 
	private AdminBoardSerivce absvc;

	@GetMapping("/admin")
	public String admin()
	{
		return "html/admin/adminBoard";
	}

	@GetMapping("/writeAdmin")
	public String writeAdmin()
	{

		return "html/admin/writeBoard_admin";
	}

	@GetMapping("/qaList")
	public String qaList()
	{
		return null;
	}
}
