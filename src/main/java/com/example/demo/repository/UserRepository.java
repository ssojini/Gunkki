package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.vo.User;



public interface UserRepository extends JpaRepository<User, String> 
{

	User findByUseridAndPwd(String userid, String pwd);

	User findByUseridAndEmail(String userid, String email);

	User findByPhoneAndEmail(String phone, String email);

	/*----소진-----*/
	
	User findByNickname(String nickname);

	User findByUserid(String username);
}