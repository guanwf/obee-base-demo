package com.obee.base.controller;

import com.obee.base.model.DemoVO;
import com.obee.base.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 22:22
 */
@Controller
@RequestMapping("/demo")
@Slf4j
public class DemoController extends BaseSingleController<DemoService, DemoVO> {

}
