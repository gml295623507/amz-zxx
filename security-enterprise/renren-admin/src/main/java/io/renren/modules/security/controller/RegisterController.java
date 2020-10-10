package io.renren.modules.security.controller;

import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;
import io.renren.common.utils.Result;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.security.dto.LoginDTO;
import io.renren.modules.security.service.CaptchaService;
import io.renren.modules.sys.dto.SysUserDTO;
import io.renren.modules.sys.service.SysRoleUserService;
import io.renren.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags="注册账号")
public class RegisterController {
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleUserService sysRoleUserService;

    @PostMapping("register")
    @ApiOperation(value = "注册")
    public Result register (HttpServletRequest request, @RequestBody LoginDTO register) {

        //效验数据
        ValidatorUtils.validateEntity(register);

        //验证码是否正确
        boolean flag = captchaService.validate(register.getUuid(), register.getCaptcha());
        if(!flag){
            return new Result().error(ErrorCode.CAPTCHA_ERROR);
        }

        //用户信息
        SysUserDTO user = sysUserService.getByUsername(register.getUsername());
        if (user != null) {
            throw new RenException(ErrorCode.DB_RECORD_EXISTS);
        } else {
            SysUserDTO userDTO = new SysUserDTO();
            userDTO.setUsername(register.getUsername());
            userDTO.setPassword(register.getPassword());
            userDTO.setStatus(1);
            userDTO.setRealName(register.getUsername());
            userDTO.setGender(2);
            List<Long> list = new ArrayList<>();
            list.add(Long.valueOf("1281471395251519489"));
            userDTO.setRoleIdList(list);
            sysUserService.save(userDTO);
        }

        return new Result();
    }
}
