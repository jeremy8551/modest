package cn.org.expect.ssm.controller;

import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cn.org.expect.ioc.EasyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloControllerSample {

    @Autowired
    private ScriptEngine engine;

    @Autowired
    private EasyContext context;

    @RequestMapping("/helloWorld")
    @ResponseBody
    public String help() throws ScriptException, IOException {
        this.engine.eval("echo hello world!");
        return "success";
    }
}
