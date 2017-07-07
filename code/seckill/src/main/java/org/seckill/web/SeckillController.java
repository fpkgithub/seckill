package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author aboy
 *
 */

@Controller
@RequestMapping("/seckill") //url:/模块/资源/{id}/细分  /seckill/list
public class SeckillController
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	//model--数据
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model)
	{
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list", list);
		//list.jsp + model = ModelAndView
		return "list";//WEB-INF/jsp/"list".jsp
	}

	@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model)
	{

		if (seckillId == null)
		{
			//请求重定向
			return "redirect:/seckillId/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if (seckill == null)
		{
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}

	//ajax json

	@RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {
			"application/json;charset=UTF-8" })
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") long seckillId)
	{
		SeckillResult<Exposer> result;
		try
		{
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true, exposer);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {
			"application/json;charset=UTF-8" })
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") long seckillId,
			@PathVariable("md5") String md5,
			@CookieValue(value = "killPhone", required = false) Long userPhone)
	{
		SeckillResult<SeckillExecution> result;
		//sprinMVC valid
		if (userPhone == null)
		{
			result = new SeckillResult<SeckillExecution>(false, "未注册");
		}
		try
		{
			//存储过程调用
			SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId,
					userPhone, md5);
			result = new SeckillResult<SeckillExecution>(true, seckillExecution);
		}
		catch (RepeatKillException e1)
		{
			SeckillExecution execution = new SeckillExecution(seckillId,
					SeckillStateEnum.REPEAT_KILL);
			result = new SeckillResult<SeckillExecution>(true, execution);
		}
		catch (SeckillCloseException e1)
		{
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
			result = new SeckillResult<SeckillExecution>(true, execution);
		}
		catch (Exception e1)
		{
			logger.error(e1.getMessage(), e1);
			SeckillExecution execution = new SeckillExecution(seckillId,
					SeckillStateEnum.INNER_ERROR);
			result = new SeckillResult<SeckillExecution>(true, execution);
		}
		return result;
	}

	//获取当前时间
	@RequestMapping(value = "/time/now", method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time()
	{
		Date now = new Date();
		return new SeckillResult<Long>(true, now.getTime());
	}

}
