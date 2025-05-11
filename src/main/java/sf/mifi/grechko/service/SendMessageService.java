package sf.mifi.grechko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sf.mifi.grechko.dto.OtpType;
import sf.mifi.grechko.entity.OtpCode;
import sf.mifi.grechko.entity.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMessageService {


    private void pushMock(String type, String destination, String message) {
        log.info("Mock for: {}", type);
        log.info("Destination: {}", destination);
        log.info("Message: {}", message);
    }

    private void pushToEmail(String destination, String message) {
        pushMock("email", destination, message);
    }

    private void pushToTelegram(String destination, String message) {
        pushMock("telegram", destination, message);
    }

    private void pushToSms(String destination, String message) {
        pushMock("SMS", destination, message);
    }

    private void pushToFile(String destination, String message) {
        log.info("Try save message to file: {}", destination);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destination, false))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            log.error("Can't save message to file: {}", e.getMessage());
            return;
        }
        log.info("Save message to file {} DONE!!!", destination);
    }

    public void send(OtpType type, OtpCode code) {
        User user = code.getUser();
        String message = generateMessage(code.getCode());
        switch(type) {
            case FILE -> pushToFile("sendToFile.txt", message);
            case EMAIL -> pushToEmail(user.getEmail(), message);
            case PHONE -> pushToSms(user.getPhone(), message);
            case TELEGRAM -> pushToTelegram(user.getTelegram(), message);
            default -> log.error("Unimplemented OtpType");
        }
    }

    private String generateMessage(List<Integer> code) {
        StringBuilder result = new StringBuilder("Your confirmation code: ");
        for (var number : code) {
            result.append(String.format("%d ", number));
        }
        return result.toString();
    }
}
