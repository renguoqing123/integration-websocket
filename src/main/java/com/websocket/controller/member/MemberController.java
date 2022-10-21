package com.websocket.controller.member;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.websocket.common.BaseRespMsg;
import com.websocket.common.ResponseResult;
import com.websocket.common.SysRespConstants;
import com.websocket.controller.member.dto.Login;
import com.websocket.controller.member.dto.Register;
import com.websocket.dao.MemberDao;
import com.websocket.model.Member;
import com.websocket.util.CacheUtils;
import com.websocket.util.GuavaCacheUtil;
import com.websocket.util.VerifyCodeUtil;

@RestController
@RequestMapping("/api/member")
public class MemberController {

	@Autowired
	private MemberDao memberDao;
	
	@PostMapping(value="/login")
	public ResponseResult<Void> mlogin(@RequestBody Login login,HttpServletRequest request, HttpServletResponse response) {
		Member member = getMember(login.getUserName());
		if(null == member) {
			return new ResponseResult<>(SysRespConstants.MEMBER_NOT_EXISTS_ERROR);
		}
		if(null == login.getPassword()) {
			return new ResponseResult<>(SysRespConstants.MEMBER_PASSWORD_NOTNULL_ERROR);
		}
		if(!login.getPassword().equals(member.getPassword())) {
			return new ResponseResult<>(SysRespConstants.MEMBER_PASSWORD_ERROR);
		}
		HttpSession session = request.getSession();
		String oldVerifyCode = (String) session.getAttribute(CacheUtils.cachekey);
		if(null != login.getVcode()) {
			return new ResponseResult<>(SysRespConstants.MEMBER_VERIFYCODE_NOTNULL_ERROR);
		}
		if(null == oldVerifyCode) {
			return new ResponseResult<>(SysRespConstants.MEMBER_VERIFYCODE_INVALID_ERROR);
		}
		if(null != login.getVcode() && !oldVerifyCode.equals(login.getVcode())) {
			return new ResponseResult<>(SysRespConstants.MEMBER_VERIFYCODE_ERROR);
		}
		String sessionid = request.getSession().getId();
		GuavaCacheUtil memberCache = GuavaCacheUtil.builder().build().create(sessionid,30,TimeUnit.SECONDS);
		memberCache.put(sessionid, member);
		return new ResponseResult<>(SysRespConstants.SUCCESS);
	}
	
	@PostMapping("/register")
	public ModelAndView mRegister(@RequestBody Register register) {
		Member member = getMember(register.getUserName());
		ModelAndView modelAndView = new ModelAndView();
		if(null != member) {
			modelAndView.addObject(BaseRespMsg.ERROR, SysRespConstants.MEMBER_ISEXISTS_ERROR.getMessage());
			return modelAndView;
		}
		if(register.getPassword().equals(register.getOncePassword())) {
			modelAndView.addObject(BaseRespMsg.ERROR, SysRespConstants.MEMBER_ISEXISTS_ERROR.getMessage());
			return modelAndView;
		}
		Member m = new Member();
		m.setUserName(register.getUserName());
		m.setPassword(register.getPassword());
		m.setCreateDate(new Date());
		memberDao.save(m);
		modelAndView.addObject(BaseRespMsg.DEFAULT_SUCCESS_RESP_CODE, BaseRespMsg.DEFAULT_SUCCESS_RESP_MEMO);
		return modelAndView;
	}
	
	@PostMapping("/updatePwd")
	public ModelAndView updatePwd(@RequestBody Register register) {
		Member member = getMember(register.getUserName());
		ModelAndView modelAndView = new ModelAndView();
		if(null == member) {
			modelAndView.addObject(BaseRespMsg.ERROR, SysRespConstants.MEMBER_NOT_EXISTS_ERROR.getMessage());
			return modelAndView;
		}
		if(register.getPassword().equals(register.getOncePassword())) {
			modelAndView.addObject(BaseRespMsg.ERROR, SysRespConstants.MEMBER_ONCE_PASSWORD_ERROR.getMessage());
			return modelAndView;
		}
		member.setUserName(register.getUserName());
		member.setPassword(register.getPassword());
		member.setUpdateDate(new Date());
		member.setUpdateUser(member.getUserName());
		memberDao.save(member);
		modelAndView.addObject(BaseRespMsg.DEFAULT_SUCCESS_RESP_CODE, BaseRespMsg.DEFAULT_SUCCESS_RESP_MEMO);
		return modelAndView;
	}
	
	@PostMapping("/loginExit")
	public ModelAndView mloginExit(HttpSession session) {
		//删除session
		session.invalidate();
		return new ModelAndView("redirect:/login.html");
	}
	
	@GetMapping("/getVerifyCodeImage")
    public void getVerifyCodeImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置页面不缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String verifyCode = CacheUtils.createVerifyCode();
        
        System.out.println("验证码：" + verifyCode);
        // 将验证码放到HttpSession里面
        request.getSession().setAttribute(CacheUtils.cachekey, verifyCode);
        // 设置输出的内容的类型为JPEG图像
        response.setContentType("image/jpeg");
        BufferedImage bufferedImage = VerifyCodeUtil.generateImageCode(verifyCode, 90, 30, 3, true, Color.WHITE,
                Color.BLACK, null);
        // 写给浏览器
        ImageIO.write(bufferedImage, "JPEG", response.getOutputStream());
    }

	
	private Member getMember(String userName) {
		Member m = new Member();
		m.setUserName(userName);
		ExampleMatcher matcher = ExampleMatcher.matching()
				.withMatcher("user_name", GenericPropertyMatchers.exact());
		Example<Member> example = Example.of(m,matcher);
		Optional<Member> mm = memberDao.findOne(example);
		if(mm.isPresent()) {
			Member member = mm.get();
			return member;
		}
		return null;
	}
	
}
