package antifraud.controller.user;

import antifraud.domain.service.CustomUserService;
import antifraud.exceptionhandler.GlobalExceptionHandlerAdvice;
import antifraud.rest.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static antifraud.controller.user.TestUtil.objectMapperHttpMessageConverter;
import static org.mockito.Mockito.mock;

public class UserControllerTest {

    private UserRequestBuilder userRequestBuilder;
    private CustomUserService customUserService;

    @BeforeEach
    void configureSystemUnderTest(){
        customUserService = mock(CustomUserService.class);

        UserController userTestedController = new UserController(customUserService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userTestedController)
                .setControllerAdvice(new GlobalExceptionHandlerAdvice())
                .setMessageConverters(objectMapperHttpMessageConverter())
                .build();
        userRequestBuilder = new UserRequestBuilder(mockMvc);
    }


}
