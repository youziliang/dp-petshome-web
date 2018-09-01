package com.dp.petshome.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/applicationContext.xml", "classpath*:/spring/webmvc-config.xml" })
@Transactional(transactionManager = "transactionManager")
public class BaseJunitTest {

	@Test
	public void init() {

	}

}
