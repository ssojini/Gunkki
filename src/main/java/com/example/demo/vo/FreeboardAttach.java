package com.example.demo.vo;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FreeboardAttach {
	@Id
	@SequenceGenerator(name="FREEBOARD_ATTACH_ANUM_GEN",allocationSize=1,sequenceName="FREEBOARD_ATTACH_ANUM_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FREEBOARD_ATTACH_ANUM_GEN")
	private Integer anum;
	private Integer fbnum;
	private String aname;
	private long asize;
}
