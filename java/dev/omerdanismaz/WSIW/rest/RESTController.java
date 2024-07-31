package dev.omerdanismaz.WSIW.rest;

import dev.omerdanismaz.WSIW.models.UserModel;
import dev.omerdanismaz.WSIW.services.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/REST")
public class RESTController
{
    private final IUserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/checkUserExistence")
    public RESTResponse checkUserExistencePOST(@RequestParam("requestData") String requestData)
    {
        UserModel searchedUser = userService.readUserByEmail(requestData);

        if(!searchedUser.getDbfUserEmail().equals("EMPTY"))
        {
            RESTResponse restResponse = new RESTResponse();
            restResponse.setResponseData("RESPONSE");
            return restResponse;
        }

        return null;
    }

    @PostMapping("/checkUserPassword")
    public RESTResponse checkUserPasswordPOST(@RequestParam("requestDataOne") String requestDataOne,
                                              @RequestParam("requestDataTwo") String requestDataTwo)
    {
        UserModel searchedUser = userService.readUserByEmail(requestDataOne);

        if(passwordEncoder.matches(requestDataTwo, searchedUser.getDbfUserPassword()))
        {
            RESTResponse restResponse = new RESTResponse();
            restResponse.setResponseData("RESPONSE");
            return restResponse;
        }

        return null;
    }
}
