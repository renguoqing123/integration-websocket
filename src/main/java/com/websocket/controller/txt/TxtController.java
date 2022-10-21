package com.websocket.controller.txt;

import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.websocket.common.BaseRespMsg;
import com.websocket.common.ResponseResult;
import com.websocket.common.SysRespConstants;
import com.websocket.controller.txt.dto.TxtContext;
import com.websocket.dao.TxtDao;
import com.websocket.model.Txt;

@RestController
@RequestMapping(("/api/txt"))
public class TxtController {

	@Resource
	private TxtDao txtDao;
	
	@PostMapping("/save")
	public ResponseResult<String> save(@RequestBody TxtContext context) {
		Integer id = context.getId();
		if(null == id) {
			return new ResponseResult<>(SysRespConstants.TXT_SAVE_ERROR);
		}
		String text = context.getContext();
		if(null == text || BaseRespMsg.EMPTY.equals(text)) {
			return new ResponseResult<>(SysRespConstants.TXT_CONTEXT_ISNULL_ERROR);
		}
		Txt t = new Txt();
		t.setId(id);
		t.setContext(context.getContext());
		txtDao.save(t);
		return new ResponseResult<>(BaseRespMsg.EMPTY);
	}
	
	@GetMapping("/getContext")
	public ResponseResult<TxtContext> getContext(@RequestParam(required=false) Integer id) {
		TxtContext context = new TxtContext();
		Optional<Txt> optxt = txtDao.findById(id);
		if(optxt.isPresent()) {
			Txt t = optxt.get();
			context.setId(t.getId());
			context.setContext(t.getContext());
		}
		return new ResponseResult<>(context);
	}
}
