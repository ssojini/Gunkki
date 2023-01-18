package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.interfaces.GoodsRepository;
import com.example.demo.service.ShopService;
import com.example.demo.vo.AddGoods_Att;
import com.example.demo.vo.Cart;
import com.example.demo.vo.Goods;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/shop")
@Slf4j
public class ShopController {
	@Autowired
	private ShopService svc;
	@Autowired
	private GoodsRepository repo;

	// 초기 테스트용
	@GetMapping("/")
	@ResponseBody
	public String index() {
		return "Shop Index";
	}

	// 초기 테스트 데이터 생성 메소드
	@GetMapping("/add")
	@ResponseBody
	public String testAdd() {
		// 상욱
		// 이미지 경로 확인해야함
		Goods goods = new Goods(0, "건강보조식품", "/images/goods.png", 5000, "건강에 좋음", "상품에 관한 상세설명", "", "카테고리1");
		Goods added = repo.save(goods);
		goods = new Goods(0, "영양제", "/images/goods.png", 1000, "건강에 좋음", "상품에 관한 상세설명", "", "카테고리2");
		added = repo.save(goods);

		// 이후 부분에 추가하여 수정 요망

		return "ShopController 초기 데이터 생성";
	}

	/*--------------------- 상욱 시작 ----------------------*/

	@GetMapping("/detail/{goodsnum}")
	public String detail(@PathVariable("goodsnum") int goodsnum, Model m) {
		// System.err.println(goodsnum);
		Goods goods = svc.getGoods(goodsnum);
		m.addAttribute("goods", goods);
		return "html/shop/goodsDetail";
	}

	@PostMapping("/cart")
	@ResponseBody
	public Map<String, Object> addCart(Cart cart) {
		// System.err.println(cart);
		Map<String, Object> map = svc.added(cart);
		return map;
	}

	@GetMapping("/cart")
	public String cart(Model m) {
		// 로그인 연동 후 주석해제
//		String userid = (String) session.getAttribute("userid");
		// 연동 전 userid 하드코딩
		String userid = "asdf";
		ArrayList<Cart> cartlist = svc.getCart(userid);
		// System.err.println(cartlist);
		// System.err.println(cartlist.get(0).getMainpic());
		m.addAttribute("cartlist", cartlist);
		return "html/shop/cart";
	}

	/*--------------------- 상욱 끝 ----------------------*/

	/* 현주 */
	
	@GetMapping("/ShopMainPage")
	public String ShopMainPage(Goods goods, AddGoods_Att att)
	{
		svc.mainpagegoods(goods, att);
		return "html/shop/ShopMain";
	}

	@GetMapping("/addgoods/{adminid}")
	public String addgoods(@PathVariable(value = "adminid", required = false) String adminid, Model m) {
		m.addAttribute("admin", svc.admininfo(adminid));
		return "html/shop/AddGoods";

	}
	
	@RequestMapping(value="/summer_image.do", produces = "application/json; charset=utf8")
	@ResponseBody
	public String uploadSummernoteImageFile(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request ) throws IOException {
		return svc.filesave(multipartFile);
	}

	@PostMapping("/addgoods")
	@ResponseBody
	public Map<String, Object> addgoods(@RequestParam("main_file") MultipartFile file,
			@RequestParam("goods_detail") String goods_detail, @RequestParam("file[]") List<String> fileList,
			HttpServletRequest request, Goods goods, AddGoods_Att att) {

		boolean b = svc.save(file, goods_detail, fileList, goods, att);
		Map<String, Object> map = new HashMap<>();
		map.put("added", b);

		return map;
	}

	/* 종빈 */
	@GetMapping("/main")
	public String main() {
		return "html/shop/main";
	}

	@GetMapping("/mypage/{userid}")
	public String ShopMyPage(@PathVariable String userid, Model m) {
		m.addAttribute("list", svc.mypagelist(userid));
		m.addAttribute("url", "shop/mypage/itemdetail");
		return "html/shop/mypage/mypage";

	}

	@GetMapping("/mypage/itemdetail/{ordernum}")
	public String ShopDetail(@PathVariable String ordernum, Model m) {
		// m.addAttribute("detail", svc.shopDetail(userid, itemid));
		// return "html/shop/mypage/detail";
		m.addAttribute("detail", svc.shopDetail(ordernum));
		return "html/shop/mypage/detail";
	}
}