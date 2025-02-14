package indi.webapp.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

@ComponentScan(basePackages = {"indi.webapp.springboot", "indi.jeremy.bean.*"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DirtiesContext
    @Test
    public void testhelp() throws Exception {
        // 创建虚拟请求，当前访问/books (这里故意写错，模拟匹配失败)
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/help");

        // 执行对应的请求
        ResultActions actions = mockMvc.perform(builder);

        // 设定预期值 与真实值进行比较，成功测试通过，失败测试失败
        // 定义本次调用的预期值
        StatusResultMatchers status = MockMvcResultMatchers.status();

        // 预计本次调用是成功的：状态200
        ResultMatcher ok = status.isOk();

        // 添加预期值到本次调用过程中，与真实执行结果进行匹配
        actions.andExpect(ok);
    }

    @DirtiesContext
    @Test
    public void testInc() throws Exception {
        // 创建虚拟请求，当前访问/books (这里故意写错，模拟匹配失败)
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/inc");

        // 执行对应的请求
        ResultActions actions = mockMvc.perform(builder);

        // 设定预期值 与真实值进行比较，成功测试通过，失败测试失败
        // 定义本次调用的预期值
        StatusResultMatchers status = MockMvcResultMatchers.status();

        // 预计本次调用是成功的：状态200
        ResultMatcher ok = status.isOk();

        // 添加预期值到本次调用过程中，与真实执行结果进行匹配
        actions.andExpect(ok);
    }
}
