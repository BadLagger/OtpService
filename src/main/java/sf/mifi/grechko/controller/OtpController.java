package sf.mifi.grechko.controller;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sf.mifi.grechko.dto.OtpRequestDto;
import sf.mifi.grechko.service.JwtService;

import java.text.ParseException;

@Controller
@RequiredArgsConstructor
public class OtpController {

    private final JwtService jwtService;

    @PostMapping("/otp-request")
    public String otpRequest(@RequestBody OtpRequestDto otpRequestDto, @RequestHeader("Authorization") String authHeader, Model model) {
        System.out.format("Login: %s\n", otpRequestDto.login());
        System.out.format("Type: %s\n", otpRequestDto.type());

        String token = authHeader.substring(7);

        System.out.format("Aut: %s\n", token);

        try {
            jwtService.parseToken(token);
        } catch (ParseException | JOSEException e) {
            System.out.format(e.getMessage());
            model.addAttribute("message", "Fail");
            return "success";
        }

        model.addAttribute("message", "Ok");

        return "success";
    }
}
